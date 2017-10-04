package com.cloudbanter.adssdk.ad.manager;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.ad.manager.images.CbImageManager;
import com.cloudbanter.adssdk.ad.model.CbDevice;
import com.cloudbanter.adssdk.ad.model.CbEvent;
import com.cloudbanter.adssdk.ad.model.CbSchedule;
import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;
import com.cloudbanter.adssdk.ad.repo.CbDatabase;
import com.cloudbanter.adssdk.ad.repo.DatabaseFactory;
import com.cloudbanter.adssdk.ad.util.CbSharedPreferences;

import java.util.Timer;
import java.util.TimerTask;

// TODO keep Cb default ads in rotator until startup complete
// TODO contexts should be getApplicationContext.

/**
 * @deprecated This class is part of the old ads system. It's highly recommended not to use it
 * anymore. It's kept as old parts of the app still uses it. Check new ads system in {@link
 * AdsInteractor}
 */
@Deprecated
public class AdvertManager {
  
  protected static String TAG = "AdvertManager";
  
  private static AdvertManager mAdManager;
  private AdSpaceChangeHandler mListener;
  private static AdRotator mAdRotator;
  private static CbDevice mDevice;
  
  private Context mContext;
  private static Object lock = new Object();
  private static Boolean firstRun = new Boolean(true);
  
  private static EventAggregator sEventAggregator;
  
  private static boolean enabled = false;

// delay between display of ads in milliseconds
// put in schedule
  
  // TODO move to config
  private static final boolean DEBUG = false; // BuildConfig.DEBUG; // MmsConfig.DEBUG;
  
  private static final long ADVERT_STARTUP_DELAY = DEBUG ? 1000 : 7000;
  private static final long ADVERT_RESTART_DELAY = DEBUG ? 1000 : 2000;
  private static final long ADVERT_ROTATE_TIME = DEBUG ? 700 : 5000;
  
  public AdvertManager() {
  }
  
  public AdvertManager(Context context) {
    if (null == mAdManager) {
      synchronized (lock) {
        mAdManager = this;
        mAdManager.mContext = context;
        mAdRotator = mAdManager.getRotator();
        sEventAggregator = EventAggregator.getInstance();
      }
    }
  }
  
  // TODO remove static storage
// TODO query from persistent storage
// TODO fix startup
  public static AdvertManager getInstance(Context context) {
    if (null == mAdManager) {
      synchronized (lock) {
        mAdManager = new AdvertManager(context);
      }
    }
    return mAdManager;
  }
  
  // only at startup ?
  public static void init(Context context) {
    if (null == mAdManager) {
      getInstance(context);
    }
    
    if (CbSharedPreferences.isRegistered(context)) {
      // get device data
      mDevice = CbDevice.restore(context);
      
      // load schedule from db
      if (mDevice != null) {
        mDevice.schedule = DatabaseFactory.getCbDatabase(context).getCbSchedule();
        
        if (null != mDevice.schedule) {
          syncNewSchedule(mDevice.schedule);
        }
      }
    } else {
      syncNewSchedule(PreloadSchedule.getSchedule());
    }
  }
  
  private AdRotator getRotator() {
    return new AdRotator();
  }
  
  public AdvertManager registerUi(AdSpaceChangeHandler handler) {
    mListener = handler;
    mAdRotator.restart();
    
    mAdRotator.onTimerAction();
    return mAdManager;
  }
  
  public void releaseUi() {
    mAdRotator.stop();
    this.mListener = null;
  }
  
  // notify ad manager listeners...
  private void notifyAdvertManagerListeners(CbScheduleEntry entry) {
    if (null != mAdManager && null != mAdManager.mListener) {
      Message msg = mListener.obtainMessage();
      msg.obj = entry;
      msg.sendToTarget();
    }
  }
  
  // bundle event triggers and cascade...
  public CbScheduleEntry onAckView(Context context, CbScheduleEntry entry) {
    entry.onAckView();
    return entry;
  }
  
  public CbScheduleEntry onAckClick(Context context, CbScheduleEntry entry) {
    entry.onAckClick();
    return entry;
  }
  
  static CbScheduleEntry lastEntry;
  
  public CbScheduleEntry onBannerView(Context context, CbScheduleEntry entry) {
    if (null == entry) {
      return entry;
    }
    
    if (entry != lastEntry) {
      entry.onBannerView();
      
      if (null == mDevice) {
        mDevice = CbDevice.restore(context);
      }
      
      // notify server  // repackage event/intent as method...
      if (null != mDevice) {
        sendBannerViewEvent(context, mDevice._id, entry);
      }
    }
    lastEntry = entry;
    return entry;
  }
  
  public CbScheduleEntry onAdTextView(Context context, CbScheduleEntry entry) {
    entry.onAdTextView();
    return entry;
  }
  
  public CbScheduleEntry onFullAdClick(Context context, CbScheduleEntry entry) {
    entry.onAckClick();
    return entry;
  }
  
  public CbScheduleEntry onFullAdView(Context context, CbScheduleEntry entry) {
    entry.onAckClick();
    return entry;
  }
  
  public CbScheduleEntry onBannerClick(Context context, CbScheduleEntry entry) {
    if (null == entry) {
      return null;
    }
    
    // update ad state
    entry.onBannerClick();
    
    // add to cb Central
    CloudbanterCentral.addItem(entry);
    entry.isSaved = true;
    
    removeItem(entry._id);
    
    if (null == mDevice) {
      mDevice = CbDevice.restore(context);
    }
    
    // notify server  // repackage event/intent as method...
    if (null != mDevice) {
      sendBannerClickEvent(context, mDevice._id, entry);
    } else {
      // RTE device
    }
    
    // callback to ui (
    return entry;
  }
  
  // TODO these should be in the CbRestService
  public void sendBannerViewEvent(Context context, String deviceId, CbScheduleEntry entry) {
    sendEvent(context, deviceId, CbEvent.EVENT_VIEW, CbEvent.SUBTYPE_AD_BANNER, entry._id);
  }
  
  // TODO these should be in the CbRestService
  public void sendBannerClickEvent(Context context, String deviceId, CbScheduleEntry entry) {
    sendEvent(context, deviceId, CbEvent.EVENT_CLICK, CbEvent.SUBTYPE_AD_BANNER, entry._id);
  }
  
  // TODO these should be in the CbRestService
// - event callback can contain update...
  public void sendEvent(Context context, String deviceID, String eventType, String subType,
                        String refID) {
//  CbEvent event = new CbEvent(deviceID, eventType, subType, refID);
//  Intent intent = CbRestService.getSendEventIntent(context, null, event);
//  mContext.startService(intent);
    if (eventType.equals(CbEvent.EVENT_CLICK)) {
      if (sEventAggregator == null) {
        sEventAggregator = EventAggregator.getInstance();
      } else {
        sEventAggregator.addClick(refID);
      }
    }
    if (eventType.equals(CbEvent.EVENT_VIEW)) {
      if (sEventAggregator == null) {
        sEventAggregator = EventAggregator.getInstance();
      } else {
        sEventAggregator.addView(refID);
      }
    }
    
  }
  
  // timer thing
// allows for ad to be viewed longer when attention is there and click happens
  public class AdRotator {
    Timer timer;
    TimerTask timerTask;
    private boolean skipOneStateChange = false;
    
    // on timer thing update ad space
    public void onTimerAction() {
      // should probably stop and restart on newAds event.
      if (!enabled) {
        Log.d(TAG, "Cb ad manager disabled");
        return;
      }
      if (AdBannerCollection.isEmpty()) {
        return;
      }
      
      // get next
      CbScheduleEntry entry = AdBannerCollection.getNext();
      mAdManager.notifyAdvertManagerListeners(entry);
    }
    
    public void start() {
      Log.d(TAG, "rotater start");
      if (!enabled) {
        Log.d(TAG, "Advert manager not enabled, canceling timer and returning");
        if (null != timer) {
          timer.cancel();
          timer.purge();
          timer = null;
        }
        return;
      }
      if (null != timer) {
        timer.cancel();
        timer.purge();
        timer = null;
      }
      if (null == mListener) {
        return;
      }
      timer = new Timer();
      timerTask = new TimerTask() {
        @Override
        public void run() {
          if (skipOneStateChange) {
            skipOneStateChange = false;
          } else {
            onTimerAction();
          }
        }
      };
      timer.scheduleAtFixedRate(timerTask, firstRun ? ADVERT_STARTUP_DELAY : ADVERT_RESTART_DELAY,
              ADVERT_ROTATE_TIME);
      if (firstRun) {
        firstRun = new Boolean(false);
      }
    }
    
    public void pause() {
      skipOneStateChange = true;
    }
    
    public void stop() {
      Log.d(TAG, "rotater stop");
      if (null != timer) {
        timer.cancel();
        timer.purge();
      }
      timer = null;
    }
    
    public void restart() {
      Log.d(TAG, "Restarting ad manager");
      stop();
      start();
    }
    
    public boolean isRunning() {
      return null != timer;
    }
  }
  
  
  public static void startAdRotator() {
    mAdRotator.start();
  }
  
  public static void stopAdRotator() {
    mAdRotator.stop();
  }
  
  // called when new schedule is downloaded
  public static void syncNewSchedule(CbSchedule sched) {
    if (null == sched) {
      return;
    }
    
    stopAdRotator();
    
    // sync with rotator collection
    CbSchedule newSched = AdBannerCollection.updateWithNewSchedule(sched);
    
    // if no ads to display load the preload set.
    if (newSched.isEmpty()) {
      newSched = PreloadSchedule.getSchedule();
      AdBannerCollection.addAll(newSched);
      Log.d(TAG, "updating from preload schedule entries: " + newSched.entries.length);
    }
    
    // sync to database
    CbDatabase database = DatabaseFactory.getCbDatabase(CbAdsSdk.getApplication());
    database.upsert(newSched);
    
    // download additional images
    CbImageManager.syncSchedule(CbAdsSdk.getApplication());
    
    // sync with cloudbanter central
    // CloudbanterCentral.addSaved(newSched);
    
    startAdRotator();
  }
  
  public static void removeItem(String id) {
    // TODO should be off main thread...
    AdBannerCollection.removeItem(id);
    // TODO move to schedule controller
    CbDatabase database = DatabaseFactory.getCbDatabase(CbAdsSdk.getApplication());
    CbSchedule tmpSched = database.getCbSchedule();
    if (tmpSched == null) {
      return;
    }
    tmpSched.removeEntry(id);
    database.upsert(tmpSched);
    
    if (tmpSched.isEmpty()) {
      if (tmpSched.isPrefs()) {
        syncNewSchedule(DefaultSchedule.getSchedule());
      } else if (tmpSched.isDefault()) {
        syncNewSchedule(PreloadSchedule.getSchedule());
      } else {
        syncNewSchedule(PreloadSchedule.getSchedule());
      }
    }
  }
  
  public static boolean isEnabled() {
    return enabled;
  }
  
  public static void setEnabled(boolean enabled) {
    Log.d(TAG,
            "Setting advert manager state to: " + enabled + " previous: " + AdvertManager.enabled);
    AdvertManager.enabled = enabled;
    mAdRotator.restart();
  }
}

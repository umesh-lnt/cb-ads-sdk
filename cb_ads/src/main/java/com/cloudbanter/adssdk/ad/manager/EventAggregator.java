package com.cloudbanter.adssdk.ad.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.ad.model.CbEvent;
import com.cloudbanter.adssdk.ad.model.CbEventSummary;
import com.cloudbanter.adssdk.ad.service.CbRestService;
import com.cloudbanter.adssdk.ad.util.CbSharedPreferences;
import com.cloudbanter.adssdk.util.ScheduleUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created by Ugljesa Jovanovic (jovanovic.ugljesa@gmail.com) on 16-Aug-2016.
 */
public class EventAggregator {
  public static final String TAG = EventAggregator.class.getSimpleName();

  public static final String ALARM_ACTION = "com.cloudbanter.mms.EVENT_AGGREGATOR_ALARM";

  private static EventAggregator sInstance;
  private Context mContext;
  private String mDeviceId;
  private Map<String, Integer> mAdvertIdToClickCountMap;
  private Map<String, Integer> mAdvertIdToViewCountMap;
  private Map<String, Integer> mAdvertIdToWebClickCountMap;
  private MessageStatistics mMessageStatistics;

  private Tracker mTracker;


  private TelephonyManager mTelephonyManager;
  private AlarmManager mAlarmManager;


  public static EventAggregator getInstance() {
    if (sInstance == null) {
      Log.e(TAG, "Event aggregator not initialized");
      return null;
    }
    return sInstance;
  }

  private EventAggregator(Context context, String deviceId) {
    Log.d(TAG, "Creating event aggregator");
    mContext = context;
    mDeviceId = deviceId;
    Map<String, Integer> clickAggregateMap = CbSharedPreferences.getClickAggregate(mContext);
    if (clickAggregateMap == null) {
      Log.d(TAG, "No persisted click aggregate map, creating new one");
      mAdvertIdToClickCountMap = new HashMap<>();
    } else {
      Log.d(TAG, "Restored click map, size: " + clickAggregateMap.size());
      mAdvertIdToClickCountMap = clickAggregateMap;
    }
    Map<String, Integer> viewAggregateMap = CbSharedPreferences.getViewAggregate(mContext);
    if (viewAggregateMap == null) {
      Log.d(TAG, "No persisted view aggregate map, creating new one");
      mAdvertIdToViewCountMap = new HashMap<>();
    } else {
      Log.d(TAG, "Restored view map, size: " + viewAggregateMap.size());
      mAdvertIdToViewCountMap = viewAggregateMap;
    }
    Map<String, Integer> webClickAggregateMap = CbSharedPreferences.getWebClickAggregate(mContext);
    if (webClickAggregateMap == null) {
      Log.d(TAG, "No persisted web click aggregate map, creating new one");
      mAdvertIdToWebClickCountMap = new HashMap<>();
    } else {
      Log.d(TAG, "Restored web click map, size: " + webClickAggregateMap.size());
      mAdvertIdToWebClickCountMap = webClickAggregateMap;
    }
    MessageStatistics messageStatistics = CbSharedPreferences.getMessageStatistics(context);
    if (messageStatistics == null) {
      Log.d(TAG, "No message statistics persisted, creating new one");
      mMessageStatistics = new MessageStatistics(0, 0, 0, 0);
    } else {
      Log.d(TAG, "Restored message statistics: " + messageStatistics.smsSent +
              messageStatistics.smsReceived +
              messageStatistics.mmsSent +
              messageStatistics.mmsReceived
      );
      mMessageStatistics = messageStatistics;
    }

    mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
    mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    Log.d(TAG, "Setting alarm");
    Intent alarmIntent = new Intent(ALARM_ACTION);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, 0);
    mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 60 * 1000, 6 * AlarmManager.INTERVAL_HOUR,
            pendingIntent);

    mTracker = CbAdsSdk.getDefaultTracker();

  }

  public static synchronized void init(Context context, String deviceId) {
    if (sInstance != null) {
      Log.d(TAG, "Aggregator already created, returning");
      return;
    }
    sInstance = new EventAggregator(context, deviceId);
  }

  public synchronized void addView(String referenceId) {
    Integer viewCount = mAdvertIdToViewCountMap.get(referenceId);
    if (viewCount == null) {
      Log.d(TAG, "Adding new view count");
      mAdvertIdToViewCountMap.put(referenceId, 1);
    } else {
      viewCount++;
      Log.d(TAG, "New view count: " + viewCount + " reference id: " + referenceId);
      mAdvertIdToViewCountMap.put(referenceId, viewCount);
    }
    updatePersistedCounters();
  }

  public synchronized void addClick(String referenceId) {
    Integer clickCount = mAdvertIdToClickCountMap.get(referenceId);
    if (clickCount == null) {
      Log.d(TAG, "Adding new click count");
      mAdvertIdToClickCountMap.put(referenceId, 1);
    } else {
      clickCount++;
      Log.d(TAG, "New click count: " + clickCount);
      mAdvertIdToClickCountMap.put(referenceId, clickCount);
    }
    mTracker.send(new HitBuilders.EventBuilder()
            .setCategory("Ad")
            .setAction("Ad click")
            .build()
    );
    updatePersistedCounters();
  }

  public synchronized void addWebClick(String referenceId) {
    Integer webClickCount = mAdvertIdToWebClickCountMap.get(referenceId);
    if (webClickCount == null) {
      Log.d(TAG, "Adding new click count");
      mAdvertIdToWebClickCountMap.put(referenceId, 1);
    } else {
      webClickCount++;
      Log.d(TAG, "New click count: " + webClickCount);
      mAdvertIdToWebClickCountMap.put(referenceId, webClickCount);
    }
    mTracker.send(new HitBuilders.EventBuilder()
            .setCategory("Ad")
            .setAction("Full screen ad click")
            .build()
    );
    updatePersistedCounters();
  }

  public synchronized void resetCounters() {
    mAdvertIdToViewCountMap = new HashMap<>();
    mAdvertIdToClickCountMap = new HashMap<>();
    mAdvertIdToWebClickCountMap = new HashMap<>();
    mMessageStatistics.smsSent = 0;
    mMessageStatistics.smsReceived = 0;
    mMessageStatistics.mmsSent = 0;
    mMessageStatistics.mmsReceived = 0;
    updatePersistedCounters();
  }

  private void updatePersistedCounters() {
    CbSharedPreferences.setViewAggregate(mContext, mAdvertIdToViewCountMap);
    CbSharedPreferences.setClickAggregate(mContext, mAdvertIdToClickCountMap);
    CbSharedPreferences.setWebClickAggregate(mContext, mAdvertIdToWebClickCountMap);
    CbSharedPreferences.setMessageStatistics(mContext, new MessageStatistics(
            mMessageStatistics.smsSent,
            mMessageStatistics.smsReceived,
            mMessageStatistics.mmsSent,
            mMessageStatistics.mmsReceived));
  }

  public void smsSent() {
    Log.d(TAG, "Sms sent logged");
    mMessageStatistics.smsSent++;
    mTracker.send(new HitBuilders.EventBuilder()
            .setCategory("Communication")
            .setAction("Sms sent")
            .build()
    );

    updatePersistedCounters();
  }

  public void multipleSmsSent(int count) {
    Log.d(TAG, "Multiple sms sent");
    mMessageStatistics.smsSent += count;
    mTracker.send(new HitBuilders.EventBuilder()
            .setCategory("Communication")
            .setAction("Sms sent")
            .build()
    );
    updatePersistedCounters();
  }

  public void smsReceived() {
    Log.d(TAG, "Sms received logged");
    mMessageStatistics.smsReceived++;
    mTracker.send(new HitBuilders.EventBuilder()
            .setCategory("Communication")
            .setAction("Sms received")
            .build()
    );

    updatePersistedCounters();
  }

  public void mmsSent() {
    Log.d(TAG, "Mms sent logged");
    mMessageStatistics.mmsSent++;
    mTracker.send(new HitBuilders.EventBuilder()
            .setCategory("Communication")
            .setAction("Mms sent")
            .build()
    );

    updatePersistedCounters();
  }

  public void multipleMmsSent(int count) {
    Log.d(TAG, "Multiple mms sent: " + count);

  }

  public void mmsReceived() {
    Log.d(TAG, "Mms received logged");
    mMessageStatistics.mmsReceived++;
    mTracker.send(new HitBuilders.EventBuilder()
            .setCategory("Communication")
            .setAction("Mms received")
            .build()
    );
    updatePersistedCounters();
  }

  public static class MessageStatistics {
    public int smsSent;
    public int smsReceived;
    public int mmsSent;
    public int mmsReceived;

    public MessageStatistics() {
    }

    public MessageStatistics(int smsSent, int smsReceived, int mmsSent, int mmsReceived) {
      this.smsSent = smsSent;
      this.smsReceived = smsReceived;
      this.mmsSent = mmsSent;
      this.mmsReceived = mmsReceived;
    }
  }


  public static class EventAggregatorAlarmReceiver extends BroadcastReceiver {
    public static final String TAG = EventAggregatorAlarmReceiver.class.getSimpleName();


    @Override
    public void onReceive(Context context, Intent intent) {
      Log.d(TAG, "Alarm receiver onReceive");
      if (intent != null && intent.getAction() != null) {
        if (sInstance == null) {
          Log.d(TAG, "Aggregator not initialized in alarm, initializing");
          init(context, CbSharedPreferences.getCbDevice(context));
        }
        //Piggyback ad mix percentage on this timer
        if (ExternalAdManager.getInstance() != null) {
          ExternalAdManager.getInstance().updatePercentage();
        }

        Set<String> allReferenceIdSet = new HashSet<>(sInstance.mAdvertIdToClickCountMap.keySet());
        allReferenceIdSet.addAll(new HashSet<>(sInstance.mAdvertIdToViewCountMap.keySet()));
        allReferenceIdSet.addAll(new HashSet<>(sInstance.mAdvertIdToWebClickCountMap.keySet()));
        CbEventSummary[] cbEventSummaries = new CbEventSummary[allReferenceIdSet.size()];
        Log.d(TAG, "Number of references: " + cbEventSummaries.length);
        int position = 0;
        for (String key : allReferenceIdSet) {
          Log.d(TAG, "Reference: " + key);
          CbEventSummary cbEventSummary = new CbEventSummary();
          cbEventSummary.reference = key;

          Integer views = sInstance.mAdvertIdToViewCountMap.get(key);
          cbEventSummary.views = views == null ? 0 : views;
          Log.d(TAG, "Views: " + cbEventSummary.views);

          Integer clicks = sInstance.mAdvertIdToClickCountMap.get(key);
          cbEventSummary.clicks = clicks == null ? 0 : clicks;
          Log.d(TAG, "Clicks: " + cbEventSummary.clicks);

          Integer webClicks = sInstance.mAdvertIdToWebClickCountMap.get(key);
          cbEventSummary.webClicks = webClicks == null ? 0 : webClicks;
          Log.d(TAG, "Web clicks: " + cbEventSummary.webClicks);

          cbEventSummaries[position++] = cbEventSummary;

        }
        if (sInstance.mDeviceId == null) {
          Log.d(TAG, "Device id was null, checking again");
          sInstance.mDeviceId = CbSharedPreferences.getCbDeviceId(context);
          if (sInstance.mDeviceId == null) {
            Log.e(TAG, "No device id! Returning");
            return;
          }
        }


        CbEvent event = new CbEvent(
                sInstance.mDeviceId,
                sInstance.mMessageStatistics.smsSent,
                sInstance.mMessageStatistics.smsReceived,
                sInstance.mMessageStatistics.mmsSent,
                sInstance.mMessageStatistics.mmsReceived,
                cbEventSummaries,
                sInstance.mTelephonyManager.getSimCountryIso().toUpperCase());
        Log.d(TAG, "Sending event summary");
        Intent sendEventIntent = CbRestService.getSendEventIntent(context, null, event);
        context.startService(sendEventIntent);

        ScheduleUtils.generateSchedule(context);

      } else {
        Log.e(TAG, "Intent or action was null");
      }

    }

  }


}

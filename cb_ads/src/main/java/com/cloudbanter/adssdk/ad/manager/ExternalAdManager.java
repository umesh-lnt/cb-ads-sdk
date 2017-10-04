package com.cloudbanter.adssdk.ad.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.cloudbanter.adssdk.ProxyManager;
import com.cloudbanter.adssdk.ad.model.AdMix;
import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;
import com.cloudbanter.adssdk.ad.service.CbCommunicationManager;
import com.cloudbanter.adssdk.ad.service.http.retrofit.CloudbanterEndpoints;
import com.cloudbanter.adssdk.ad.service.http.retrofit.EndpointOperationCallback;
import com.cloudbanter.adssdk.ad_exchange.views.CloudbanterAdView;
import com.mopub.mobileads.AdViewController;
import com.mopub.mobileads.MoPubView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Ugljesa Jovanovic (ionspinf@gmail.com) on 23-Aug-2016
 */
public class ExternalAdManager {

  public static final String TAG = ExternalAdManager.class.getSimpleName();

  private static final String SELECTED_PREFERENCE = "custom_pref_";

  private static int mopubLimit = 18;
  private static int cbLimit = 4;

  private static final int CLOUDBANTER_ADS = 0;
  private static final int MOPUB_ADS = 1;

  private boolean CB_ENABLED = true;
  private boolean MOPUB_ENABLED = true;

  private int moPubAdShownCounter;
  private int cbAdShownCounter;

  private static ExternalAdManager instance;

  private WeakReference<CloudbanterAdView> currentAdViewReference;
  private WeakReference<View> currentCbViewReference;
  private LinearLayout temporaryPartentHolderView;
  private Context context;

  private String gender;
  private String age;
  private String location;
  private List<String> iabCategoriesSelected;
  private Handler handler;

  String currentSource;

  private int failureCounter = 0;

  private int currentPlatform = CLOUDBANTER_ADS;

  private SharedPreferences sharedPreferences;
  private boolean keywordsPopulated = false;

  private BannerGrabber bannerGrabber;

  private EventAggregator eventAggregator;

  private boolean noConnection;
  private CbCommunicationManager.NetworkListener networkListener;

  private ExternalAdManager(Context context) {
    this.context = context;
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    handler = new Handler(Looper.getMainLooper());
    bannerGrabber = BannerGrabber.getInstance();
    eventAggregator = EventAggregator.getInstance();
    prepareKeywords();

    networkListener = new CbCommunicationManager.NetworkListener() {
      @Override
      public void onNetworkStatusChanged(boolean isNetworkAvailable, int networkType) {
        Log.d(TAG, "Network status changed! Is available: " + isNetworkAvailable);
        Log.d(TAG, "Network type: " + CbCommunicationManager.networkTypeToName(networkType));
        noConnection = !isNetworkAvailable;
        if (noConnection) {
          cyclePlatform();
        }
        ProxyManager.restoreProxyAfterLeavingWebView();
      }
    };
    noConnection = !CbCommunicationManager.getInstance().isNetworkAvailable();
    CbCommunicationManager.getInstance().registerNetworkListener(networkListener);
    if (!noConnection) {
      updatePercentage();
    }

  }

  private void handleNetworkChange(boolean networkAvailable) {
    Log.d(TAG, "Handling network change");
  }

  public static synchronized void init(Context context) {
//        MMLog.setLogLevel(2);
    instance = new ExternalAdManager(context);
  }

  public void updatePercentage() {
    CloudbanterEndpoints.getInstance().getAdMix(new EndpointOperationCallback<AdMix>() {
      @Override
      public void onSuccess(AdMix result) {
        if (result != null) {
          Log.d(TAG, "Got mix: CB: " + result.getConsole() + " MP: " + result.getAdExchange());
          cbLimit = result.getConsole();
          mopubLimit = result.getAdExchange();
        } else {
          Log.d(TAG, "AdMix was null!");
        }
      }

      @Override
      public void onFailure(String errorMessage, Exception exception) {
        Log.d(TAG, "Failed: " + errorMessage);
        if (exception != null) {
          Log.e(TAG, "", exception);
        }
      }
    });
  }

  private void addSeparatorIfNotEmpty(StringBuilder keywordsStringBuilder) {
    if (keywordsStringBuilder.length() != 0) {
      keywordsStringBuilder.append(",");
    }
  }

  public static synchronized ExternalAdManager getInstance() {
    return instance;
  }

  public void initializeAdView() {
    Log.d(TAG, "Initializing mopub view");
    if (currentAdViewReference == null || currentAdViewReference.get() == null) {
      Log.e(TAG, "Ad view was not set up");
      return;
    }
    CloudbanterAdView currentAdView = currentAdViewReference.get();
    currentAdView.init();
  }

  public void shutdownMoPub() {
    Log.d(TAG, "Shutting down MoPub");
    if (currentAdViewReference != null && currentAdViewReference.get() != null) {
      currentAdViewReference.get().onDestroy();
    } else {
      Log.w(TAG, "Empty reference when trying to destroy");
    }
  }

  public void setAdView(CloudbanterAdView adView) {

    if (currentAdViewReference != null) {
      if (currentAdViewReference.get() == adView) {
        Log.w(TAG, "Setting current mo pub view to same mo pub view!");
        resumeAdViewCycle(adView);

        return;
      }
    }
    if (!keywordsPopulated) {
      prepareKeywords();
    }
    currentAdViewReference = new WeakReference<CloudbanterAdView>(adView);
    initializeAdView();
  }

  public void setCbView(View cbView) {
    Log.d(TAG, "Setting cb view: " + cbView);
    if (currentCbViewReference != null) {
      if (currentCbViewReference.get() == cbView) {
        Log.w(TAG, "Same cb view as the one already set");
      }
    }
    currentCbViewReference = new WeakReference<View>(cbView);
  }

  public void showAds() {
    Log.d(TAG, "Show ads called");
    if (currentPlatform == MOPUB_ADS && currentAdViewReference != null &&
            currentAdViewReference.get() != null) {
      CloudbanterAdView adView = currentAdViewReference.get();
      if (currentCbViewReference != null && currentCbViewReference.get() != null) {
        currentCbViewReference.get().setVisibility(View.GONE);
        AdvertManager.setEnabled(false);
      }
      adView.setVisibility(View.VISIBLE);
      adView.onResume();
    }
    if (currentPlatform == CLOUDBANTER_ADS) {
      if (currentCbViewReference != null) {
        if (currentCbViewReference.get() != null) {

          if (currentAdViewReference != null && currentAdViewReference.get() != null) {
            currentAdViewReference.get().setVisibility(View.GONE);
          }
          if (currentAdViewReference != null && currentAdViewReference.get() != null) {
            currentAdViewReference.get().setVisibility(View.GONE);
            pauseMoPubRefresh(currentAdViewReference.get());
          }
          AdvertManager.setEnabled(true);
          currentCbViewReference.get().setVisibility(View.VISIBLE);
        } else {
          Log.e(TAG, "Empty reference for cb view in show ads");
        }
      } else {
        Log.e(TAG, "Null reference for cb view in show ads");
      }
    }

  }


  private void prepareKeywords() {
    iabCategoriesSelected = new LinkedList<>();
    Resources resources = context.getResources();

    Map<String, ?> defaultSharedPreferences = sharedPreferences.getAll();
    if (!defaultSharedPreferences.containsKey("attrib_pref_gender")) {
      Log.d(TAG, "Not yet registered");
      return;
    } else {
      Log.d(TAG, "Populating keywords");
      keywordsPopulated = true;
    }
    for (String key : defaultSharedPreferences.keySet()) {
      Log.d(TAG, "Key: " + key + " value: " + defaultSharedPreferences.get(key));
      if (key.equals("attrib_pref_gender")) {
        gender = (String) defaultSharedPreferences.get(key);
        Log.d(TAG, "Gender: " + gender);
      }
      if (key.equals("attrib_pref_age")) {
        age = (String) defaultSharedPreferences.get(key);
        Log.d(TAG, "Age: " + age);
      }
      if (key.equals("attrib_pref_location")) {
        location = (String) defaultSharedPreferences.get(key);
        Log.d(TAG, "Location: " + location);
      }
      if (key.startsWith(SELECTED_PREFERENCE)) {
        Boolean selected = sharedPreferences.getBoolean(key, false);
        if (selected) {
          Log.d(TAG, "Preference selected: " + key);
          int categoryId = Integer.parseInt(key.substring(key.lastIndexOf('_') + 1));
          Log.d(TAG, "Id: " + categoryId);
          String categoryName = resources.getString(
                  resources.getIdentifier("title_cb_pref_list_" + categoryId, "string",
                          context.getPackageName())
          );
          Log.d(TAG, "Category name: " + categoryName);
          iabCategoriesSelected.add(categoryName);

        }
      }
    }
    StringBuilder keywordsStringBuilder = new StringBuilder();
    keywordsStringBuilder.append("m_age:" + age + ",");
    keywordsStringBuilder.append("m_gender:" + (gender.equals("male") ? "M" : "F") + ",");
    for (String category : iabCategoriesSelected) {
      keywordsStringBuilder.append(category + ",");
    }
    keywordsStringBuilder.delete(keywordsStringBuilder.length() - 1,
            keywordsStringBuilder.length());
    Log.d(TAG, "Keywords: " + keywordsStringBuilder.toString());
    if (currentAdViewReference != null && currentCbViewReference.get() != null) {
      // FIXME: Manage keywords with CloudbanterAdView
      // currentAdViewReference.get().setKeywords(keywordsStringBuilder.toString());
    }
  }


  public String getGender() {
    if (gender == null) {
      prepareKeywords();
    }
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getAge() {
    if (age == null) {
      prepareKeywords();
    }
    return age;
  }

  public void setAge(String age) {
    this.age = age;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public List<String> getIabCategoriesSelected() {
    return iabCategoriesSelected;
  }

  public void setIabCategoriesSelected(List<String> iabCategoriesSelected) {
    this.iabCategoriesSelected = iabCategoriesSelected;
  }

  public String getCategoriesAsString() {
    StringBuilder stringBuilder = new StringBuilder();
    boolean first = true;
    for (String iabCategory : iabCategoriesSelected) {
      if (!first) {
        stringBuilder.append(", ");
      } else {
        first = false;
      }
      stringBuilder.append(iabCategory);
    }
    return stringBuilder.toString();
  }

  private void cyclePlatform() {
    noConnection = !CbCommunicationManager.getInstance().isNetworkAvailable();
    Log.d(TAG, "No connection?" + noConnection);
    if (noConnection) {
      Log.d(TAG, "No connection, running cb ads");
      currentPlatform = CLOUDBANTER_ADS;
      if (currentCbViewReference != null) {
        if (currentCbViewReference.get() != null) {

          if (currentAdViewReference != null && currentAdViewReference.get() != null) {
            currentAdViewReference.get().setVisibility(View.GONE);
            pauseMoPubRefresh(currentAdViewReference.get());
          }
          AdvertManager.setEnabled(true);
          currentCbViewReference.get().setVisibility(View.VISIBLE);
        } else {
          Log.e(TAG, "Cb view reference was empty");
        }
      } else {
        Log.e(TAG, "Cb ad view reference was null!");
      }
      moPubAdShownCounter = 0;
      return;
    }
    if (currentPlatform == MOPUB_ADS) {
      Log.d(TAG, "Switching from mopub to cb");
      currentPlatform = CLOUDBANTER_ADS;
      if (currentCbViewReference != null) {
        if (currentCbViewReference.get() != null) {

          if (currentAdViewReference != null && currentAdViewReference.get() != null) {
            currentAdViewReference.get().setVisibility(View.GONE);
            pauseMoPubRefresh(currentAdViewReference.get());
          }
          AdvertManager.setEnabled(true);
          currentCbViewReference.get().setVisibility(View.VISIBLE);
        } else {
          Log.e(TAG, "Cb view reference was empty");
        }
      } else {
        Log.e(TAG, "Cb ad view reference was null!");
      }
      moPubAdShownCounter = 0;
      return;
    }
    if (currentPlatform == CLOUDBANTER_ADS) {
      Log.d(TAG, "Switching from cb to mopub");

      currentPlatform = MOPUB_ADS;
      if (currentAdViewReference != null && currentAdViewReference.get() != null) {
        if (currentCbViewReference != null && currentCbViewReference.get() != null) {
          currentCbViewReference.get().setVisibility(View.GONE);
          AdvertManager.setEnabled(false);
        }
        initializeAdView();
        resumeAdViewCycle(currentAdViewReference.get());
        Log.d(TAG, "Asking MoPub to load ad");
        currentAdViewReference.get().setVisibility(View.VISIBLE);
      }
      cbAdShownCounter = 0;
    }
  }

  public boolean cbAdShown(CbScheduleEntry entry) {
    Log.d(TAG, "Reported cb ad showing. Entry: " + entry);
    Log.d(TAG, "Cb counter: " + cbAdShownCounter);
    if (cbAdShownCounter >= cbLimit) {
      Log.d(TAG, "View will be discarded because we are switching platforms");
      cyclePlatform();
      return false;
    }
    cbAdShownCounter++;
    return true;

  }

  private AdViewController getMoPubAdViewController(MoPubView moPubView) {
    Log.d(TAG, "Getting ad view controller");
    try {
      Field adViewControllerField = MoPubView.class.getDeclaredField("mAdViewController");
      adViewControllerField.setAccessible(true);
      return (AdViewController) adViewControllerField.get(moPubView);
    } catch (NoSuchFieldException e) {
      Log.e(TAG, "", e);
    } catch (IllegalAccessException e) {
      Log.e(TAG, "", e);
    }
    return null;
  }

  private void pauseMoPubRefresh(CloudbanterAdView adView) {
    Log.d(TAG, "Pausing ad view cycle");
    adView.onPause();
  }

  private void resumeAdViewCycle(final CloudbanterAdView adView) {
    Log.d(TAG, "Resuming ad view cycle");
    adView.onResume();
  }

  public Date getBirthdayDate() {
    Calendar calendar = new GregorianCalendar();
    calendar.roll(Calendar.YEAR, -30);
    Date date = calendar.getTime();
    return date;
  }

  public void refreshData() {
    init(context);
  }

  public void bypassForGoogleAdView(com.google.android.gms.ads.AdView adView) {
//        bannerGrabber.handleFavouritesGoogleBypass(adView);
  }

  public void pauseAds(CloudbanterAdView adView) {
    pauseMoPubRefresh(adView);
  }

  public void replaceAllViews(View replaceTargetView) {
    // TODO: What part of the life cycle of CloudbanterAdView should be here?
  }

  public void restoreViews() {
    if (temporaryPartentHolderView != null) {
      Log.d(TAG, "Got parent");

      CloudbanterAdView adView = currentAdViewReference.get();
      if (adView.getParent() != null) {
        Log.e(TAG, "Views were already attached");
        return;
      }
      int childCount = temporaryPartentHolderView.getChildCount();
      for (int i = 0; i < childCount; i++) {
        View child = temporaryPartentHolderView.getChildAt(i);
        Log.d(TAG, "Position: " + i + " type: " + child.getClass().getSimpleName());
        child.setVisibility(View.GONE);
      }
      temporaryPartentHolderView.removeAllViews();
      if (adView != null) {
        Log.d(TAG, "Restoring mopub view");
        temporaryPartentHolderView.addView(adView);
        adView.setVisibility(View.VISIBLE);
      }
      View cloudbanaterView = currentCbViewReference.get();
      if (cloudbanaterView != null) {
        Log.d(TAG, "Restoring cb view");
        temporaryPartentHolderView.addView(cloudbanaterView);
        cloudbanaterView.setVisibility(View.VISIBLE);
      }
      temporaryPartentHolderView.invalidate();
      childCount = temporaryPartentHolderView.getChildCount();
      for (int i = 0; i < childCount; i++) {
        View child = temporaryPartentHolderView.getChildAt(i);
        Log.d(TAG, "Position: " + i + " type: " + child.getClass().getSimpleName());
      }


    } else {
      Log.e(TAG, "Parent layout was null, can't restore views");
    }
  }
}

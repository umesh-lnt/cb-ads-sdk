package com.cloudbanter.adssdk.ad.util;

import android.content.Context;
import android.util.Log;

import com.cloudbanter.adssdk.ad.manager.EventAggregator;
import com.cloudbanter.adssdk.ad.manager.ExternalBannerGrab;
import com.cloudbanter.adssdk.model.ad_blender.AdsConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class CbSharedPreferences extends SharedPreferenceAccessor {
  public static final String TAG = CbSharedPreferences.class.getSimpleName();
  
  private static final Random RANDOM = new Random();
  
  private static final String THE_THING = "pref_cb_the_thing";
  private static final String CB_DEVICE_ID_PREF = "pref_cb_device_id";
  private static final String CB_DEVICE_PREF = "pref_cb_device";
  private static final String CB_PHONE_NUMBER_PREF = "pref_cb_phone_number";
  private static final String CB_IS_REGISTERED_PREF = "pref_cb_is_registered";
  private static final String CB_AUTH_TOKEN = "pref_cb_auth_token";
  private static final String CB_IS_USER_PREFERENCES_SET = "pref_cb_preferences_set";
  private static final String CB_DETAILS_PHONE_NUMBER = "cb_settings_mobile";
  private static final String CB_IS_MY_DETAILS_SET = "pref_cb_has_my_details";
  private static final String CB_HAS_HOMESCREEN_SHORTCUT = "pref_cb_has_homescreen_shortcut";
  private static final String CB_IS_IN_DEMO_MODE = "pref_cb_is_in_demo_mode";
  private static final String CB_CLICK_AGGREGATE = "pref_cb_click_aggregate";
  private static final String CB_VIEW_AGGREGATE = "pref_cb_view_aggregate";
  private static final String CB_WEB_CLICK_AGGREGATE = "pref_cb_web_click_aggregate";
  private static final String CB_GRABBED_ADS = "grabbed_ads";
  private static final String CB_MESSAGE_STATISTICS = "message_statistics";
  private static final String CB_SELECTED_CUSTOM_COLORS = "selected_custom_colors";
  private static final String CB_SELECTED_INBOX_SEGMENTS = "selected_inbox_segments";
  private static final String CB_CUSTOM_KEYWORDS = "custom_keywords";
  private static final String CB_INBOX_SEGMENTATION_SAMPLE = "inbox_segmentation_sample";
  private static final String CB_ADS_CONFIG = "ads_config";
  
  private static final Gson sGson = new Gson();
  
  public static String getTheThing(Context context) {
    return getStringPreference(context, THE_THING, "default no thing");
  }
  
  public static void setTheThing(Context context, String theThing) {
    setStringPreference(context, THE_THING, theThing);
  }
  
  public static String getCbDeviceId(Context context) {
    return getStringPreference(context, CB_DEVICE_ID_PREF, null);
  }
  
  public static void setCbDeviceId(Context context, String deviceId) {
    setStringPreference(context, CB_DEVICE_ID_PREF, deviceId);
  }
  
  public static String getCbPhoneNumber(Context context) {
    return getStringPreference(context, CB_PHONE_NUMBER_PREF, null);
  }
  
  public static void setCbPhoneNumber(Context context, String phoneNumber) {
    setStringPreference(context, CB_PHONE_NUMBER_PREF, phoneNumber);
  }
  
  public static boolean isRegistered(Context context) {
    return getBooleanPreference(context, CB_IS_REGISTERED_PREF, false);
  }
  
  public static void setRegistered(Context context, Boolean value) {
    setBooleanPreference(context, CB_IS_REGISTERED_PREF, value);
  }
  
  public static void setCbDevice(Context context, String device) {
    setStringPreference(context, CB_DEVICE_PREF, device);
  }
  
  public static String getCbDevice(Context context) {
    return getStringPreference(context, CB_DEVICE_PREF, null);
  }
  
  // TODO secure auth token
  public static void setAuthToken(Context context, String token) {
    setStringPreference(context, CB_AUTH_TOKEN, token);
  }
  
  public static String getAuthToken(Context context) {
    return getStringPreference(context, CB_AUTH_TOKEN, null);
  }
  
  public static boolean isUserPreferencesSet(Context context) {
    return getBooleanPreference(context, CB_IS_USER_PREFERENCES_SET, false);
  }
  
  /* used to indicate preferences have been set at registration time - should be no "unset" ops */
  public static void setUserPreferencesSet(Context context, Boolean value) {
    setBooleanPreference(context, CB_IS_USER_PREFERENCES_SET, value);
  }
  
  public static String getCbDetailsMobileNumber(Context context) {
    return getStringPreference(context, CB_DETAILS_PHONE_NUMBER, null);
  }
  
  public static void setCbDetailsMobileNumber(Context context, String phoneNumber) {
    setStringPreference(context, CB_DETAILS_PHONE_NUMBER, phoneNumber);
  }
  
  public static boolean isMyDetailsSet(Context context) {
    return getBooleanPreference(context, CB_IS_MY_DETAILS_SET, false);
  }
  
  public static void setMyDetailsSet(Context context, Boolean value) {
    setBooleanPreference(context, CB_IS_MY_DETAILS_SET, value);
  }
  
  public static boolean hasHomeScreenShortcut(Context context) {
    return getBooleanPreference(context, CB_HAS_HOMESCREEN_SHORTCUT, false);
  }
  
  public static void setHomeScreenShortcut(Context context, Boolean b) {
    setBooleanPreference(context, CB_HAS_HOMESCREEN_SHORTCUT, b);
  }
  
  public static void setIsInDemoMode(Context context, Boolean value) {
    setBooleanPreference(context, CB_IS_IN_DEMO_MODE, value);
  }
  
  public static boolean getIsInDemoMode(Context context) {
    return getBooleanPreference(context, CB_IS_IN_DEMO_MODE, false);
  }
  
  public static void setClickAggregate(Context context, Map<String, Integer> clickAggregateMap) {
//    Log.d(TAG, "Updating click aggregate");
//    Log.d(TAG, "Click aggregate: " + sGson.toJson(clickAggregateMap));
    setStringPreference(context, CB_CLICK_AGGREGATE, sGson.toJson(clickAggregateMap));
  }
  
  public static Map<String, Integer> getClickAggregate(Context context) {
    String clickAggregateJson = getStringPreference(context, CB_CLICK_AGGREGATE, "");
//    Log.d(TAG, "Click aggregate json: " + clickAggregateJson);
    if (!clickAggregateJson.equals("")) {
      Type type = new TypeToken<Map<String, Integer>>() {
      }.getType();
      return sGson.fromJson(clickAggregateJson, type);
    } else {
      return null;
    }
  }
  
  public static void setWebClickAggregate(Context context,
                                          Map<String, Integer> webClickAggregateMap) {
    setStringPreference(context, CB_WEB_CLICK_AGGREGATE, sGson.toJson(webClickAggregateMap));
  }
  
  public static Map<String, Integer> getWebClickAggregate(Context context) {
    String webClickAggregateJson = getStringPreference(context, CB_WEB_CLICK_AGGREGATE, "");
    if (!webClickAggregateJson.equals("")) {
      Type type = new TypeToken<Map<String, Integer>>() {
        
      }.getType();
      return sGson.fromJson(webClickAggregateJson, type);
    } else {
      return null;
    }
  }
  
  public static void setViewAggregate(Context context, Map<String, Integer> viewAggregateMap) {
//    Log.d(TAG, "Updating view aggregate");
//    Log.d(TAG, "View aggregate: " + sGson.toJson(viewAggregateMap));
    setStringPreference(context, CB_VIEW_AGGREGATE, sGson.toJson(viewAggregateMap));
  }
  
  public static Map<String, Integer> getViewAggregate(Context context) {
    String viewAggregateJson = getStringPreference(context, CB_VIEW_AGGREGATE, "");
//    Log.d(TAG, "View aggregate json: " + viewAggregateJson);
    if (!viewAggregateJson.equals("")) {
      Type type = new TypeToken<Map<String, Integer>>() {
      }.getType();
      return sGson.fromJson(viewAggregateJson, type);
    } else {
      return null;
    }
  }
  
  public static void setMessageStatistics(Context context,
                                          EventAggregator.MessageStatistics messageStatistics) {
    setStringPreference(context, CB_MESSAGE_STATISTICS, sGson.toJson(messageStatistics));
  }
  
  public static EventAggregator.MessageStatistics getMessageStatistics(Context context) {
    String messageStatisticsJson = getStringPreference(context, CB_MESSAGE_STATISTICS, "");
    Log.d(TAG, "Message statistics json: " + messageStatisticsJson);
    if (!messageStatisticsJson.equals("")) {
      return sGson.fromJson(messageStatisticsJson, EventAggregator.MessageStatistics.class);
    } else {
      return null;
    }
  }
  
  public static void setGrabbedAds(Context context, List<ExternalBannerGrab> grabbedAds) {
    setStringPreference(context, CB_GRABBED_ADS, sGson.toJson(grabbedAds));
  }
  
  public static List<ExternalBannerGrab> getGrabbedAds(Context context) {
    String grabbedAdsJson = getStringPreference(context, CB_GRABBED_ADS, "");
    if (!grabbedAdsJson.equals("")) {
      Type type = new TypeToken<List<ExternalBannerGrab>>() {
        
      }.getType();
      return sGson.fromJson(grabbedAdsJson, type);
    } else {
      return null;
    }
  }
  
  private static void clearThreadsSelection(Set<Long> storedThreadsIds,
                                            Set<Long> selectedThreadIds) {
    for (long id : selectedThreadIds) {
      storedThreadsIds.remove(id);
    }
  }

  public static void setCustomKeywordsPreferences(Context context, List<String> keywords) {
    setStringPreference(context, CB_CUSTOM_KEYWORDS, sGson.toJson(keywords));
  }
  
  public static List<String> getCustomKeywordsPreferences(Context context) {
    String selectedSegments = getStringPreference(context, CB_CUSTOM_KEYWORDS, "");
    if (!selectedSegments.equals("")) {
      Type type = new TypeToken<List<String>>() {
        
      }.getType();
      return sGson.fromJson(selectedSegments, type);
    } else {
      return new ArrayList<>(0);
    }
  }
  
  public static boolean isInboxSegmentationSampleShown(Context context) {
    return getBooleanPreference(context, CB_INBOX_SEGMENTATION_SAMPLE, false);
  }
  
  public static void setInboxSegmentationSampleShown(Context context, boolean shown) {
    setBooleanPreference(context, CB_INBOX_SEGMENTATION_SAMPLE, shown);
  }
  
  public static void setAdsConfig(Context context, AdsConfig result) {
    setStringPreference(context, CB_ADS_CONFIG, sGson.toJson(result));
  }
  
  public static AdsConfig getAdsConfig(Context context, AdsConfig defaultValue) {
    String selectedSegments = getStringPreference(context, CB_ADS_CONFIG, "");
    if (!selectedSegments.equals("")) {
      Type type = new TypeToken<AdsConfig>() {
        
      }.getType();
      return sGson.fromJson(selectedSegments, type);
    } else {
      return defaultValue;
    }
  }
}

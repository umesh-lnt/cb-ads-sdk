package com.cloudbanter.adssdk.ad.ui.util;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.cloudbanter.adssdk.ad.model.CbPreferenceData;
import com.cloudbanter.adssdk.ad.model.CbUserInfo;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created by eric on 3/3/16.
 */
public class CbPreferenceScreen {
  private static String TAG = CbPreferenceScreen.class.getSimpleName();
  private static final String ATTRIBUTED_GROUP = "attrib_pref_";
  private static final String SETTINGS_PREFERENCE = "cb_settings_";
  private static final String SELECTED_PREFERENCE = "custom_pref_";
  
  public static CbPreferenceData getPreferenceData(Context context) {
    CbPreferenceData prefData = new CbPreferenceData();
    CbUserInfo userData = new CbUserInfo();
    
    Map<String, ?> allPreferences = PreferenceManager.getDefaultSharedPreferences(context).getAll();
    for (String key : allPreferences.keySet()) {
      if (key.contains(ATTRIBUTED_GROUP)) {
        prefData.addAttrPref(getPref(key), allPreferences.get(key));
      }
      if (key.contains(SETTINGS_PREFERENCE)) {
        if (key.equalsIgnoreCase("cb_settings_name")) {
          userData.name = (String) allPreferences.get(key);
        }
        if (key.equalsIgnoreCase("cb_settings_email")) {
          userData.email = (String) allPreferences.get(key);
        }
      }
      if (key.contains(SELECTED_PREFERENCE)) {
        prefData.addPref(getCustomPreferenceFromKey(key));
      }
    }
    return prefData;
    
  }
  
  // on completion update the server.
  public static CbPreferenceData getPrefData(PreferenceScreen prefs) {
    CbPreferenceData prefData = new CbPreferenceData();
    CbUserInfo userData = new CbUserInfo();
    
    prefData = loopPrefData(prefData, userData, prefs);
    String placeId = prefs.getSharedPreferences().getString("placeId", "");
    prefData.addAttrPref("placeId", placeId);
    // TODO add language to preferences.
    String language = getIso632Language(Locale.getDefault());
    prefData.addAttrPref("language", language);
    return prefData;
  }
  
  private static String getIso632Language(Locale locale) {
    // FIXME: Need to be compared against a ISO 632 standard table
    if (isLocale(locale, "in")) {
      return "id";
    }
    return locale.getLanguage();
  }
  
  private static boolean isLocale(Locale locale, String expected) {
    return locale.getLanguage().equals(new Locale(expected).getLanguage());
  }
  
  private static CbPreferenceData loopPrefData(CbPreferenceData prefData, CbUserInfo userData,
                                               PreferenceGroup prefs) {
    
    for (int i = 0; i < prefs.getPreferenceCount(); i++) {
      Preference p = prefs.getPreference(i);
      if (p instanceof PreferenceGroup) {
        prefData = loopPrefData(prefData, userData, (PreferenceGroup) p);
      } else {
        if (isAttributedGroup(p)) {
          prefData.addAttrPref(getPref(p.getKey()), getValue(p));
        } else if (isUserData(p)) {
          setUserDataValue(userData, p);
        } else if (isCustomPref(p) && isSelectedPref(p)) {
          prefData.addPref(getCustomPreference(p));
        } else {
          ; // ignore
        }
      }
    }
    return prefData;
  }
  
  //- Entry super class? (no - Entry is a type)
  public static boolean isAttributedGroup(Preference p) {
    String s = (String) p.getKey();
    return null != s
            && (s).contains(ATTRIBUTED_GROUP)
            && !s.contains("postcode") // TODO remove to send postcode to server.
            ;
  }
  
  public static boolean isUserData(Preference p) {
    String s = p.getKey();
    return null != s
            && s.contains(SETTINGS_PREFERENCE)
            && !s.contains(
            "mobile")  // hack to fix gw requirement for mobile number on preferences / details
            // screen...
            ;
  }
  
  public static boolean isSelectedPref(Preference p) {
    String s = p.getKey();
    return null != s
            && s.contains(SELECTED_PREFERENCE);
  }
  
  public static boolean isCustomPref(Preference p) {
    return p instanceof CheckBoxPreference && ((CheckBoxPreference) p).isChecked();
  }
  
  public static String getPref(String key) {
    Log.d(TAG, "pref " + key);
    return null == key ? null : (key.substring(key.lastIndexOf('_') + 1));
  }
  
  public static String getCustomPreference(Preference p) {
    Log.d(TAG, "custom pref " + p.getTitle() + " key: " + p.getKey());
    String key = p.getKey();
    return getCustomPreferenceFromKey(key);
  }
  
  @NonNull
  private static String getCustomPreferenceFromKey(String key) {
    String preferenceNumberString = key.substring(key.lastIndexOf('_') + 1);
    Log.d(TAG, "PreferenceNumberString: " + preferenceNumberString);
    Integer preferenceNumber = Integer.parseInt(preferenceNumberString);
    String IABCategory = "IAB" + (preferenceNumber + 1);
    return IABCategory;
  }
  
  
  public static Object getValue(Preference p) {
    if (TextUtils.isEmpty(p.getSummary())) {
      return null;
    } else {
      Log.d(TAG, "pref value: " + p.getSummary());
      return p.getSummary().toString().toLowerCase();
    }
  }
  
  
  public static CbUserInfo setUserDataValue(CbUserInfo ud, Preference p) {
    String k = p.getKey();
    if (p.getSummary() == null) {
      Log.e(TAG, "Field had empty summary!");
      return ud;
    }
    if ("cb_settings_name".equalsIgnoreCase(k)) {
      ud.name = p.getSummary().toString();
    } else if ("cb_settings_email".equalsIgnoreCase(k)) {
      ud.email = p.getSummary().toString().toLowerCase();
    } else {
      Log.d(TAG, "unexpected userinfo " + k + ": " + p.getSummary().toString());
    }
    return ud;
  }
  
  public static CbUserInfo getUserData(PreferenceScreen prefs) {
    // TODO pull date from prefsdata
    CbUserInfo userInfo = new CbUserInfo();
    userInfo.name = "test_name";
    // data.addAttrPref("age", String.valueOf(new Date().getTime() - ( DateUtils.YEAR_IN_MILLIS *
    // 5 )));  // milliseconds GMT -  N years ago // Sync with js apis
    userInfo.age = new Date().toGMTString();
    userInfo.email = "test@test.email";
    return userInfo;
  }
  
  public static CbPreferenceData getPrefDataQ(PreferenceScreen prefs) {
    CbPreferenceData data = new CbPreferenceData();
    
    data.addAttrPref("location", "test_location");
    data.addAttrPref("postcode", "test_postcode");
    
    return data;
  }
  
}

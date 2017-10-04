package com.cloudbanter.adssdk.ad.ui.util;

import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by eric on 3/2/16.
 */
public class PreferencesValidator {

  public static final String TAG = PreferencesValidator.class.getSimpleName();

  // TODO move pref to Config
  private static final int minPrefCountRequired = 1;
  private static int pc;


  public static boolean isPrefsValid(Preference p) {
    pc = 0;

    boolean checkPrefsResult = checkPrefs(p);
    Log.d(TAG, "Pc: " + pc + " Pref valid: " + checkPrefsResult);
    return checkPrefsResult &&
            pc >= minPrefCountRequired ? true : false;
  }

  private static boolean checkPrefs(Preference p) {
    if (p instanceof PreferenceGroup) {
      PreferenceGroup pGrp = (PreferenceGroup) p;
      int count = pGrp.getPreferenceCount();
      for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
        Preference pi = pGrp.getPreference(i);
        if (!checkPrefs(pi)) {
          return false;
        }
      }
    } else {
      return prefValid(p);
    }
    return true;
  }

  private static boolean prefValid(Preference p) {
    if (p.getKey().contains("attrib_pref_") || p.getKey().contains("cb_settings_")) {
      return !TextUtils.isEmpty(p.getSummary());
    } else if (p instanceof CheckBoxPreference && ((CheckBoxPreference) p).isChecked()) {
      pc++;
    }
    return true;
  }

  public static boolean ageLocationValid(PreferenceScreen ps) {
    String value;
    int age;
    if (TextUtils.isEmpty((value = ps.getSharedPreferences().getString("attrib_pref_age", "")))
            || !value.matches("\\d+")
            || (age = Integer.valueOf(value)) < 1
            || age > 130) {
      return false;
    }
    return true;
  }

  public static boolean isPrefsEmpty(PreferenceScreen ps) {
    pc = 0;
    return checkPrefs(ps) &&
            pc == 0;
  }
}

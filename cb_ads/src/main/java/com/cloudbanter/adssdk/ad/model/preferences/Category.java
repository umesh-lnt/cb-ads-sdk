package com.cloudbanter.adssdk.ad.model.preferences;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.cloudbanter.adssdk.R;


/**
 * Created by Antonio on 5/2/17.
 */
public enum Category {
  
  ARTS_ENTERTAINMENT("custom_pref_edittext_0", R.string.title_cb_pref_list_0,
          R.drawable.ic_arts_entertainment),
  
  AUTOMOTIVE("custom_pref_edittext_1", R.string.title_cb_pref_list_1, R.drawable.ic_automotive),
  
  BUSINESS("custom_pref_edittext_2", R.string.title_cb_pref_list_2, R.drawable.ic_business),
  
  CAREERS("custom_pref_edittext_3", R.string.title_cb_pref_list_3, R.drawable.ic_career),
  
  EDUCATION("custom_pref_edittext_4", R.string.title_cb_pref_list_4, R.drawable.ic_education),
  
  FAMILY_PARENTING("custom_pref_edittext_5", R.string.title_cb_pref_list_5,
          R.drawable.ic_family_parenting),
  
  HEALTH_FITNESS("custom_pref_edittext_6", R.string.title_cb_pref_list_6,
          R.drawable.ic_health_fitness),
  
  FOOD_DRINKING("custom_pref_edittext_7", R.string.title_cb_pref_list_7, R.drawable.ic_food_drink),
  
  HOBBIES_INTERESTS("custom_pref_edittext_8", R.string.title_cb_pref_list_8,
          R.drawable.ic_hobbies_interests),
  
  HOME_GARDEN("custom_pref_edittext_9", R.string.title_cb_pref_list_9, R.drawable.ic_home_garden),
  
  LAW_POLITICS("custom_pref_edittext_10", R.string.title_cb_pref_list_10,
          R.drawable.ic_law_government),
  
  NEWS("custom_pref_edittext_11", R.string.title_cb_pref_list_11, R.drawable.ic_newspaper),
  
  PERSONAL_FINANCE("custom_pref_edittext_12", R.string.title_cb_pref_list_12,
          R.drawable.ic_finances),
  
  SOCIETY("custom_pref_edittext_13", R.string.title_cb_pref_list_13, R.drawable.ic_society),
  
  SCIENCE("custom_pref_edittext_14", R.string.title_cb_pref_list_14, R.drawable.ic_science),
  
  PETS("custom_pref_edittext_15", R.string.title_cb_pref_list_15, R.drawable.ic_pets),
  
  SPORTS("custom_pref_edittext_16", R.string.title_cb_pref_list_16, R.drawable.ic_sports),
  
  STYLE_FASHION("custom_pref_edittext_17", R.string.title_cb_pref_list_17,
          R.drawable.ic_style_fashion),
  
  TECHNOLOGY_COMPUTING("custom_pref_edittext_18", R.string.title_cb_pref_list_18,
          R.drawable.ic_technology),
  
  TRAVEL("custom_pref_edittext_19", R.string.title_cb_pref_list_19, R.drawable.ic_travel),
  
  REAL_ESTATE("custom_pref_edittext_20", R.string.title_cb_pref_list_20, R.drawable.ic_real_estate),
  
  SHOPPING("custom_pref_edittext_21", R.string.title_cb_pref_list_21, R.drawable.ic_shopping),
  
  RELIGION_SPIRITUALITY("custom_pref_edittext_22", R.string.title_cb_pref_list_22,
          R.drawable.ic_religion),
  
  CUSTOM(null, R.string.custom_categories, R.drawable.ic_help);
  
  /** Category key for shared preferences **/
  private final String key;
  
  /** Displayed name **/
  @StringRes
  private final int stringRes;
  
  /** Icon res **/
  @DrawableRes
  private final int iconRes;
  
  Category(String key, int stringRes, int iconRes) {
    this.key = key;
    this.stringRes = stringRes;
    this.iconRes = iconRes;
  }
  
  public String getKey() {
    return key;
  }
  
  public int getStringRes() {
    return stringRes;
  }
  
  public int getIconRes() {
    return iconRes;
  }
}

package com.cloudbanter.adssdk.ad.model;

public class CbAtomicUserInfo extends AModel<CbAtomicUserInfo> {

  /** User's name **/
  private String name;

  /** User's email **/
  private String email;

  /** User's age **/
  private String age;

  /** User's preferences **/
  private CbPreferenceData preferences;

  /**
   * Creates an atomic user info given a {@link CbUserInfo} and his {@link CbPreferenceData}
   *
   * @param userInfo
   *         User info to be parsed
   * @param preferences
   *         Preferences to be added
   */
  public CbAtomicUserInfo(CbUserInfo userInfo, CbPreferenceData preferences) {
    this.name = userInfo.name;
    this.email = userInfo.email;
    this.age = userInfo.age;
    this.preferences = preferences;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *         the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * @param email
   *         the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * @return the age
   */
  public String getAge() {
    return age;
  }

  /**
   * @param age
   *         the age to set
   */
  public void setAge(String age) {
    this.age = age;
  }

  /**
   * @return the preferences
   */
  public CbPreferenceData getPreferences() {
    return preferences;
  }

  /**
   * @param preferences
   *         the preferences to set
   */
  public void setPreferences(CbPreferenceData preferences) {
    this.preferences = preferences;
  }

  public String toJson() {
    return gson.toJson(this, CbAtomicUserInfo.class);
  }

  public CbAtomicUserInfo fromJson(String json) {
    return (CbAtomicUserInfo) gson.fromJson(json, CbAtomicUserInfo.class);
  }
}

package com.cloudbanter.adssdk.ad.model;

/**
 * Atomic registration result
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 23/1/17
 */
public class CbAtomicRegistrationResult {

  /** Device info **/
  private CbDevice device;

  /** User info **/
  private CbUserInfo userInfo;

  /** User's Preferences **/
  private CbPreferenceData preferences;

  public CbAtomicRegistrationResult(CbDevice device, CbUserInfo userInfo,
                                    CbPreferenceData preferences) {
    this.device = device;
    this.userInfo = userInfo;
    this.preferences = preferences;
  }

  /**
   * @return the device
   */
  public CbDevice getDevice() {
    return device;
  }

  /**
   * @param device
   *         the device to set
   */
  public void setDevice(CbDevice device) {
    this.device = device;
  }

  /**
   * @return the userInfo
   */
  public CbUserInfo getUserInfo() {
    return userInfo;
  }

  /**
   * @param userInfo
   *         the userInfo to set
   */
  public void setUserInfo(CbUserInfo userInfo) {
    this.userInfo = userInfo;
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
}

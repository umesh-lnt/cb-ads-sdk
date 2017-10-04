package com.cloudbanter.adssdk.ad.model;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.google.gson.annotations.SerializedName;

/**
 * Atomic registration for device, user and preferences data
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 23/1/17
 */
public class CbAtomicRegistration extends AModel<CbAtomicRegistration> {

  /** Build info **/
  private String buildInfo = CbAdsSdk.sVersionName;

  /** Device's phone number **/
  private String phoneNumber;

  /** Mobile operator **/
  @SerializedName("mobile_operator")
  private CbMobileOperator mobileOperator;

  /** Atomic user info **/
  private CbAtomicUserInfo userInfo;

  public CbAtomicRegistration(CbDevice device, CbUserInfo userInfo, CbPreferenceData preferences) {
    this.buildInfo = device.buildInfo;
    this.phoneNumber = device.phoneNumber;
    this.mobileOperator = device.mobile_operator;
    this.userInfo = new CbAtomicUserInfo(userInfo, preferences);
  }

  public String toJson() {
    return gson.toJson(this, CbAtomicRegistration.class);
  }

  /**
   * @return the buildInfo
   */
  public String getBuildInfo() {
    return buildInfo;
  }

  /**
   * @param buildInfo
   *         the buildInfo to set
   */
  public void setBuildInfo(String buildInfo) {
    this.buildInfo = buildInfo;
  }

  /**
   * @return the phoneNumber
   */
  public String getPhoneNumber() {
    return phoneNumber;
  }

  /**
   * @param phoneNumber
   *         the phoneNumber to set
   */
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  /**
   * @return the mobileOperator
   */
  public CbMobileOperator getMobileOperator() {
    return mobileOperator;
  }

  /**
   * @param mobileOperator
   *         the mobileOperator to set
   */
  public void setMobileOperator(CbMobileOperator mobileOperator) {
    this.mobileOperator = mobileOperator;
  }

  /**
   * @return the userInfo
   */
  public CbAtomicUserInfo getUserInfo() {
    return userInfo;
  }

  /**
   * @param userInfo
   *         the userInfo to set
   */
  public void setUserInfo(CbAtomicUserInfo userInfo) {
    this.userInfo = userInfo;
  }
}

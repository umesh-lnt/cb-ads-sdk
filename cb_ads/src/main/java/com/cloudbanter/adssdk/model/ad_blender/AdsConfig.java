package com.cloudbanter.adssdk.model.ad_blender;


import com.cloudbanter.adssdk.ad.model.AdMix;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model class for ads configuration
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio</a>
 * @since 8/16/17
 */
public class AdsConfig {
  
  /** AdNetworks configuration data **/
  @SerializedName("ad_networks")
  List<AdNetworkConfig> adNetworkConfigs;
  
  /** Rotation time in secs **/
  @SerializedName("rotation_secs")
  private Integer rotationInSecs;
  
  /** Profile & Preferences delay in days **/
  @SerializedName("profile_preferences_delay_secs")
  private Integer profilePreferencesDelayInSecs;
  
  /** Ad mix **/
  @SerializedName("mix")
  private AdMix adMix;
  
  /**
   * @return adNetworkConfigs
   */
  public List<AdNetworkConfig> getAdNetworkConfigs() {
    return adNetworkConfigs;
  }
  
  /**
   * @param adNetworkConfigs
   *         to be set
   */
  public AdsConfig setAdNetworkConfigs(
          List<AdNetworkConfig> adNetworkConfigs) {
    this.adNetworkConfigs = adNetworkConfigs;
    return this;
  }
  
  /**
   * @return rotationInSecs
   */
  public Integer getRotationInSecs() {
    return rotationInSecs;
  }
  
  /**
   * @param rotationInSecs
   *         to be set
   */
  public AdsConfig setRotationInSecs(Integer rotationInSecs) {
    this.rotationInSecs = rotationInSecs;
    return this;
  }
  
  /**
   * @return profilePreferencesDelayInSecs
   */
  public Integer getProfilePreferencesDelayInSecs() {
    return profilePreferencesDelayInSecs;
  }
  
  /**
   * @param profilePreferencesDelayInSecs
   *         to be set
   */
  public AdsConfig setProfilePreferencesDelayInSecs(Integer profilePreferencesDelayInSecs) {
    this.profilePreferencesDelayInSecs = profilePreferencesDelayInSecs;
    return this;
  }
  
  /**
   * @return adMix
   */
  public AdMix getAdMix() {
    return adMix;
  }
  
  /**
   * @param adMix
   *         to be set
   */
  public AdsConfig setAdMix(AdMix adMix) {
    this.adMix = adMix;
    return this;
  }
}

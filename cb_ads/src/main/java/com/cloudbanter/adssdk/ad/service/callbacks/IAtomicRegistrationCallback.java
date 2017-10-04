package com.cloudbanter.adssdk.ad.service.callbacks;


import com.cloudbanter.adssdk.ad.model.CbDevice;
import com.cloudbanter.adssdk.ad.model.CbPreferenceData;
import com.cloudbanter.adssdk.ad.model.CbUserInfo;

/**
 * Atomic registration callback to return the result
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 23/1/17
 */
public interface IAtomicRegistrationCallback extends ICallback {

  /**
   * Called when atomic registration has been succeeded.
   *
   * @param device
   *         The registered device
   * @param userInfo
   *         The registered user info
   * @param preferences
   *         The registered preferences
   */
  void onAtomicRegistrationSuccess(CbDevice device, CbUserInfo userInfo,
                                   CbPreferenceData preferences);

}

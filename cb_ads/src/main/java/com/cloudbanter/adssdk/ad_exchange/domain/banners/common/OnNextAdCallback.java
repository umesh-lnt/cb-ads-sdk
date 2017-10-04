package com.cloudbanter.adssdk.ad_exchange.domain.banners.common;


import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;

/**
 * On next ad callback
 * Created by Antonio on 5/13/17.
 */
public interface OnNextAdCallback {

  /**
   * On next ad to be shown
   *
   * @param entry
   *         Entry to be shown
   */
  void onNextAd(CbScheduleEntry entry);

  /**
   * Calledn when no ads are available
   */
  void onNoAdsAvailable();
}

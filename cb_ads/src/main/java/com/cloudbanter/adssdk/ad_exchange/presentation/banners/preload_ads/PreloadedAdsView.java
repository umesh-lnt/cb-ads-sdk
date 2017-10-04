package com.cloudbanter.adssdk.ad_exchange.presentation.banners.preload_ads;

import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;

/**
 * Expected view for preload ads management
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 10/3/17
 */
public interface PreloadedAdsView {

  /**
   * Shows the given ad
   *
   * @param entry
   *         Entry that contains the ad
   */
  void showAd(CbScheduleEntry entry);

  /**
   * No ads are available to be shown
   */
  void noAdsAvailable();
}

package com.cloudbanter.adssdk.ad_exchange.presentation.banners.preload_ads;

/**
 * Presenter for cloudbanter ads
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 10/3/17
 */
public interface PreloadedAdsPresenter {

  /**
   * Gets the next ad to be shown for the current view. If no ads where displayed and there are no
   * console ads to show, then the preloaded ads are returned
   *
   * @param wereAdsDisplayed
   *         Indicates if any ad has been displayed until the moment this method is requested
   */
  void getAd(boolean wereAdsDisplayed);

}

package com.cloudbanter.adssdk.ad_exchange.domain.banners.preload_ads;

import android.support.annotation.NonNull;

import com.cloudbanter.adssdk.ad_exchange.domain.banners.common.OnNextAdCallback;


/**
 * Interactor for preload ads management
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 10/3/17
 */
public interface PreloadedAdsInteractor {

  /**
   * Get next preloaded ads if available.
   *
   * @param callback
   *         Callback to return the results
   */
  void getNextAd(@NonNull OnNextAdCallback callback);

}

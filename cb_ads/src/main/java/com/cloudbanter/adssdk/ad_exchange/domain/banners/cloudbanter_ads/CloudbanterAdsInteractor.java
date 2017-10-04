package com.cloudbanter.adssdk.ad_exchange.domain.banners.cloudbanter_ads;

import android.support.annotation.NonNull;

import com.cloudbanter.adssdk.ad_exchange.domain.banners.common.OnNextAdCallback;

/**
 * Interactor for cloudbanter console ads management
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 10/3/17
 */
public interface CloudbanterAdsInteractor {

  /**
   * Get next cloudbanter console ad if available.
   *
   * @param callback
   *         Callback to return the results
   */
  void getNextAd(@NonNull OnNextAdCallback callback);

}

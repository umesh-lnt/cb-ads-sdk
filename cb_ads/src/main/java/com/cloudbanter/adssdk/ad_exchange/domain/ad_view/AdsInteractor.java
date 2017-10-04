package com.cloudbanter.adssdk.ad_exchange.domain.ad_view;

import android.support.annotation.Nullable;
import android.view.View;

/**
 * Ads interactor which is responsible to load and caches a pool of AdNetworks ads. This pool is
 * automatically filled with ad views. When an ad is requested, then its space is occupied by a new
 * one
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 28/2/17
 */
public interface AdsInteractor {

  /**
   * Polls one of the cached Ads. If there are no cached apps, then null is returned
   *
   * @return The cached ad or null if there's are no available ads
   */
  @Nullable
  View pollCachedAd();

}

package com.cloudbanter.adssdk.ad_exchange.domain.banners.cloudbanter_ads;

import android.content.Context;

/**
 * Factory for {@link CloudbanterAdsInteractor}
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 10/3/17
 */
public final class CloudbanterAdsInteractorFactory {

  /** {@link CloudbanterAdsInteractor} instance **/
  private static CloudbanterAdsInteractor mInstance;

  /** Private constructor to avoid instances **/
  private CloudbanterAdsInteractorFactory() {
  }

  /**
   * Requests the {@link CloudbanterAdsInteractor} instance
   *
   * @return {@link CloudbanterAdsInteractor} instance
   */
  public static CloudbanterAdsInteractor getInstance(Context context) {
    if (mInstance == null) {
      mInstance = new CloudbanterAdsInteractorImpl(context);
    }
    return mInstance;
  }

}

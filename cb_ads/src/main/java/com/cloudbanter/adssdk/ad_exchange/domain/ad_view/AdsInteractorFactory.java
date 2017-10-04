package com.cloudbanter.adssdk.ad_exchange.domain.ad_view;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Factory to build and maintain an {@link AdsInteractor} instance
 * <p>
 * NOTE: This should be perfectly done using dagger2 but due to the current project complexity,
 * this wasn't implemented
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 28/2/17
 */
public final class AdsInteractorFactory {

  /** Ads interactor instance **/
  private static AdsInteractor sAdsInteractor;

  /** Private to avoid instances **/
  private AdsInteractorFactory() {
  }

  /**
   * Requests an interactor instance
   *
   * @return {@link AdsInteractor} instance
   */
  @NonNull
  public static AdsInteractor getInstance(@NonNull Context context) {
    if (sAdsInteractor == null) {
      sAdsInteractor = new AdsInteractorImpl(context);
    }
    return sAdsInteractor;
  }

}

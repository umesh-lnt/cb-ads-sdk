package com.cloudbanter.adssdk.ad_exchange.domain.banners.preload_ads;

/**
 * Factory for {@link PreloadedAdsInteractor}
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 10/3/17
 */
public final class PreloadAdsInteractorFactory {

  /** {@link PreloadedAdsInteractor} instance **/
  private static PreloadedAdsInteractor mInstance;

  /** Private constructor to avoid instances **/
  private PreloadAdsInteractorFactory() {
  }

  /**
   * Requests the {@link PreloadedAdsInteractor} instance
   *
   * @return {@link PreloadedAdsInteractor} instance
   */
  public static PreloadedAdsInteractor getInstance() {
    if (mInstance == null) {
      mInstance = new PreloadedAdsInteractorImpl();
    }
    return mInstance;
  }

}

package com.cloudbanter.adssdk.ad_exchange.presentation.banners.preload_ads;

import android.support.annotation.NonNull;

import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;
import com.cloudbanter.adssdk.ad_exchange.domain.banners.common.OnNextAdCallback;
import com.cloudbanter.adssdk.ad_exchange.domain.banners.preload_ads.PreloadAdsInteractorFactory;
import com.cloudbanter.adssdk.ad_exchange.domain.banners.preload_ads.PreloadedAdsInteractor;


/**
 * Implementation of the {@link PreloadedAdsPresenter}
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 10/3/17
 */
public class PreloadedAdsPresenterImpl implements PreloadedAdsPresenter, OnNextAdCallback {

  /** Preload ads interactor **/
  private final PreloadedAdsInteractor mPreloadedAdsInteractor;

  /** Preload ads view **/
  private final PreloadedAdsView mView;

  /**
   * Constructor
   */
  public PreloadedAdsPresenterImpl(@NonNull PreloadedAdsView view) {
    this.mView = view;
    mPreloadedAdsInteractor = PreloadAdsInteractorFactory.getInstance();
  }

  @Override
  public void getAd(boolean wereAdsDisplayed) {
    if (!wereAdsDisplayed) {
      mPreloadedAdsInteractor.getNextAd(this);
    } else {
      mView.noAdsAvailable();
    }
  }

  @Override
  public void onNextAd(CbScheduleEntry entry) {
    mView.showAd(entry);
  }

  @Override
  public void onNoAdsAvailable() {
    mView.noAdsAvailable();
  }
}

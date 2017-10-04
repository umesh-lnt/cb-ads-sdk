package com.cloudbanter.adssdk.ad_exchange.presentation.banners.cloudbanter_ads;

import android.content.Context;
import android.support.annotation.NonNull;

import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;
import com.cloudbanter.adssdk.ad_exchange.domain.banners.cloudbanter_ads.CloudbanterAdsInteractor;
import com.cloudbanter.adssdk.ad_exchange.domain.banners.cloudbanter_ads.CloudbanterAdsInteractorFactory;
import com.cloudbanter.adssdk.ad_exchange.domain.banners.common.OnNextAdCallback;
import com.cloudbanter.adssdk.ad_exchange.presentation.banners.preload_ads.PreloadedAdsPresenter;

/**
 * Implementation of the {@link PreloadedAdsPresenter}
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 10/3/17
 */
public class CloudbanterAdsPresenterImpl implements CloudbanterAdsPresenter, OnNextAdCallback {

  /** Cloudbanter ads interactor **/
  private final CloudbanterAdsInteractor mCloudbanterAdsInteractor;

  /** Cloudbanter ads view **/
  private final CloudbanterAdsView mView;

  /**
   * Constructor
   */
  public CloudbanterAdsPresenterImpl(@NonNull Context context, @NonNull CloudbanterAdsView view) {
    this.mView = view;
    mCloudbanterAdsInteractor = CloudbanterAdsInteractorFactory.getInstance(context);
  }

  @Override
  public void getAd() {
    mCloudbanterAdsInteractor.getNextAd(this);
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

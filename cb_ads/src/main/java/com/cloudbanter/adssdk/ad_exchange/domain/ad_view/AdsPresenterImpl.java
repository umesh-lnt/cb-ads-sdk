package com.cloudbanter.adssdk.ad_exchange.domain.ad_view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.cloudbanter.adssdk.ad_exchange.domain.managers.AdBlenderManager;
import com.cloudbanter.adssdk.ad_exchange.domain.observers.AdsStateSubscription;
import com.cloudbanter.adssdk.ad_exchange.presentation.ad_view.AdPresenter;
import com.cloudbanter.adssdk.ad_exchange.presentation.ad_view.AdView;
import com.cloudbanter.adssdk.model.ad_blender.AdsConfig;
import com.cloudbanter.adssdk.util.InfiniteTimer;


/**
 * Implementation of {@link AdPresenter}
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 28/2/17
 */
public class AdsPresenterImpl implements AdPresenter, InfiniteTimer.OnTickCallback,
        AdBlenderManager.AdsConfigObserver {
  
  /** Tag for logs **/
  private static final String TAG = AdsPresenterImpl.class.getSimpleName();
  
  /** Ad view instance **/
  private final AdView mAdView;
  
  /** Flag for initialization **/
  private boolean mInitialized = false;
  
  /** Infinite timer **/
  private InfiniteTimer mInfiniteTimer;
  
  /** Ads Interactor **/
  private AdsInteractor mAdsInteractor;
  
  /** Current context **/
  private Context mContext;
  
  /**
   * Constructor to properly load an ad view
   *
   * @param adView
   *         The ad view to be loaded
   */
  public AdsPresenterImpl(@NonNull AdView adView) {
    this.mAdView = adView;
  }
  
  @Override
  public void init(Context context) {
    if (mInitialized) {
      Log.i(TAG, "Ad view already initialized");
      return;
    }
    mInitialized = true;
    mContext = context;
    mInfiniteTimer = new InfiniteTimer(getRotationTime(), false, this);
    mAdsInteractor = AdsInteractorFactory.getInstance(context);
    AdBlenderManager.getInstance().registerToAdsConfigChanges(this);
  }
  
  @Override
  public void onAdsConfigChanged(AdsConfig adsConfig) {
    if (mInfiniteTimer != null) {
      mInfiniteTimer.cancel();
    }
    destroy();
    init(mContext);
    mInfiniteTimer.start();
  }
  
  private long getRotationTime() {
    return AdBlenderManager.getInstance().getRotationTimeInMillis();
  }
  
  @Override
  public void onTick() {
    View nextAd = mAdsInteractor.pollCachedAd();
    if (nextAd != null) {
      mAdView.showAd(nextAd);
    }
  }
  
  @Override
  public void resumeAds() {
    Log.i(TAG, "Ads resumed");
    if (!checkInitialization()) {
      return;
    }
    AdsStateSubscription.getInstance().submitEvent(AdsStateSubscription.Event.WAKE_UP);
    mInfiniteTimer.cancel();
    mInfiniteTimer.start();
  }
  
  @Override
  public void pauseAds() {
    Log.i(TAG, "Ads paused");
    if (!checkInitialization()) {
      return;
    }
    AdsStateSubscription.getInstance().submitEvent(AdsStateSubscription.Event.SLEEP);
    mInfiniteTimer.cancel();
  }
  
  @Override
  public void destroy() {
    Log.i(TAG, "Ads destroyed");
    mInitialized = false;
    mInfiniteTimer = null;
  }
  
  /**
   * Checks if the Ad view has been already initialized. In case of not, warning logs are shown
   *
   * @return True if the ad view has been already initialized
   */
  private boolean checkInitialization() {
    if (!mInitialized) {
      Log.w(TAG,
              "CloudbanterAdView has not been initialized. No ads will be shown if init() is not " +
                      "invoked");
    }
    return mInitialized;
  }
  
}

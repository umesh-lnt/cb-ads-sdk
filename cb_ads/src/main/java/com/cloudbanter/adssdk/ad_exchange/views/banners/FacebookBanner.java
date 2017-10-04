package com.cloudbanter.adssdk.ad_exchange.views.banners;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.ProxyManager;
import com.cloudbanter.adssdk.ad.AdAwareContextWrapper;
import com.cloudbanter.adssdk.ad.manager.BannerGrabber;
import com.cloudbanter.adssdk.ad_exchange.views.CustomEventBanner;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.mopub.common.util.Views;
import com.mopub.mobileads.MoPubErrorCode;

import java.util.Map;

/**
 * Tested with Facebook SDK 4.8.1.
 */
public class FacebookBanner extends CustomEventBanner implements AdListener {
  public static final String TAG = FacebookBanner.class.getSimpleName();
  public static final String PLACEMENT_ID_KEY = "placement_id";
  public static final String AD_WIDTH = "adWidth";
  public static final String AD_HEIGHT = "adHeight";
  
  private AdView mFacebookBanner;
  private CustomEventBannerListener mBannerListener;
  
  /**
   * CustomEventBanner implementation
   */
  
  @Override
  public void loadBanner(final Context context,
                         final CustomEventBannerListener customEventBannerListener,
                         final Map<String, Object> localExtras,
                         final Map<String, String> serverExtras) {
    mBannerListener = customEventBannerListener;
    Context adAwareContext = new AdAwareContextWrapper(context, BannerGrabber.SOURCE_FACEBOOK_AD);
    
    String placementId;
    if (serverExtrasAreValid(serverExtras)) {
      placementId = serverExtras.get(PLACEMENT_ID_KEY);
    } else {
      mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
      return;
    }
    Log.d(TAG, "Facebook placement id: " + placementId);
    
    int width;
    int height;
    if (localExtrasAreValid(serverExtras)) {
      width = (int) ((double) Double.valueOf(serverExtras.get(AD_WIDTH)));
      height = (int) ((double) Double.valueOf(serverExtras.get(AD_HEIGHT)));
    } else {
      mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
      return;
    }
    
    AdSize adSize = calculateAdSize(width, height);
    AdSettings.addTestDevice("4f2b3e8d1abd16136b47ec92b46057dd");
    if (adSize == null) {
      mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
      return;
    }
    mFacebookBanner = new AdView(adAwareContext, placementId, adSize);
    mFacebookBanner.setAdListener(this);
    mFacebookBanner.disableAutoRefresh();
    Log.d(TAG, "Requesting facebook ad");
    mFacebookBanner.loadAd();
    if (CbAdsSdk.PROXY_ADS) {
      ProxyManager.notifyAboutProxy(context, CbAdsSdk.PROXY_URL, CbAdsSdk.PROXY_PORT,
              CbAdsSdk.class.getName());
    }
    
  }
  
  @Override
  public void onInvalidate() {
    if (mFacebookBanner != null) {
      Views.removeFromParent(mFacebookBanner);
      mFacebookBanner.destroy();
      mFacebookBanner = null;
    }
  }
  
  @Override
  public void addViewEvent(View bannerView) {
    handleFavorites(bannerView);
    // Event call has been removed because it should be invoked in #onLoggingImpression()
  }
  
  @Override
  protected String getSource() {
    return BannerGrabber.SOURCE_FACEBOOK_AD;
  }
  
  /**
   * AdListener implementation
   */
  
  @Override
  public void onAdLoaded(Ad ad) {
    Log.d(TAG, "Facebook banner ad loaded successfully. Showing ad...");
    mBannerListener.onBannerLoaded(mFacebookBanner);
  }
  
  @Override
  public void onError(final Ad ad, final AdError error) {
    Log.d(TAG, "Facebook banner ad failed to load.");
    Log.d(TAG, "Error: " + error.getErrorCode() + " " + error.getErrorMessage());
    if (error == AdError.NO_FILL) {
      mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
    } else if (error == AdError.INTERNAL_ERROR) {
      mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
    } else {
      mBannerListener.onBannerFailed(MoPubErrorCode.UNSPECIFIED);
    }
  }
  
  @Override
  public void onAdClicked(Ad ad) {
    Log.d(TAG, "Facebook banner ad clicked.");
    mBannerListener.onBannerClicked();
  }
  
  @Override
  public void onLoggingImpression(Ad ad) {
    Log.i(TAG, "Logging impression for Facebook Ad");
    super.addViewEvent(null);
  }
  
  private boolean serverExtrasAreValid(final Map<String, String> serverExtras) {
    final String placementId = serverExtras.get(PLACEMENT_ID_KEY);
    return (placementId != null && placementId.length() > 0);
  }
  
  private boolean localExtrasAreValid(@NonNull final Map<String, String> localExtras) {
    return localExtras.containsKey(AD_WIDTH) && localExtras.containsKey(AD_HEIGHT);
  }
  
  @Nullable
  private AdSize calculateAdSize(int width, int height) {
    // Use the smallest AdSize that will properly contain the adView
    if (height <= AdSize.BANNER_320_50.getHeight()) {
      return AdSize.BANNER_320_50;
    } else if (height <= AdSize.BANNER_HEIGHT_90.getHeight()) {
      return AdSize.BANNER_HEIGHT_90;
    } else if (height <= AdSize.RECTANGLE_HEIGHT_250.getHeight()) {
      return AdSize.RECTANGLE_HEIGHT_250;
    } else {
      return null;
    }
  }
  
  @Deprecated
    // for testing
  AdView getAdView() {
    return mFacebookBanner;
  }
}
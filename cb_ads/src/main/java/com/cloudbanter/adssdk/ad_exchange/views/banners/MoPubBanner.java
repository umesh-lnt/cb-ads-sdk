package com.cloudbanter.adssdk.ad_exchange.views.banners;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.cloudbanter.adssdk.R;
import com.cloudbanter.adssdk.ad.manager.BannerGrabber;
import com.cloudbanter.adssdk.ad_exchange.views.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import java.util.Map;

/**
 * MoPub ad network loading banner
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 10/4/17
 */
public class MoPubBanner extends CustomEventBanner implements MoPubView.BannerAdListener {
  
  /** Tag for logs **/
  private static final String TAG = MoPubBanner.class.getSimpleName();
  
  /** Ad unit ID param **/
  private static final String AD_UNIT_ID_PARAM = "adUnitID";
  
  /** MoPub View **/
  private MoPubView sMoPubView;
  
  /** Custom event banner listener **/
  private CustomEventBannerListener mCustomEventBannerListener;
  
  @Override
  public synchronized void loadBanner(Context context,
                                      CustomEventBannerListener customEventBannerListener,
                                      Map<String, Object> localExtras,
                                      Map<String, String> serverExtras) {
    Log.i(TAG, "Initializing MoPub banner loading...");
    if (!(context instanceof Activity)) {
      Log.e(TAG, "No activity supplied for inflating view");
      customEventBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
      return;
    }
    Activity activity = (Activity) context;
    mCustomEventBannerListener = customEventBannerListener;
    String adUnitId = serverExtras.get(AD_UNIT_ID_PARAM);
    MoPubView adView = getMoPubView(activity);
    adView.setAdUnitId(adUnitId);
    adView.setBannerAdListener(this);
    Log.d(TAG, "Start loading banner");
    adView.loadAd();
  }
  
  /**
   * Gets the MoPub view or creates it if doesn't exist
   *
   * @param activity
   *         Base activity
   *
   * @return MoPubView view
   */
  @SuppressLint("InflateParams")
  @NonNull
  private MoPubView getMoPubView(Activity activity) {
    if (sMoPubView == null) {
      sMoPubView = (MoPubView) activity.getLayoutInflater()
              .inflate(R.layout.layout_mopub_banner, null, false);
    }
    return sMoPubView;
  }
  
  @Override
  public void onInvalidate() {
    
  }
  
  @Override
  protected String getSource() {
    return BannerGrabber.SOURCE_MOPUB_MARKET_AD;
  }
  
  @Override
  public void onBannerLoaded(MoPubView banner) {
    Log.i(TAG, "MoPub banner successfully loaded");
    mCustomEventBannerListener.onBannerLoaded(banner);
  }
  
  @Override
  public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
    Log.e(TAG, "Failed to load MoPub banner: " + errorCode);
    mCustomEventBannerListener.onBannerFailed(errorCode);
  }
  
  @Override
  public void onBannerClicked(MoPubView banner) {
    
  }
  
  @Override
  public void onBannerExpanded(MoPubView banner) {
    
  }
  
  @Override
  public void onBannerCollapsed(MoPubView banner) {
    
  }
}

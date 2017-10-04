package com.cloudbanter.adssdk.ad_exchange.ad_networks;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.cloudbanter.adssdk.R;
import com.cloudbanter.adssdk.ad_exchange.views.CustomEventBanner;
import com.cloudbanter.adssdk.ad_exchange.views.banners.CloudbanterAdsBanner;
import com.cloudbanter.adssdk.ad_exchange.views.banners.FacebookBanner;
import com.cloudbanter.adssdk.ad_exchange.views.banners.FlurryCustomEventBanner;
import com.cloudbanter.adssdk.ad_exchange.views.banners.GooglePlayServicesBanner;
import com.cloudbanter.adssdk.ad_exchange.views.banners.InMobiBannerCustomEvent;
import com.cloudbanter.adssdk.ad_exchange.views.banners.MillennialBanner;
import com.cloudbanter.adssdk.ad_exchange.views.banners.MoPubBanner;
import com.cloudbanter.adssdk.ad_exchange.views.banners.PreloadedAdsBanner;
import com.cloudbanter.adssdk.ad_exchange.views.banners.SomaMopubAdapter;

/**
 * Available ad networks. Each AdNetwork has its own structure, which is specified on the specific
 * string resource
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 28/2/17
 */
public enum AdNetwork {
  
  /** Cloudbanter ads **/
  PRELOADED_ADS(PreloadedAdsBanner.class, R.string.preload_ad_data),
  
  /** Cloudbanter ads **/
  CLOUDBANTER(CloudbanterAdsBanner.class, R.string.cloudbanter_ad_data),
  
  /** AdMob **/
  AD_MOB(GooglePlayServicesBanner.class, R.string.ad_mob_data),
  
  /** Facebook **/
  FACEBOOK(FacebookBanner.class, R.string.facebook_data),
  
  /** Millennial Media **/
  MILLENNIAL_MEDIA(MillennialBanner.class, R.string.millennial_data),
  
  /** InMobi **/
  IN_MOBI(InMobiBannerCustomEvent.class, R.string.in_mobi_data),
  
  /** Smaato **/
  SMAATO(SomaMopubAdapter.class, R.string.smaato_data),
  
  /** MoPub **/
  MOPUB(MoPubBanner.class, R.string.ad_mob_data),
  
  /** Flurry **/
  FLURRY(FlurryCustomEventBanner.class, R.string.flurry_data);
  
  /** Class of the banner that load the Ad view **/
  private final Class<? extends CustomEventBanner> mBannerClass;
  
  /** String res that contains the data from the given flavor **/
  @StringRes
  private final int mDataRes;
  
  /**
   * AdNetwork constructor which indicates the specific resource with the requested data
   *
   * @param dataRes
   *         Data related to the network
   */
  AdNetwork(@NonNull Class<? extends CustomEventBanner> bannerClass, @StringRes int dataRes) {
    mBannerClass = bannerClass;
    mDataRes = dataRes;
  }
  
  /**
   * @return the mBannerClass
   */
  public Class<? extends CustomEventBanner> getBannerClass() {
    return mBannerClass;
  }
  
  /**
   * @return the mDataRes
   */
  public int getDataRes() {
    return mDataRes;
  }
}

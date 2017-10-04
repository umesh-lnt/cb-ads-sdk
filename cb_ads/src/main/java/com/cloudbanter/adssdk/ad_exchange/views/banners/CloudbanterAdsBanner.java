package com.cloudbanter.adssdk.ad_exchange.views.banners;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.cloudbanter.adssdk.R;
import com.cloudbanter.adssdk.ad.manager.AdvertManager;
import com.cloudbanter.adssdk.ad.manager.BannerGrabber;
import com.cloudbanter.adssdk.ad.manager.EventAggregator;
import com.cloudbanter.adssdk.ad.manager.images.CbBitmap;
import com.cloudbanter.adssdk.ad.manager.images.ImageDisplayActivity;
import com.cloudbanter.adssdk.ad.manager.images.ImageRef;
import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;
import com.cloudbanter.adssdk.ad.ui.CbWebBrowserActivity;
import com.cloudbanter.adssdk.ad.ui.CloudbanterCentralActivity;
import com.cloudbanter.adssdk.ad_exchange.presentation.banners.cloudbanter_ads.CloudbanterAdsPresenter;
import com.cloudbanter.adssdk.ad_exchange.presentation.banners.cloudbanter_ads.CloudbanterAdsPresenterImpl;
import com.cloudbanter.adssdk.ad_exchange.presentation.banners.cloudbanter_ads.CloudbanterAdsView;
import com.cloudbanter.adssdk.ad_exchange.views.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;

import java.util.Map;

/**
 * Banner for preload and default ads
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 10/3/17
 */
public class CloudbanterAdsBanner extends CustomEventBanner implements CloudbanterAdsView {
  
  /** Cloudbanter ads presenter **/
  private CloudbanterAdsPresenter mPresenter;
  
  /** Activity **/
  private Activity mActivity;
  
  /** Custom Event Banner Listener **/
  private CustomEventBannerListener mListener;
  
  @Override
  public void loadBanner(Context context,
                         CustomEventBannerListener customEventBannerListener,
                         Map<String, Object> localExtras, Map<String, String> serverExtras) {
    mListener = customEventBannerListener;
    if (context instanceof Activity) {
      mActivity = (Activity) context;
    }
    getPresenter().getAd();
  }
  
  @Override
  public void onInvalidate() {
    
  }
  
  @Override
  protected String getSource() {
    return BannerGrabber.SOURCE_UNKNOWN_SOURCE;
  }
  
  /**
   * Gets the presenter instance
   */
  @NonNull
  private CloudbanterAdsPresenter getPresenter() {
    if (mPresenter == null) {
      mPresenter = new CloudbanterAdsPresenterImpl(mActivity, this);
    }
    return mPresenter;
  }
  
  @Override
  public void showAd(CbScheduleEntry entry) {
    if (mActivity == null) {
      mListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
      return;
    }
    loadBanner(entry);
  }
  
  @Override
  public void noAdsAvailable() {
    mListener.onBannerFailed(MoPubErrorCode.NO_FILL);
  }
  
  /**
   * Loads a banner given an entry
   *
   * @param entry
   *         Entry to be shown
   */
  @SuppressLint("InflateParams")
  private void loadBanner(CbScheduleEntry entry) {
    ImageView banner = (ImageView) mActivity.getLayoutInflater()
            .inflate(R.layout.layout_preload_ad_banner, null, false);
    Bitmap b = CbBitmap.getBitmap(entry, ImageRef.IMAGE_TYPE_BANNER, mActivity);
    if (b != null) {
      banner.setImageBitmap(b);
      banner.setOnClickListener(new AdClickListener(mActivity, entry));
      notifyLoadedAd(entry);
      mListener.onBannerLoaded(banner);
    } else {
      mListener.onBannerFailed(MoPubErrorCode.INTERNAL_ERROR);
    }
  }
  
  /**
   * Sends the event that notifies that the ad was loaded
   *
   * @param entry
   *         Ad entry
   */
  private void notifyLoadedAd(CbScheduleEntry entry) {
    EventAggregator eventAggregator = EventAggregator.getInstance();
    if (eventAggregator != null) {
      eventAggregator.addView(entry._id);
    }
  }
  
  /**
   * Listener for ad clicked
   */
  private class AdClickListener implements View.OnClickListener {
    
    /** Context **/
    final Context mContext;
    
    /** Clicked entry **/
    final CbScheduleEntry mEntry;
    
    /** Constructor **/
    AdClickListener(Context context, CbScheduleEntry entry) {
      mContext = context;
      mEntry = entry;
    }
    
    @Override
    public void onClick(View v) {
      if (null == mEntry) {
        return;
      }
      // send click to admanager - process and send click via services.
      AdvertManager.getInstance(mContext).onBannerClick(mContext, mEntry);
      
      // goto Cloudbanter Central - delegates to detail
      if (mEntry.advert.fullImage != null) {
        Intent intent = new Intent(mContext, CloudbanterCentralActivity.class);
        intent.putExtra(ImageDisplayActivity.ARG_ENTRY_ITEM, mEntry);
        mContext.startActivity(intent);
      } else {
        mEntry.onFullAdClick();
        Intent intent = new Intent(mContext, CbWebBrowserActivity.class);
        intent.putExtra(CbWebBrowserActivity.EXTRA_URL, mEntry.advert.url);
        mContext.startActivity(intent);
      }
    }
  }
}

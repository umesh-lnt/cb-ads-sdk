package com.cloudbanter.adssdk.ad_exchange.views.banners;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.ProxyManager;
import com.cloudbanter.adssdk.ad.manager.BannerGrabber;
import com.cloudbanter.adssdk.ad.manager.ExternalAdManager;
import com.cloudbanter.adssdk.ad.ui.CbWebBrowserActivity;
import com.cloudbanter.adssdk.ad_exchange.views.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;
import com.smaato.soma.AdDownloaderInterface;
import com.smaato.soma.AdListenerInterface;
import com.smaato.soma.AdType;
import com.smaato.soma.BannerStateListener;
import com.smaato.soma.BannerView;
import com.smaato.soma.BaseView;
import com.smaato.soma.CrashReportTemplate;
import com.smaato.soma.ReceivedBannerInterface;
import com.smaato.soma.bannerutilities.constant.BannerStatus;
import com.smaato.soma.debug.DebugCategory;
import com.smaato.soma.debug.Debugger;
import com.smaato.soma.debug.LogMessage;
import com.smaato.soma.exception.ClosingLandingPageFailed;
import com.smaato.soma.exception.OpeningLandingPageFailed;
import com.smaato.soma.exception.RetrievingDeviceOrientationFailed;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Example of MoPub Smaato Banner mediation adapter.
 * <p>
 * Updated in Smaato SDK 5.0.7 version release
 *
 * @author Palani Soundararajan
 */

public class SomaMopubAdapter extends CustomEventBanner {
  
  private static BannerView mBanner;
  
  private static String TAG = SomaMopubAdapter.class.getSimpleName();
  
  private String targetUrl = null;
  
  private static final boolean AD_AUDIO_ALERT = false;
  
  private LinearLayout parentLayout;
  
  private static boolean removedOtherViews = false;
  
  
  /*
  * (non-Javadoc)
  * @see
  * com.mopub.mobileads.CustomEventBanner#loadBanner(android.content.Context,
  * com.mopub.mobileads.CustomEventBanner.CustomEventBannerListener,
  * java.util.Map, java.util.Map) */
  @Override
  public void loadBanner(final Context context,
                         final CustomEventBannerListener customEventBannerListener,
                         Map<String, Object> localExtras,
                         Map<String, String> serverExtras) {
    try {
      Log.d(TAG, "Trying to load smaato banner");
      if (mBanner == null) {

//                Debugger.setDebugMode(Debugger.Level_3);
//                mBanner = new BannerView(context);
        
        Debugger.setDebugMode(Debugger.Level_2);
        mBanner = new BannerView(context) {
          
          
          @Override
          protected void openInternalBrowser()
                  throws OpeningLandingPageFailed, RetrievingDeviceOrientationFailed {
            if (targetUrl == null) {
              super.openInternalBrowser();
            } else {
              Log.d(TAG, "Intercepting url: " + targetUrl);
              Intent openCbWebBrowser = new Intent(context, CbWebBrowserActivity.class);
              openCbWebBrowser.putExtra(CbWebBrowserActivity.EXTRA_URL, targetUrl);
              openCbWebBrowser.putExtra(CbWebBrowserActivity.EXTRA_SOURCE,
                      BannerGrabber.SOURCE_SMAATO_AD);
              context.startActivity(openCbWebBrowser);
            }
          }
          
        };
        
        mBanner.addAdListener(new AdListenerInterface() {
          @Override
          public void onReceiveAd(final AdDownloaderInterface downloaderInterface,
                                  final ReceivedBannerInterface receivedBannerInterface) {
            
            new CrashReportTemplate<Void>() {
              @Override
              public Void process() throws Exception {
                
                if (receivedBannerInterface.getStatus() == BannerStatus.ERROR) {
                  printDebugLogs("NO_FILL", DebugCategory.DEBUG);
                  Log.d(TAG, "No fill");
                  Log.d(TAG, "Did we remove other views?: " + removedOtherViews);
                  if (removedOtherViews) {
                    reataachOtherViews();
                  }
                  customEventBannerListener.onBannerFailed(MoPubErrorCode.NO_FILL);
                } else {
                  printDebugLogs("Ad available", DebugCategory.DEBUG);
                  Log.d(TAG, "Trying to show smaato banner!");
                  mBanner.setVisibility(View.VISIBLE);
                  if (AD_AUDIO_ALERT) {
                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 400);
                  }
                  
                  Log.d(TAG, "Rich media data: " + receivedBannerInterface.getRichMediaData());
                  Log.d(TAG, "Click url: " + receivedBannerInterface.getClickUrl());
                  if (receivedBannerInterface.getClickUrl() == null) {
                    if (receivedBannerInterface.getAdType() == AdType.RICHMEDIA) {
                      targetUrl = null;
                    }
                    if (receivedBannerInterface.getAdType() == AdType.IMAGE) {
                      targetUrl = receivedBannerInterface.getClickUrl();
                    }
                    if (receivedBannerInterface.getAdType() == AdType.TEXT) {
                      targetUrl = receivedBannerInterface.getClickUrl();
                    }
                    if (receivedBannerInterface.getAdType() == AdType.NATIVE) {
                      targetUrl = receivedBannerInterface.getClickUrl();
                    }
                  } else {
                    targetUrl = receivedBannerInterface.getClickUrl();
                  }
                  Log.d(TAG, "Reporting banner loaded");
                  //Add a fake view to trigger invalidate
                  customEventBannerListener.onBannerLoaded(mBanner);
                  
                  
                }
                
                return null;
              }
            }.execute();
            
          }
        });
        mBanner.setBannerStateListener(new BannerStateListener() {
          @Override
          public void onWillOpenLandingPage(BaseView arg0) {
            printDebugLogs("Banner Clicked", DebugCategory.DEBUG);
          }
          
          @Override
          public void onWillCloseLandingPage(BaseView arg0) throws ClosingLandingPageFailed {
            new CrashReportTemplate<Void>() {
              @Override
              public Void process() throws Exception {
                mBanner.asyncLoadNewBanner();
                printDebugLogs("Banner closed", DebugCategory.DEBUG);
                return null;
              }
            }.execute();
          }
        });
      }
      Log.d(TAG, "Publisher id: " + serverExtras.get("publisherId"));
      Log.d(TAG, "AdSpace id: " + serverExtras.get("adSpaceId"));
      int publisherId = Integer.parseInt(serverExtras.get("publisherId"));
      int adSpaceId = Integer.parseInt(serverExtras.get("adSpaceId"));
      mBanner.getAdSettings().setPublisherId(publisherId);
      mBanner.getAdSettings().setAdspaceId(adSpaceId);
      Log.d(TAG, "Request Smaato banner");
      resetBackgroundCounter(mBanner);
      mBanner.asyncLoadNewBanner();
      if (CbAdsSdk.PROXY_ADS) {
        ProxyManager.notifyAboutProxy(context, CbAdsSdk.PROXY_URL, CbAdsSdk.PROXY_PORT,
                CbAdsSdk.class.getName());
      }
    } catch (RuntimeException e) {
      Log.e(TAG, "", e);
      printDebugLogs("Failed to load banner", DebugCategory.ERROR);
    } catch (Exception e) {
      Log.e(TAG, "", e);
    }
  }
  
  private void detachOtherViews(BannerView bannerView) {
    Log.d(TAG, "Removing other views");
    ExternalAdManager.getInstance().replaceAllViews(bannerView);
    removedOtherViews = true;
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        reataachOtherViews();
      }
    }, 13 * 1000);
    BannerGrabber.getInstance().storeBitmapForSmato(bannerView);
  }
  
  private void reataachOtherViews() {
    Log.d(TAG, "Reattaching other views");
    mBanner.setVisibility(View.GONE);
    ExternalAdManager.getInstance().restoreViews();
    removedOtherViews = false;
    
  }
  
  
  public void resetBackgroundCounter(BannerView bannerView) {
    try {
      Field backgroundCounterField = BaseView.class.getDeclaredField("backgroundViews");
      backgroundCounterField.setAccessible(true);
      Log.d(TAG, "Current background counter: " + (int) backgroundCounterField.get(bannerView));
      backgroundCounterField.set(bannerView, 0);
    } catch (NoSuchFieldException e) {
      Log.e(TAG, "", e);
    } catch (IllegalAccessException e) {
      Log.e(TAG, "", e);
    }
  }

    /*
* (non-Javadoc)
* @see com.mopub.mobileads.CustomEventBanner#onInvalidate() */
  
  @Override
  public void onInvalidate() {
    // clear for memory
    Log.d(TAG, "Invalidating ad");
    Log.d(TAG, "Did we remove other views?: " + removedOtherViews);
    if (removedOtherViews) {
      reataachOtherViews();
    }
    
  }
  
  @Override
  protected String getSource() {
    return BannerGrabber.SOURCE_MILLENIAL_AD;
  }
  
  public void printDebugLogs(String str, DebugCategory debugCategory) {
    Debugger.showLog(new LogMessage(TAG,
            str,
            Debugger.Level_1,
            debugCategory));
  }
  
}
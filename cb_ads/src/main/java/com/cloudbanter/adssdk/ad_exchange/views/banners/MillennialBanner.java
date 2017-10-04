package com.cloudbanter.adssdk.ad_exchange.views.banners;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.ProxyManager;
import com.cloudbanter.adssdk.R;
import com.cloudbanter.adssdk.ad.manager.BannerGrabber;
import com.cloudbanter.adssdk.ad.manager.ExternalAdManager;
import com.cloudbanter.adssdk.ad_exchange.views.CustomEventBanner;
import com.millennialmedia.AppInfo;
import com.millennialmedia.InlineAd;
import com.millennialmedia.InlineAd.AdSize;
import com.millennialmedia.InlineAd.InlineAdMetadata;
import com.millennialmedia.InlineAd.InlineErrorStatus;
import com.millennialmedia.MMException;
import com.millennialmedia.MMLog;
import com.millennialmedia.MMSDK;
import com.millennialmedia.UserData;
import com.mopub.mobileads.AdViewController;
import com.mopub.mobileads.MoPubErrorCode;

import java.util.Map;

/**
 * Compatible with version 6.0 of the Millennial Media SDK.
 */
public class MillennialBanner extends CustomEventBanner {
  public static final String TAG = MillennialBanner.class.getSimpleName();
  
  public static final String LOGCAT_TAG = "MP->MM Inline";
  public static final String DCN_KEY = "dcn";
  public static final String APID_KEY = "adUnitID";
  public static final String AD_WIDTH_KEY = "adWidth";
  public static final String AD_HEIGHT_KEY = "adHeight";
  
  private InlineAd mInlineAd;
  private CustomEventBannerListener mBannerListener;
  private LinearLayout mInternalView;
  private ExternalAdManager mExternalAdManager;
  private static final Handler UI_THREAD_HANDLER = new Handler(Looper.getMainLooper());
  
  
  @Override
  public void loadBanner(final Context context,
                         final CustomEventBannerListener customEventBannerListener,
                         final Map<String, Object> localExtras,
                         final Map<String, String> serverExtras) {
    
    LayoutParams lp;
    String apid;
    String dcn;
    int width;
    int height;
    mBannerListener = customEventBannerListener;
    mExternalAdManager = ExternalAdManager.getInstance();
//        MMLog.setLogLevel(2);
    
    if (!MMSDK.isInitialized()) {
      try {
        MMSDK.initialize((Application) context.getApplicationContext());
      } catch (Exception e) {
        Log.e(LOGCAT_TAG, "Unable to initialize the Millennial SDK-- " + e.getMessage());
        e.printStackTrace();
        UI_THREAD_HANDLER.post(new Runnable() {
          @Override
          public void run() {
            mBannerListener.onBannerFailed(MoPubErrorCode.INTERNAL_ERROR);
          }
        });
        return;
      }
    }
    
    MMLog.setLogLevel(Log.VERBOSE);
    
    if (extrasAreValid(serverExtras)) {
      dcn = serverExtras.get(DCN_KEY);
      apid = serverExtras.get(APID_KEY);
      width = Integer.parseInt(serverExtras.get(AD_WIDTH_KEY));
      height = Integer.parseInt(serverExtras.get(AD_HEIGHT_KEY));
      Log.d(TAG, "Millenial width: " + width);
      Log.d(TAG, "Millenial height: " + height);
    } else {
      Log.e(LOGCAT_TAG,
              "We were given invalid extras! Make sure placement ID, width, and height are " +
                      "specified.");
      UI_THREAD_HANDLER.post(new Runnable() {
        @Override
        public void run() {
          mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        }
      });
      return;
    }
    
    // Add DCN's for Nexage folks
    try {
      AppInfo ai = new AppInfo().setMediator("mopubsdk");
      if (dcn != null && dcn.length() > 0) {
        ai = ai.setSiteId(dcn);
      } else {
        ai = ai.setSiteId(null);
      }
      MMSDK.setAppInfo(ai);
    } catch (IllegalStateException e) {
      Log.e(LOGCAT_TAG, "Caught exception ", e);
      UI_THREAD_HANDLER.post(new Runnable() {
        @Override
        public void run() {
          mBannerListener.onBannerFailed(MoPubErrorCode.INTERNAL_ERROR);
        }
      });
      return;
    } catch (MMException e) {
      Log.e(TAG, "", e);
    }
    
    mInternalView = new LinearLayout(context);
    
    lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    lp.gravity = Gravity.CENTER_HORIZONTAL;
    mInternalView.setLayoutParams(lp);
    
    InlineAdMetadata mInlineAdMetadata;
    
    try {
      mInlineAd = InlineAd.createInstance(apid, mInternalView);
      mInlineAdMetadata = new InlineAdMetadata().setAdSize(new AdSize(width, height));
    } catch (MMException e) {
      e.printStackTrace();
      UI_THREAD_HANDLER.post(new Runnable() {
        @Override
        public void run() {
          mBannerListener.onBannerFailed(MoPubErrorCode.INTERNAL_ERROR);
        }
      });
      return;
    }
    
    mInlineAd.setListener(new MillennialInlineListener());

        /* If MoPub gets location, so do we. */
    try {
      setUserData(context, localExtras);
    } catch (MMException e) {
      Log.e(TAG, "", e);
    }
    
    AdViewController.setShouldHonorServerDimensions(mInternalView);
    Log.d(TAG, "Requesting millenial ad");
    mInlineAd.request(mInlineAdMetadata);
    if (CbAdsSdk.PROXY_ADS) {
      ProxyManager.notifyAboutProxy(context, CbAdsSdk.PROXY_URL, CbAdsSdk.PROXY_PORT,
              CbAdsSdk.class.getName());
    }
  }
  
  private void setUserData(Context context, Map<String, Object> localExtras) throws MMException {
    MMSDK.setLocationEnabled((localExtras.containsKey("location")));
    UserData userData = new UserData();
    if (mExternalAdManager.getAge() != null) {
      userData.setAge(Integer.parseInt(mExternalAdManager.getAge()));
    }
    String maleStr = context.getString(R.string.male);
    if (mExternalAdManager.getGender() != null) {
      userData.setGender(
              mExternalAdManager.getGender().
                      equalsIgnoreCase(maleStr) ? UserData.Gender.MALE : UserData.Gender.FEMALE);
    }
    userData.setKeywords(mExternalAdManager.getCategoriesAsString());
    MMSDK.setUserData(userData);
  }
  
  @Override
  public void onInvalidate() {
    // Destroy any hanging references.
    if (mInlineAd != null) {
      mInlineAd.setListener(null);
      mInlineAd = null;
    }
  }
  
  @Override
  protected String getSource() {
    return BannerGrabber.SOURCE_MILLENIAL_AD;
  }
  
  private boolean extrasAreValid(final Map<String, String> serverExtras) {
    try {
      // Add pos / non-null and APIDs.
      int w = Integer.parseInt(serverExtras.get(AD_WIDTH_KEY));
      int h = Integer.parseInt(serverExtras.get(AD_HEIGHT_KEY));
      if (h < 0 || w < 0) {
        throw new NumberFormatException();
      }
    } catch (Exception e) {
      Log.e(LOGCAT_TAG, "Width and height must exist and contain positive integers!");
      e.printStackTrace();
      return false;
    }
    
    return serverExtras.containsKey(APID_KEY);
  }
  
  class MillennialInlineListener implements InlineAd.InlineListener {
    
    @Override
    public void onAdLeftApplication(InlineAd arg0) {
      // Intentionally not calling MoPub's onLeaveApplication to avoid double-count
      Log.d(LOGCAT_TAG, "Millennial Inline Ad - Leaving application");
    }
    
    @Override
    public void onClicked(InlineAd arg0) {
      Log.d(LOGCAT_TAG, "Millennial Inline Ad - Ad clicked");
      UI_THREAD_HANDLER.post(new Runnable() {
        @Override
        public void run() {
          mBannerListener.onBannerClicked();
        }
      });
    }
    
    @Override
    public void onCollapsed(InlineAd arg0) {
      Log.d(LOGCAT_TAG, "Millennial Inline Ad - Banner collapsed");
      UI_THREAD_HANDLER.post(new Runnable() {
        @Override
        public void run() {
          mBannerListener.onBannerCollapsed();
        }
      });
      
    }
    
    @Override
    public void onExpanded(InlineAd arg0) {
      Log.d(LOGCAT_TAG, "Millennial Inline Ad - Banner expanded");
      UI_THREAD_HANDLER.post(new Runnable() {
        @Override
        public void run() {
          mBannerListener.onBannerExpanded();
        }
      });
    }
    
    @Override
    public void onRequestFailed(InlineAd arg0, InlineErrorStatus err) {
      Log.d(LOGCAT_TAG, "Millennial Inline Ad - Banner failed (" + err.toString() + ")");
      MoPubErrorCode mopubErrorCode;
      
      switch (err.getErrorCode()) {
        case InlineErrorStatus.ADAPTER_NOT_FOUND:
          mopubErrorCode = MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR;
          break;
        case InlineErrorStatus.DISPLAY_FAILED:
          mopubErrorCode = MoPubErrorCode.INTERNAL_ERROR;
          break;
        case InlineErrorStatus.INIT_FAILED:
          mopubErrorCode = MoPubErrorCode.WARMUP;
          break;
        case InlineErrorStatus.NO_NETWORK:
          mopubErrorCode = MoPubErrorCode.NO_CONNECTION;
          break;
        case InlineErrorStatus.UNKNOWN:
          mopubErrorCode = MoPubErrorCode.UNSPECIFIED;
          break;
        case InlineErrorStatus.LOAD_FAILED:
        default:
          mopubErrorCode = MoPubErrorCode.NETWORK_NO_FILL;
      }
      
      final MoPubErrorCode fErrorCode = mopubErrorCode;
      UI_THREAD_HANDLER.post(new Runnable() {
        @Override
        public void run() {
          mBannerListener.onBannerFailed(fErrorCode);
        }
      });
      
    }
    
    @Override
    public void onRequestSucceeded(InlineAd arg0) {
      Log.d(LOGCAT_TAG, "Millennial Inline Ad - Banner request succeeded");
      UI_THREAD_HANDLER.post(new Runnable() {
        @Override
        public void run() {
          mBannerListener.onBannerLoaded(mInternalView);
        }
      });
    }
    
    @Override
    public void onResize(InlineAd arg0, int w, int h) {
      Log.d(LOGCAT_TAG,
              "Millennial Inline Ad - Banner about to resize (width: " + w + ", height: " + h +
                      ")");
    }
    
    @Override
    public void onResized(InlineAd arg0, int w, int h, boolean isClosed) {
      Log.d(LOGCAT_TAG,
              "Millennial Inline Ad - Banner resized (width: " + w + ", height: " + h + "). "
                      + (isClosed ? "Returned to original placement." : "Got a fresh, new place."));
      
    }
    
  }
  
}
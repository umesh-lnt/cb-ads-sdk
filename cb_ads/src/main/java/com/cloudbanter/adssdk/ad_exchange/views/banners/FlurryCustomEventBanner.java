package com.cloudbanter.adssdk.ad_exchange.views.banners;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.FrameLayout;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.ProxyManager;
import com.cloudbanter.adssdk.R;
import com.cloudbanter.adssdk.ad.manager.BannerGrabber;
import com.cloudbanter.adssdk.ad.manager.ExternalAdManager;
import com.cloudbanter.adssdk.ad_exchange.views.CustomEventBanner;
import com.flurry.android.FlurryAgentListener;
import com.flurry.android.ads.FlurryAdBanner;
import com.flurry.android.ads.FlurryAdBannerListener;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdTargeting;
import com.flurry.android.ads.FlurryGender;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Map;

import static com.mopub.mobileads.MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR;
import static com.mopub.mobileads.MoPubErrorCode.NETWORK_INVALID_STATE;
import static com.mopub.mobileads.MoPubErrorCode.NETWORK_NO_FILL;

public class FlurryCustomEventBanner extends CustomEventBanner implements
        FlurryAgentListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
  private static final String LOG_TAG = FlurryCustomEventBanner.class.getSimpleName();
  
  private Context mContext;
  private CustomEventBannerListener mListener;
  private FrameLayout mLayout;
  
  private String mAdSpaceName;
  
  private FlurryAdBanner mBanner;
  
  private ExternalAdManager externalAdManager;
  
  private GoogleApiClient mGoogleApiClient;
  
  private void buildGoogleApiClient(Context context) {
    if (mGoogleApiClient == null) {
      mGoogleApiClient = new GoogleApiClient.Builder(context)
              .addConnectionCallbacks(this)
              .addOnConnectionFailedListener(this)
              .addApi(LocationServices.API)
              .build();
    }
  }
  
  @Override
  public void onConnected(@Nullable Bundle bundle) {
    Log.i(LOG_TAG, "On Google API client connected");
    Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    Log.d(LOG_TAG, "Las known user location: " + lastLocation);
    loadTargetingAndShowAd(lastLocation);
    mGoogleApiClient.disconnect();
  }
  
  @Override
  public void onConnectionSuspended(int i) {
    Log.i(LOG_TAG, "On Google API client connection suspended");
  }
  
  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Log.i(LOG_TAG, "On Google API client connection failed");
    loadTargetingAndShowAd(null);
  }
  
  // CustomEventBanner
  @Override
  public void loadBanner(Context context,
                         CustomEventBannerListener listener,
                         Map<String, Object> localExtras, Map<String, String> serverExtras) {
    if (context == null) {
      Log.e(LOG_TAG, "Context cannot be null.");
      listener.onBannerFailed(ADAPTER_CONFIGURATION_ERROR);
      return;
    }
    if (CbAdsSdk.PROXY_ADS) {
      ProxyManager.notifyAboutProxy(context, CbAdsSdk.PROXY_URL, CbAdsSdk.PROXY_PORT,
              CbAdsSdk.class.getName());
    }
    externalAdManager = ExternalAdManager.getInstance();
    
    if (listener == null) {
      Log.e(LOG_TAG, "CustomEventBannerListener cannot be null.");
      return;
    }
    
    if (!(context instanceof Activity)) {
      Log.e(LOG_TAG, "Ad can be rendered only in Activity context.");
      listener.onBannerFailed(ADAPTER_CONFIGURATION_ERROR);
      return;
    }
    
    if (!extrasAreValid(serverExtras)) {
      listener.onBannerFailed(ADAPTER_CONFIGURATION_ERROR);
      return;
    }
    
    mContext = context;
    mListener = listener;
    mLayout = new FrameLayout(context);
    
    String apiKey = serverExtras.get(FlurryAgentWrapper.PARAM_API_KEY);
    mAdSpaceName = serverExtras.get(FlurryAgentWrapper.PARAM_AD_SPACE_NAME);
    
    FlurryAgentWrapper.getInstance().startSession(context, apiKey, this);
    
    mBanner = new FlurryAdBanner(mContext, mLayout, mAdSpaceName);
    Log.d(LOG_TAG, "fetch Flurry Ad (" + mAdSpaceName + ") -- " + mLayout.toString());
    mBanner.setListener(new FlurryMopubBannerListener());
    
    buildGoogleApiClient(context);
    mGoogleApiClient.connect();
  }
  
  private void loadTargetingAndShowAd(Location location) {
    if (mBanner == null) {
      return;
    }
    if (externalAdManager != null) {
      FlurryAdTargeting flurryAdTargeting = new FlurryAdTargeting();
      String age = externalAdManager.getAge();
      if (age != null) {
        flurryAdTargeting.setAge(Integer.parseInt(age));
      }
      String gender = externalAdManager.getGender();
      String maleStr = mContext.getString(R.string.male);
      if (gender != null) {
        flurryAdTargeting.setGender(
                externalAdManager.getGender().equalsIgnoreCase(maleStr) ? FlurryGender.MALE :
                        FlurryGender.FEMALE);
      }
      if (location != null) {
        flurryAdTargeting.setLocation((float) location.getLatitude(),
                (float) location.getLongitude());
      }
      mBanner.setTargeting(flurryAdTargeting);
    }
    
    mBanner.fetchAndDisplayAd();
  }
  
  @Override
  public void onInvalidate() {
    if (mContext == null) {
      return;
    }
    
    Log.d(LOG_TAG, "MoPub issued onInvalidate (" + mAdSpaceName + ")");
    
    FlurryAgentWrapper.getInstance().endSession(mContext);
    
    if (mBanner != null) {
      mBanner.destroy();
      mBanner = null;
    }
    
    mContext = null;
    mListener = null;
    mLayout = null;
  }
  
  @Override
  protected String getSource() {
    return BannerGrabber.SOURCE_UNKNOWN_SOURCE;
  }
  
  private boolean extrasAreValid(Map<String, String> serverExtras) {
    return serverExtras != null && serverExtras.containsKey(FlurryAgentWrapper.PARAM_API_KEY) &&
            serverExtras.containsKey(FlurryAgentWrapper.PARAM_AD_SPACE_NAME);
    
  }
  
  @Override
  public void onSessionStarted() {
    Log.i(LOG_TAG, "Flurry session started");
  }
  
  // FlurryAdListener
  private class FlurryMopubBannerListener implements FlurryAdBannerListener {
    private final String LOG_TAG = getClass().getSimpleName();
    
    @Override
    public void onFetched(FlurryAdBanner adBanner) {
      Log.d(LOG_TAG, "onFetched(" + adBanner.toString() + ")");
      
      if (mBanner != null) {
        mBanner.displayAd();
      }
    }
    
    @Override
    public void onRendered(FlurryAdBanner adBanner) {
      Log.d(LOG_TAG, "onRendered(" + adBanner.toString() + ")");
      
      if (mListener != null) {
        mListener.onBannerLoaded(mLayout);
      }
    }
    
    @Override
    public void onShowFullscreen(FlurryAdBanner adBanner) {
      Log.d(LOG_TAG, "onShowFullscreen(" + adBanner.toString() + ")");
      
      if (mListener != null) {
        mListener.onBannerExpanded();
      }
    }
    
    @Override
    public void onCloseFullscreen(FlurryAdBanner adBanner) {
      Log.d(LOG_TAG, "onCloseFullscreen(" + adBanner.toString() + ")");
      
      if (mListener != null) {
        mListener.onBannerCollapsed();
      }
    }
    
    @Override
    public void onAppExit(FlurryAdBanner adBanner) {
      Log.d(LOG_TAG, "onAppExit(" + adBanner.toString() + ")");
      
      if (mListener != null) {
        mListener.onLeaveApplication();
      }
    }
    
    @Override
    public void onClicked(FlurryAdBanner adBanner) {
      Log.d(LOG_TAG, "onClicked " + adBanner.toString());
      
      if (mListener != null) {
        mListener.onBannerClicked();
      }
    }
    
    @Override
    public void onVideoCompleted(FlurryAdBanner adBanner) {
      Log.d(LOG_TAG, "onVideoCompleted " + adBanner.toString());
      
      // no-op
    }
    
    @Override
    public void onError(FlurryAdBanner adBanner, FlurryAdErrorType adErrorType,
                        int errorCode) {
      Log.d(LOG_TAG, "onError(" + adErrorType.toString() + " " + errorCode + ")");
      
      if (mListener != null) {
        if (FlurryAdErrorType.FETCH.equals(adErrorType)) {
          mListener.onBannerFailed(NETWORK_NO_FILL);
        } else if (FlurryAdErrorType.RENDER.equals(adErrorType)) {
          mListener.onBannerFailed(NETWORK_INVALID_STATE);
        }
      }
    }
  }
}
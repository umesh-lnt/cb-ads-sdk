package com.cloudbanter.adssdk.ad_exchange.views.banners;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.ProxyManager;
import com.cloudbanter.adssdk.R;
import com.cloudbanter.adssdk.ad.AdAwareContextWrapper;
import com.cloudbanter.adssdk.ad.manager.BannerGrabber;
import com.cloudbanter.adssdk.ad.manager.ExternalAdManager;
import com.cloudbanter.adssdk.ad_exchange.views.CustomEventBanner;
import com.cloudbanter.adssdk.util.StringUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mopub.common.util.Views;
import com.mopub.mobileads.MoPubErrorCode;

import java.util.Map;

import static com.google.android.gms.ads.AdSize.BANNER;
import static com.google.android.gms.ads.AdSize.FULL_BANNER;
import static com.google.android.gms.ads.AdSize.LEADERBOARD;
import static com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE;

/*
 * Compatible with version 7.8.0 of the Google Play Services SDK.
 */

// Note: AdMob ads will now use this class as Google has deprecated the AdMob SDK.

public class GooglePlayServicesBanner extends CustomEventBanner
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
  public static final String TAG = GooglePlayServicesBanner.class.getSimpleName();
  /*
   * These keys are intended for MoPub internal use. Do not modify.
   */
  public static final String AD_UNIT_ID_KEY = "adUnitID";
  public static final String AD_WIDTH_KEY = "adWidth";
  public static final String AD_HEIGHT_KEY = "adHeight";
  public static final String LOCATION_KEY = "location";
  
  private CustomEventBannerListener mBannerListener;
  private AdView mGoogleAdView;
  private ExternalAdManager externalAdManager;
  
  private AdRequest.Builder mAdRequestBuilder;
  private GoogleApiClient mGoogleApiClient;
  private Context mContext;
  
  @SuppressLint("HardwareIds")
  @Override
  public void loadBanner(
          final Context context,
          final CustomEventBannerListener customEventBannerListener,
          final Map<String, Object> localExtras,
          final Map<String, String> serverExtras) {
    mContext = context;
    mBannerListener = customEventBannerListener;
    final String adUnitId;
    final int adWidth;
    final int adHeight;
    
    externalAdManager = ExternalAdManager.getInstance();
    if (externalAdManager == null) {
      mBannerListener.onBannerFailed(MoPubErrorCode.INTERNAL_ERROR);
      return;
    }
    
    if (extrasAreValid(serverExtras)) {
      adUnitId = serverExtras.get(AD_UNIT_ID_KEY);
      adWidth = Integer.parseInt(serverExtras.get(AD_WIDTH_KEY));
      adHeight = Integer.parseInt(serverExtras.get(AD_HEIGHT_KEY));
      
      Log.d(TAG, "Width: " + adWidth);
      Log.d(TAG, "Height: " + adHeight);
    } else {
      mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
      return;
    }


//        if (context.getApplicationContext() != null) {
//            mGoogleAdView = new AdView(MethodInterceptor.getInterceptedContext(context
// .getApplicationContext()));
//
//        } else {
//            mGoogleAdView = new AdView(MethodInterceptor.getInterceptedContext(context));
//
    Context adAwareContext = new AdAwareContextWrapper(context.getApplicationContext(),
            BannerGrabber.SOURCE_GOOGLE_AD);
    mGoogleAdView = new AdView(adAwareContext);
    mGoogleAdView.setAdListener(new AdViewListener());
    mGoogleAdView.setAdUnitId(adUnitId);
    
    
    final AdSize adSize = calculateAdSize(adWidth, adHeight);
    if (adSize == null) {
      mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
      return;
    }
    
    mGoogleAdView.setAdSize(adSize);
    
    String maleStr = context.getString(R.string.male);
    mAdRequestBuilder = new AdRequest.Builder()
            .setRequestAgent("MoPub")
            .setGender(maleStr.equalsIgnoreCase(externalAdManager.getGender()) ?
                    AdRequest.GENDER_MALE :
                    AdRequest.GENDER_FEMALE)
            .setBirthday(externalAdManager.getBirthdayDate());
    
    if (CbAdsSdk.isTestDevice()) {
      String android_id =
              Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
      String deviceId = StringUtils.md5(android_id).toUpperCase();
      mAdRequestBuilder.addTestDevice(deviceId);
      Log.i(TAG, "ID Test Device: " + deviceId);
    }
    
    buildGoogleApiClient(context);
  }
  
  private void buildGoogleApiClient(Context context) {
    if (mGoogleApiClient == null) {
      mGoogleApiClient = new GoogleApiClient.Builder(context)
              .addConnectionCallbacks(this)
              .addOnConnectionFailedListener(this)
              .addApi(LocationServices.API)
              .build();
    }
    mGoogleApiClient.connect();
  }
  
  @Override
  public void onConnected(@Nullable Bundle bundle) {
    Log.i(TAG, "On Google API client connected");
    Location lastLocation = null;
    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
      Log.d(TAG, "Las known user location: " + lastLocation);
    }
    loadAd(lastLocation);
    mGoogleApiClient.disconnect();
  }
  
  @Override
  public void onConnectionSuspended(int i) {
    Log.i(TAG, "On Google API client connection suspended");
  }
  
  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Log.i(TAG, "On Google API client connection failed");
    loadAd(null);
  }
  
  private void loadAd(Location location) {
    try {
      Log.d(TAG, "Requesting google ad");
      mAdRequestBuilder.setLocation(location);
      AdRequest adRequest = mAdRequestBuilder.build();
      boolean isTestDevice = adRequest.isTestDevice(mContext);
      
      Log.i(TAG, "Is AdMob Test Device? " + isTestDevice);
      mGoogleAdView.loadAd(adRequest);
      if (CbAdsSdk.PROXY_ADS) {
        ProxyManager.notifyAboutProxy(mContext, CbAdsSdk.PROXY_URL, CbAdsSdk.PROXY_PORT,
                CbAdsSdk.class.getName());
      }
    } catch (NoClassDefFoundError e) {
      // This can be thrown by Play Services on Honeycomb.
      mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }
  }
  
  @Override
  public void onInvalidate() {
    Views.removeFromParent(mGoogleAdView);
    if (mGoogleAdView != null) {
      mGoogleAdView.setAdListener(null);
      mGoogleAdView.destroy();
    }
  }
  
  @Override
  protected String getSource() {
    return BannerGrabber.SOURCE_GOOGLE_AD;
  }
  
  private boolean extrasAreValid(Map<String, String> serverExtras) {
    try {
      Integer.parseInt(serverExtras.get(AD_WIDTH_KEY));
      Integer.parseInt(serverExtras.get(AD_HEIGHT_KEY));
    } catch (NumberFormatException e) {
      return false;
    }
    
    return serverExtras.containsKey(AD_UNIT_ID_KEY);
  }
  
  private AdSize calculateAdSize(int width, int height) {
    // Use the smallest AdSize that will properly contain the adView
    if (width <= BANNER.getWidth() && height <= BANNER.getHeight()) {
      return BANNER;
    } else if (width <= MEDIUM_RECTANGLE.getWidth() && height <= MEDIUM_RECTANGLE.getHeight()) {
      return MEDIUM_RECTANGLE;
    } else if (width <= FULL_BANNER.getWidth() && height <= FULL_BANNER.getHeight()) {
      return FULL_BANNER;
    } else if (width <= LEADERBOARD.getWidth() && height <= LEADERBOARD.getHeight()) {
      return LEADERBOARD;
    } else {
      return null;
    }
  }
  
  private class AdViewListener extends AdListener {
    /*
     * Google Play Services AdListener implementation
     */
    @Override
    public void onAdClosed() {
      
    }
    
    @Override
    public void onAdFailedToLoad(int errorCode) {
      Log.d(TAG, "Google Play Services banner ad failed to load.");
      Log.d(TAG, "Error: " + errorCode);
      if (mBannerListener != null) {
        mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
      }
    }
    
    @Override
    public void onAdLeftApplication() {
      
    }
    
    @Override
    public void onAdLoaded() {
      Log.d("MoPub", "Google Play Services banner ad loaded successfully. Showing ad...");
      if (mBannerListener != null) {
        mBannerListener.onBannerLoaded(mGoogleAdView);
        externalAdManager.bypassForGoogleAdView(mGoogleAdView);
      }
    }
    
    @Override
    public void onAdOpened() {
      Log.d("MoPub", "Google Play Services banner ad clicked.");
      if (mBannerListener != null) {
        mBannerListener.onBannerClicked();
      }
    }
  }
  
  @Deprecated
    // for testing
  AdView getGoogleAdView() {
    return mGoogleAdView;
  }
}
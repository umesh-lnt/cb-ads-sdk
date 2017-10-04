package com.cloudbanter.adssdk.ad_exchange.views.banners;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.ProxyManager;
import com.cloudbanter.adssdk.ad.AdAwareContextWrapper;
import com.cloudbanter.adssdk.ad.manager.BannerGrabber;
import com.cloudbanter.adssdk.ad.manager.ExternalAdManager;
import com.cloudbanter.adssdk.ad_exchange.views.CustomEventBanner;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiAdRequestStatus.StatusCode;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.ads.InMobiBanner.AnimationType;
import com.inmobi.ads.InMobiBanner.BannerAdListener;
import com.inmobi.sdk.InMobiSdk;
import com.mopub.mobileads.MoPubErrorCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;

/*
 * Tested with InMobi SDK 5.3.1
 */
public class InMobiBannerCustomEvent extends CustomEventBanner implements BannerAdListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
  public static final String TAG = InMobiBannerCustomEvent.class.getSimpleName();
  
  private CustomEventBannerListener mBannerListener;
  private InMobiBanner imbanner;
  private static boolean isAppIntialize = false;
  private JSONObject serverParams;
  private String accountId = "";
  private long placementId = -1;
  private Context mContext;
  
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
    Log.i(TAG, "On Google API client connected");
    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
      Log.d(TAG, "Las known user location: " + lastLocation);
      InMobiSdk.setLocation(lastLocation);
    }
    mGoogleApiClient.disconnect();
  }
  
  @Override
  public void onConnectionSuspended(int i) {
    Log.i(TAG, "On Google API client connection suspended");
  }
  
  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Log.i(TAG, "On Google API client connection failed");
  }
  
  @Override
  public void onAdDismissed(InMobiBanner arg0) {
    Log.v("InMobiBannerCustomEvent", "Ad Dismissed");
  }
  
  @Override
  public void onAdDisplayed(InMobiBanner arg0) {
    Log.v("InMobiBannerCustomEvent", "Ad displayed");
  }
  
  @Override
  public void onAdInteraction(InMobiBanner arg0, Map<Object, Object> arg1) {
    Log.v("InMobiBannerCustomEvent", "Ad interaction");
    mBannerListener.onBannerClicked();
  }
  
  @Override
  public void onAdLoadFailed(InMobiBanner inMobiBanner, InMobiAdRequestStatus requestStatus) {
    Log.v("InMobiBannerCustomEvent", "Ad failed to load");
    Log.d(TAG, "Request status: " + requestStatus.getStatusCode().name() + " message: " +
            requestStatus.getMessage());
    
    if (mBannerListener != null) {
      
      if (requestStatus.getStatusCode() == StatusCode.INTERNAL_ERROR) {
        mBannerListener
                .onBannerFailed(MoPubErrorCode.INTERNAL_ERROR);
      } else if (requestStatus.getStatusCode() == StatusCode.REQUEST_INVALID) {
        mBannerListener
                .onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
      } else if (requestStatus.getStatusCode() == StatusCode.NETWORK_UNREACHABLE) {
        mBannerListener
                .onBannerFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
      } else if (requestStatus.getStatusCode() == StatusCode.NO_FILL) {
        mBannerListener
                .onBannerFailed(MoPubErrorCode.NO_FILL);
      } else if (requestStatus.getStatusCode() == StatusCode.REQUEST_TIMED_OUT) {
        mBannerListener
                .onBannerFailed(MoPubErrorCode.NETWORK_TIMEOUT);
      } else if (requestStatus.getStatusCode() == StatusCode.SERVER_ERROR) {
        mBannerListener
                .onBannerFailed(MoPubErrorCode.SERVER_ERROR);
      } else {
        mBannerListener
                .onBannerFailed(MoPubErrorCode.UNSPECIFIED);
      }
    }
    
  }
  
  @Override
  public void onAdLoadSucceeded(InMobiBanner arg0) {
    Log.d("InMobiBannerCustomEvent", "InMobi banner ad loaded successfully.");
    if (mBannerListener != null) {
      if (arg0 != null) {
        mBannerListener.onBannerLoaded(arg0);
      } else {
        mBannerListener
                .onBannerFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
      }
    }
  }
  
  @Override
  public void onAdRewardActionCompleted(InMobiBanner arg0, Map<Object, Object> arg1) {
    Log.v("InMobiBannerCustomEvent", "Ad rewarded");
  }
  
  @Override
  public void onUserLeftApplication(InMobiBanner arg0) {
    Log.v("InMobiBannerCustomEvent", "User left applicaton");
    mBannerListener.onLeaveApplication();
  }
  
  @Override
  public void loadBanner(Context context, CustomEventBannerListener bannerListener,
                         Map<String, Object> arg2,
                         Map<String, String> arg3) {
    externalAdManager = ExternalAdManager.getInstance();
    mBannerListener = bannerListener;
    mContext = context;
    buildGoogleApiClient(context);
    mGoogleApiClient.connect();
    Context adAwareContext = new AdAwareContextWrapper(context, BannerGrabber.SOURCE_INMOBI_AD);
    
    if (adAwareContext == null) {
      mBannerListener.onBannerFailed(null);
      return;
    }
    
    try {
      serverParams = new JSONObject(arg3);
      accountId = serverParams.getString("accountid");
      placementId = serverParams.getLong("placementid");
      Log.d("InMobiBannerCustomEvent", String.valueOf(placementId));
      Log.d("InMobiBannerCustomEvent", accountId);
      
    } catch (JSONException e1) {
      e1.printStackTrace();
    }
    
    
    if (!isAppIntialize) {
      Log.d(TAG, "InMobi wasn't initialized, intitializing with account id: " + accountId);
      try {
        if (context instanceof Activity) {
          Log.i(TAG, "Context is Activity");
          InMobiSdk.init((Activity) context, accountId);
        } else {
          Log.i(TAG, "Context isn't Activity");
          InMobiSdk.init(context, accountId);
        }
      } catch (Exception e) {
        Log.e(TAG, "Error initializing InMobi", e);
      }
      isAppIntialize = true;
    }
    /*
    Sample for setting up the InMobi SDK Demographic params.
        Publisher need to set the values of params as they want.

		InMobiSdk.setAreaCode("areacode");
		InMobiSdk.setEducation(Education.HIGH_SCHOOL_OR_LESS);
		InMobiSdk.setGender(Gender.MALE);
		InMobiSdk.setIncome(1000);
		InMobiSdk.setAge(23);
		InMobiSdk.setPostalCode("postalcode");
		InMobiSdk.setLogLevel(LogLevel.DEBUG);
		InMobiSdk.setLocationWithCityStateCountry("blore", "kar", "india");
		InMobiSdk.setLanguage("ENG");
		InMobiSdk.setInterests("dance");
		InMobiSdk.setEthnicity(Ethnicity.ASIAN);
		InMobiSdk.setYearOfBirth(1980);*/
    if (externalAdManager != null) {
      InMobiSdk.setAge(Integer.parseInt(TextUtils.isEmpty(externalAdManager.getAge()) ? "25" : externalAdManager.getAge()));
      Log.d(TAG, "Setting inmobi language: " + Locale.getDefault().getLanguage());
      InMobiSdk.setLanguage(Locale.getDefault().getLanguage());
      Log.d(TAG, "Setting categories (interest) : " + externalAdManager.getCategoriesAsString());
      InMobiSdk.setInterests(externalAdManager.getCategoriesAsString());
    }
    if (context instanceof Activity) {
      imbanner = new InMobiBanner((Activity) context, placementId);
    } else {
      imbanner = new InMobiBanner(adAwareContext, placementId);
    }
    imbanner.setListener(this);
    imbanner.setEnableAutoRefresh(false);
    imbanner.setAnimationType(AnimationType.ANIMATION_OFF);
    
    DisplayMetrics dm = new DisplayMetrics();
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();
    display.getMetrics(dm);
    
    imbanner.setLayoutParams(new LinearLayout.LayoutParams(Math.round(320 * dm.density),
            Math.round(50 * dm.density)));
    Log.d(TAG, "Trying to load inmobi ad");
    imbanner.load();
    if (CbAdsSdk.PROXY_ADS) {
      ProxyManager.notifyAboutProxy(context, CbAdsSdk.PROXY_URL, CbAdsSdk.PROXY_PORT,
              CbAdsSdk.class.getName());
    }
  }
  
  @Override
  public void onInvalidate() {
    Log.d(TAG, "Inmobi ad invalidated");
    // TODO Auto-generated method stub
    
  }
  
  @Override
  protected String getSource() {
    return BannerGrabber.SOURCE_INMOBI_AD;
  }
  
}


package com.cloudbanter.adssdk;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.cloudbanter.adssdk.ad.manager.AdvertManager;
import com.cloudbanter.adssdk.ad.manager.BannerGrabber;
import com.cloudbanter.adssdk.ad.manager.CloudbanterCentral;
import com.cloudbanter.adssdk.ad.manager.EventAggregator;
import com.cloudbanter.adssdk.ad.manager.ExternalAdManager;
import com.cloudbanter.adssdk.ad.manager.PreloadContent;
import com.cloudbanter.adssdk.ad.manager.images.CbImageManager;
import com.cloudbanter.adssdk.ad.service.CbCommunicationManager;
import com.cloudbanter.adssdk.ad.service.http.CbServerAddressResolver;
import com.cloudbanter.adssdk.ad.service.http.ServerAddress;
import com.cloudbanter.adssdk.ad.service.http.retrofit.CloudbanterEndpoints;
import com.cloudbanter.adssdk.ad.util.CbSharedPreferences;
import com.cloudbanter.adssdk.ad_exchange.domain.managers.AdBlenderManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.Tracker;

import java.util.List;

/**
 * Created by 10603675 on 25-09-2017.
 */

public class CbAdsSdk {

    public static final String TAG = CbAdsSdk.class.getSimpleName();
    public static String FILE_PATH;
    public static final String SERVER_NAME = "test";

    private static Context mApplication;
    private static List<String> keywordList;
    public static Boolean sIsInDemoMode = false;
    public static boolean sIsDemoClient = false;

    public static String sVersionName;
    public static int sVersionCode;

    private static Tracker mTracker;

    public static Boolean PROXY_ADS = false;
    public static String PROXY_URL = "demo.cloudbanter.com";
    public static String PROXY_PORT = "8888";
    public static String PRELOAD_IMAGES_ZIPFILE = "PreloadImages.zip";

    public static final ServerAddress CLOUD_SERVER_ADDRESS =
            new ServerAddress("market-ww.cloudbanter.com", "443");

    public static final String PRELOAD_IMAGE_SCHED = "demo";
    private static boolean testDevice;

    public static void initialize(Application application) {
        mApplication = application;

        CbCommunicationManager.init(mApplication);
        CloudbanterEndpoints.init(mApplication, CbServerAddressResolver.getServerAddress(),
                CbServerAddressResolver.getLocationServerAddress());
        ExternalAdManager.init(mApplication);
        PreloadContent.init(mApplication);
        ProxyManager.init(mApplication);
        BannerGrabber.init(application);
        CloudbanterCentral.init();
        EventAggregator.init(mApplication, CbSharedPreferences.getCbDeviceId(mApplication));

        CbImageManager.init(mApplication);
        AdvertManager.init(mApplication);
        AdBlenderManager.init(getApplication());

        PackageInfo pInfo;
        try {
            pInfo = mApplication.getPackageManager().getPackageInfo(mApplication.getPackageName(), 0);
            sVersionName = pInfo.versionName;
            sVersionCode = pInfo.versionCode;
            sVersionName = sVersionName + "-" + Build.MODEL + "-" + Build.VERSION.RELEASE;
            Log.d(TAG, "Got version name: " + sVersionName);
            Log.d(TAG, "Got version code: " + sVersionCode);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "", e);
            sVersionName = "N/A";
            sVersionCode = 1;
        }

        FacebookSdk.sdkInitialize(mApplication);
        AppEventsLogger.activateApp(application);
    }

    public static Context getApplication() {
        return mApplication;
    }

    public static void setDemoMode(boolean isInDemoMode) {
        sIsInDemoMode = isInDemoMode;
    }

    public static void setsIsDemoClient(boolean sIsDemoClient) {
        CbAdsSdk.sIsDemoClient = sIsDemoClient;
    }

    public static void setKeywords(List<String> keywords) {
        keywordList = keywords;
    }

    public static List<String> getKeywords() {
        return keywordList;
    }

    public static void setDefaultTracker(Tracker tracker) {
        mTracker = tracker;
    }

    public static Tracker getDefaultTracker() {
        return mTracker;
    }

    public static void setProxyAds(Boolean proxyAds) {
        PROXY_ADS = proxyAds;
    }

    public static void setProxyUrl(String proxyUrl) {
        PROXY_URL = proxyUrl;
    }

    public static void setProxyPort(String proxyPort) {
        PROXY_PORT = proxyPort;
    }

    public static void setPreloadImagesZipfile(String preloadImagesZipfile) {
        PRELOAD_IMAGES_ZIPFILE = preloadImagesZipfile;
    }

    public static boolean isTestDevice() {
        return testDevice;
    }

    public static void setTestDevice(boolean testDevice) {
        CbAdsSdk.testDevice = testDevice;
    }
}

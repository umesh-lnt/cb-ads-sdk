package com.cloudbanter.adssdk.ad.service;


import android.content.Context;

import com.cloudbanter.adssdk.ad.manager.EventAggregator;
import com.cloudbanter.adssdk.ad.util.CbSharedPreferences;

/**
 * Created by Ugljesa Jovanovic (jovanovic.ugljesa@gmail.com) on 29-Mar-2016.
 */
public class Authenticator {

  private static Authenticator sInstance = null;

  public String deviceId = null;
  public String authToken = null;
  private Context mContext = null;

  public static synchronized Authenticator getInstance(Context context) {
    if (sInstance == null) {
      sInstance = new Authenticator(context);
    }
    return sInstance;
  }

  private Authenticator(Context context) {
    mContext = context;
  }


  public void setAuthToken(String did, String at) {
    deviceId = did;
    authToken = at;
    CbSharedPreferences.setCbDeviceId(mContext, did);
    CbSharedPreferences.setAuthToken(mContext, at);
  }

  public boolean isAuthenticated() {
    if (null != authToken) {
      EventAggregator.init(mContext, deviceId);
      return true;
    } else if (CbSharedPreferences.isRegistered(mContext)) {
      deviceId = CbSharedPreferences.getCbDeviceId(mContext);
      EventAggregator.init(mContext, deviceId);
      authToken = CbSharedPreferences.getAuthToken(mContext);
      return true;
    }
    return false;
  }

  // TODO propagate in calls to prefs/events/user/sched/auth...
  private static boolean sDemoMode = false;

  public static void setDemoMode(boolean b) {
    sDemoMode = true;
  }

  public static boolean getDemoMode() {
    return sDemoMode;
  }
}

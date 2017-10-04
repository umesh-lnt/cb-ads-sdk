package com.cloudbanter.adssdk.ad.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.cloudbanter.adssdk.R;
import com.cloudbanter.adssdk.ad.model.CbDevice;
import com.cloudbanter.adssdk.ad.model.CbPreferenceData;
import com.cloudbanter.adssdk.ad.model.CbSchedule;
import com.cloudbanter.adssdk.ad.model.CbUserInfo;
import com.cloudbanter.adssdk.ad.service.CbRestService;
import com.cloudbanter.adssdk.ad.service.callbacks.CallbackHandler;
import com.cloudbanter.adssdk.ad.service.callbacks.IAtomicRegistrationCallback;
import com.cloudbanter.adssdk.ad.service.callbacks.IGenerateScheduleCallback;
import com.cloudbanter.adssdk.ad.service.callbacks.ISendPreferenceCallback;
import com.cloudbanter.adssdk.ad.service.callbacks.ISendUserInfoCallback;
import com.cloudbanter.adssdk.ad.ui.util.ProgressHandler;
import com.cloudbanter.adssdk.ad.util.CbSharedPreferences;
import com.cloudbanter.adssdk.util.ScheduleUtils;


public abstract class ACbPrefActivity extends AppCompatActivity
        implements ISendPreferenceCallback, ISendUserInfoCallback, IGenerateScheduleCallback,
        IAtomicRegistrationCallback {

  protected static final String TAG = ACbPrefActivity.class.getSimpleName();

  protected static CallbackHandler<?> mHandler;

  ProgressHandler mProgressHandler;

  /**
   * ATTENTION: This was auto-generated to implement the App Indexing API.
   * See https://g.co/AppIndexing/AndroidStudio for more information.
   */

  protected ACbPrefActivity() {
    mHandler = new CallbackHandler<>(this);
  }

  public CallbackHandler<?> getHandler() {
    return mHandler;
  }

  @Override
  public void handleError(String s) {
    Log.e(TAG, s);
  }

  @Override
  public void onSuccess(Object data) {
    Log.d(TAG, "unknown success handler");
  }

  public void sendPrefData(CbPreferenceData prefData) {
    // startProgressDialog();
    Intent intent = CbRestService.getUpdatePreferencesIntent(this, mHandler, prefData);
    startService(intent);
  }


  private static int myOrientation;
  private static boolean myRotated = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
    myOrientation = display.getOrientation();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    if (newConfig.orientation != myOrientation) {
      Log.d(TAG, "rotated");
      myRotated = true;
    }
    super.onConfigurationChanged(newConfig);
  }

  @Override
  public void onSendPreferenceComplete(CbPreferenceData obj) {
    // endProgressDialog();
    CbSharedPreferences.setUserPreferencesSet(this, true);
    if (!myRotated) {
//        Toast.makeText(this, R.string.text_preferences_complete, Toast.LENGTH_LONG).show();
      myRotated = false;
    }
    finish();
    updateSchedule();
    Log.d(TAG, "preferences updated");
  }

  public void sendUserData(CbUserInfo userData) {
    Intent intent = CbRestService.getSendUserDataIntent(this, mHandler, userData);
    startService(intent);
  }

  @Override
  public void onSendUserInfoComplete(CbUserInfo obj) {
    Log.d(TAG, "userinfo updated");
  }

  // TODO we start the intent here - CbRestService will update AdvertManager service
  private void updateSchedule() {

    ScheduleUtils.generateSchedule(this);
  }

  @Override
  public void onGenerateScheduleComplete(CbSchedule obj) {
    Log.d(TAG, "prefs received schedule");
  }

  // -- mod: now Toast indicating prefs changed...
  public void startProgressDialog() {
    if (mProgressHandler == null) {
      mProgressHandler = new ProgressHandler(ACbPrefActivity.this);
    }
    if (mProgressHandler.isProgressDialogShowing()) {
      Log.e(TAG, "Progress dialog already showing.");
      return;
    }

    mProgressHandler.initiateProgressDialog(R.string.text_recording_preferences);
  }

  public void endProgressDialog() {
    Log.d(TAG, "Ending progress dialog");
    if (null != mProgressHandler) {
      mProgressHandler.onTaskComplete();
    } else {
      Log.e(TAG, "Progress handler was null");

    }
  }

  public void doAtomicRegistration(CbDevice device, CbUserInfo userInfo,
                                   CbPreferenceData preferenceData) {
    Intent atomicIntent =
            CbRestService.getAtomicRegistrationIntent(this, getHandler(), device, userInfo,
                    preferenceData);
    startService(atomicIntent);
  }

}

package com.cloudbanter.adssdk.ad.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.R;
import com.cloudbanter.adssdk.ad.service.callbacks.CallbackHandler;
import com.cloudbanter.adssdk.ad.service.callbacks.ICallback;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by eric on 7/10/15.
 */
public abstract class ACbActivity<DataT> extends AppCompatActivity implements ICallback<DataT> {

  protected static final String TAG = ACbActivity.class.getSimpleName();

  protected Tracker mTracker;

  protected static CallbackHandler<?> mHandler;

  protected ACbActivity() {
    mHandler = new CallbackHandler<DataT>(this);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    doItAsynchronously();
  }

  @ColorRes
  protected int getStatusBarColor() {
    return R.color.colorPrimaryDark;
  }

  private void doItAsynchronously() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        initAsynchronously();
      }
    }).run();
  }

  @CallSuper
  protected void initAsynchronously() {
    mTracker = CbAdsSdk.getDefaultTracker();
  }

  protected Activity getActivity() {
    return this;
  }

  @Override
  public void onSuccess(DataT data) {
    handleError("unmatched success receiver");
  }

  @Override
  public void handleError(String s) {
    Log.d(TAG, s);
  }

  public CallbackHandler<?> getHandler() {
    return mHandler;
  }

}

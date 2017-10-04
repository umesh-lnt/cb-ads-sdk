package com.cloudbanter.adssdk.ad.ui;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.R;
import com.cloudbanter.adssdk.ad.service.callbacks.CallbackHandler;
import com.cloudbanter.adssdk.ad.service.callbacks.ICallback;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by eric on 7/10/15.
 */
public class ACbListActivity<DataT> extends ActionBarListActivity implements ICallback<DataT> {

  protected static final String TAG = ACbListActivity.class.getSimpleName();

  protected Tracker mTracker;

  protected static CallbackHandler<?> mCbHandler;

  private static final long EXIT_SYSTEM_DELAY = 1000;

  protected ACbListActivity() {
    mCbHandler = new CallbackHandler<>(this);
  }

  @Override
  public void onSuccess(DataT data) {
    handleError("unmatched success receiver");
  }

  @Override
  public void handleError(String s) {
    Log.e(TAG, s);
  }

  CallbackHandler<?> getHandler() {
    return mCbHandler;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mTracker = CbAdsSdk.getDefaultTracker();
    setUpStatusBar();
  }

  private void setUpStatusBar() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      return;
    }

    Window window = getWindow();

    // clear FLAG_TRANSLUCENT_STATUS flag:
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

    // finally change the color
    window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
  }

  @Override
  public void finishAffinity() {
    super.finishAffinity();
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        System.exit(0);
      }
    }, EXIT_SYSTEM_DELAY);
  }
}

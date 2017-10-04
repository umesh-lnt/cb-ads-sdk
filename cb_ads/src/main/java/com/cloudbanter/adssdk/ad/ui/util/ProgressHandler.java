package com.cloudbanter.adssdk.ad.ui.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.util.Log;

import java.lang.ref.WeakReference;

public class ProgressHandler {
  public static final String TAG = ProgressHandler.class.getSimpleName();

  private ProgressDialog mProgressDialog;
  private final WeakReference<Activity> mActivity;
  private final Handler mHandler;

  public ProgressHandler(Activity activity) {
    mActivity = new WeakReference<Activity>(activity);
    mHandler = new Handler();
  }

  // Shows the activity's progress spinner. Should be canceled if exiting the activity.
  private Runnable mShowProgressDialogRunnable = new Runnable() {
    @Override
    public void run() {
      if (mProgressDialog != null) {
        mProgressDialog.show();
      }
    }
  };

  /**
   * setup the progress dialog with its intended settings.
   */
  private ProgressDialog createProgressDialog(int stringResourceId) {
    if (null == mActivity || null == mActivity.get()) {
      return null;
    }
    ProgressDialog dialog = new ProgressDialog(mActivity.get());
    dialog.setIndeterminate(true);
    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    dialog.setCanceledOnTouchOutside(false);
    dialog.setCancelable(false);
    dialog.setTitle(mActivity.get().getResources().getString(stringResourceId));
    return dialog;
  }

  public void initiateProgressDialog(int id) {
    mProgressDialog = createProgressDialog(id);
    Log.d(TAG, "Showing progress dialog");
    mHandler.post(mShowProgressDialogRunnable);
  }

  public void onTaskComplete() {
    if (null == mActivity && null == mActivity.get()) {
      Log.d(TAG, "Activity is gone.");
      return;
    }

    if (mProgressDialog != null && mProgressDialog.isShowing()) {
      Log.d(TAG, "Dialog showing? " + mProgressDialog.isShowing());
      mProgressDialog.dismiss();
    } else {
      Log.e(TAG, "Dialog was null!");
    }

    mHandler.removeCallbacks(mShowProgressDialogRunnable);
    mProgressDialog = null;
  }

  public boolean isProgressDialogShowing() {
    return mProgressDialog == null ? false : mProgressDialog.isShowing();
  }
}

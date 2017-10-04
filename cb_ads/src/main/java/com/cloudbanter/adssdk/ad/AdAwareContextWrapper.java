package com.cloudbanter.adssdk.ad;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cloudbanter.adssdk.ad.ui.CbWebBrowserActivity;

/**
 * @author Ugljesa Jovanovic (ionspinf@gmail.com) on 06-Sep-2016
 */
public class AdAwareContextWrapper extends ContextWrapper {
  public static final String TAG = AdAwareContextWrapper.class.getSimpleName();

  private String source;

  public AdAwareContextWrapper(Context base, String source) {
    super(base);
    this.source = source;
  }

  @Override
  public void startActivity(Intent intent) {
    if (intent != null) {
      Log.d(TAG, "Intent action: " + intent.getAction());
      if (intent.getComponent() != null) {
        Log.d(TAG, "Target component: " + intent.getComponent().flattenToString());
      }
      if (intent.getData() != null) {
        Log.d(TAG, "Data: " + intent.getDataString());
      }
      if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(Intent.ACTION_VIEW)) {
        Log.d(TAG, source + ": Got view action for: " + source);
        intent.putExtra(CbWebBrowserActivity.EXTRA_URL, intent.getData().toString());
        intent.putExtra(CbWebBrowserActivity.EXTRA_SOURCE, source);
        intent.setComponent(new ComponentName(getBaseContext(), CbWebBrowserActivity.class));
      } else {
        Log.d(TAG, source + ": Null action");
      }

    }
    super.startActivity(intent);
  }

  @Override
  public void startActivity(Intent intent, Bundle options) {
    if (intent != null) {
      Log.d(TAG, "Intent action: " + intent.getAction());
      Log.d(TAG, "Target component: " + intent.getComponent().flattenToString());
      Log.d(TAG, "Data: " + intent.getDataString());
      if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(Intent.ACTION_VIEW)) {
        Log.d(TAG, source + ": Got view action for: " + source);
        intent.putExtra(CbWebBrowserActivity.EXTRA_URL, intent.getData().toString());
        intent.putExtra(CbWebBrowserActivity.EXTRA_SOURCE, source);
        intent.setComponent(new ComponentName(getBaseContext(), CbWebBrowserActivity.class));
      } else {
        Log.d(TAG, source + ": Null action");
      }

    }
    super.startActivity(intent, options);
  }
}

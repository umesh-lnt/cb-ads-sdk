package com.cloudbanter.adssdk.ad.ui;

import android.content.Context;
import android.support.v7.widget.ShareActionProvider;
import android.view.View;

/**
 * Created by Ugljesa Jovanovic (jovanovic.ugljesa@gmail.com) on 25-Jul-2016.
 */
public class AdvertShareActionProvider extends ShareActionProvider {

  /**
   * Creates a new instance.
   *
   * @param context
   *         Context for accessing resources.
   */
  public AdvertShareActionProvider(Context context) {
    super(context);
  }

  @Override
  public View onCreateActionView() {
    return null;
  }
}

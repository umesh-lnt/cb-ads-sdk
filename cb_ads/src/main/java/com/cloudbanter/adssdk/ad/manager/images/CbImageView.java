package com.cloudbanter.adssdk.ad.manager.images;

import android.content.Context;
import android.widget.ImageView;


class CbImageView extends ImageView {
  public CbImageView(Context context) {
    super(context);
  }

  int viewWidth = 0;
  int viewHeight = 0;

  @Override
  protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
    super.onSizeChanged(xNew, yNew, xOld, yOld);
    viewWidth = xNew;
    viewHeight = yNew;
  }
}
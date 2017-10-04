package com.cloudbanter.adssdk.ad.manager;

import android.content.Context;

import com.smaato.soma.BaseView;
import com.smaato.soma.ReceivedBannerInterface;
import com.smaato.soma.exception.CloseButtonBitmapFailed;
import com.smaato.soma.exception.PixelToDpFailed;
import com.smaato.soma.internal.views.CustomWebView;

/**
 * @author Ugljesa Jovanovic (ionspinf@gmail.com) on 15-Nov-2016
 */
public class SmaatoLinkGrabber extends CustomWebView {
  public static final String TAG = SmaatoLinkGrabber.class.getSimpleName();

  public SmaatoLinkGrabber(Context context, ReceivedBannerInterface banner, BaseView baseView)
          throws PixelToDpFailed, CloseButtonBitmapFailed {
    super(context, banner, baseView);
  }


}

package com.cloudbanter.adssdk.ad.manager;

import android.content.Context;
import android.util.Log;
import android.webkit.WebSettings;
import android.widget.RelativeLayout;

import com.smaato.soma.BannerView;
import com.smaato.soma.BaseView;
import com.smaato.soma.ExpandedBannerActivity;
import com.smaato.soma.ReceivedBannerInterface;
import com.smaato.soma.bannerutilities.AbstractBannerPackage;
import com.smaato.soma.exception.CloseButtonBitmapFailed;
import com.smaato.soma.exception.PixelToDpFailed;
import com.smaato.soma.internal.views.CustomWebView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Ugljesa Jovanovic (ionspinf@gmail.com) on 16-Nov-2016
 */
public class SmaatoWebView extends CustomWebView {
  public static final String TAG = SmaatoWebView.class.getSimpleName();

  public SmaatoWebView(Context context, ReceivedBannerInterface banner, BaseView baseView)
          throws PixelToDpFailed, CloseButtonBitmapFailed {
    super(context, banner, baseView);
    AbstractBannerPackage bannerPackage = ExpandedBannerActivity.getCurrentPackage();
    BannerView bannerView = (BannerView) bannerPackage.getBannerView();
    clearCache(true);
    setFocusable(true);

    try {
      getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
    } catch (RuntimeException var5) {
      ;
    } catch (Exception var6) {
      ;
    }

    getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
    if (bannerView != null) {
      setBackgroundColor(bannerView.getBackgroundColor());
    }

    WebSettings settings = getSettings();
    settings.setSupportZoom(false);
    settings.setBuiltInZoomControls(false);
    settings.setJavaScriptEnabled(true);

    try {
      settings.setMediaPlaybackRequiresUserGesture(false);
    } catch (NoSuchMethodError var4) {
      ;
    }

//        if(this.settingsSetter != null) {
//            this.settingsSetter.applyWebSettings(settings);
//        }
    settings.setLoadWithOverviewMode(true);

    settings.setUseWideViewPort(false);
    RelativeLayout.LayoutParams webViewParams;
//        if(bannerView.getAdSettings().getAdDimension() == AdDimension.MEDIUMRECTANGLE &&
// bannerView instanceof FullScreenBanner.FullScreenView) {
//            webViewParams = new RelativeLayout.LayoutParams(Converter.getInstance().pixelToDp
// (this.getContext(), 300), Converter.getInstance().pixelToDp(this.mContext, 250));
//        } else if(bannerView.getAdSettings().getAdDimension() == AdDimension
// .INTERSTITIAL_PORTRAIT && this.getBannerView() instanceof FullScreenBanner.FullScreenView) {
//            webViewParams = new RelativeLayout.LayoutParams(Converter.getInstance().pixelToDp
// (this.mContext, 320), Converter.getInstance().pixelToDp(this.mContext, 480));
//        } else if(bannerView.getAdSettings().getAdDimension() == AdDimension
// .INTERSTITIAL_LANDSCAPE && this.getBannerView() instanceof FullScreenBanner.FullScreenView) {
//            webViewParams = new RelativeLayout.LayoutParams(Converter.getInstance().pixelToDp
// (this.mContext, 480), Converter.getInstance().pixelToDp(this.mContext, 320));
//        } else {
//            webViewParams = new RelativeLayout.LayoutParams(-2, -1);
//        }
    Log.d(TAG, "Ad dimensions: " + bannerView.getAdSettings().getAdDimension().name());
    webViewParams = new RelativeLayout.LayoutParams(-2, -1);

    setLayoutParams(webViewParams);
    setVerticalScrollBarEnabled(false);
    setHorizontalScrollBarEnabled(false);
    setWebChromeClient(bannerPackage.getWebChromeClient());
    bannerPackage.getOrmmaBridge().setWebView(this);
    addJavascriptInterface(bannerPackage.getOrmmaBridge(), "smaato_bridge");
    try {
      Constructor constructor = null;
      for (Class clazz : AbstractBannerPackage.class.getDeclaredClasses()) {
        Log.d(TAG, "Class: " + clazz.getName());
        if (clazz.getName().contains("HtmlGetterJSInterface")) {
          Log.d(TAG, "Found class");
          for (Constructor constructorTmp : clazz.getDeclaredConstructors()) {
            Log.d(TAG, "Const: " + constructorTmp.getName());
            Log.d(TAG, "Parameter count: " + constructorTmp.getParameterTypes().length);
            for (Class paramType : constructorTmp.getParameterTypes()) {
              Log.d(TAG, "Param type: " + paramType.getName());
            }
            if (constructorTmp.getParameterTypes().length == 1) {
              constructor = constructorTmp;
              constructor.setAccessible(true);
            }
          }


        }
      }
      Object htmlGetterJsInterfaceObject = constructor.newInstance(bannerPackage);
      addJavascriptInterface(htmlGetterJsInterfaceObject, "HTMLOUT");
    } catch (IllegalAccessException e) {
      Log.e(TAG, "", e);
    } catch (InstantiationException e) {
      Log.e(TAG, "", e);
    } catch (InvocationTargetException e) {
      Log.e(TAG, "", e);
    }

  }

  @Override
  public void loadUrl(String url) {
    Log.d(TAG, "LoadUrl: " + url);
    super.loadUrl(url);
  }

  @Override
  public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding,
                                  String historyUrl) {
    Log.d(TAG, "Baseurl: " + baseUrl + " data: " + data);
    super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
  }
}

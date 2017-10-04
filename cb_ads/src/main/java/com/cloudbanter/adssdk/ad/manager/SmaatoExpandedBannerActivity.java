package com.cloudbanter.adssdk.ad.manager;

import android.os.Bundle;
import android.util.Log;

import com.smaato.soma.ExpandedBannerActivity;
import com.smaato.soma.exception.CloseButtonBitmapFailed;
import com.smaato.soma.exception.PixelToDpFailed;

/**
 * @author Ugljesa Jovanovic (ionspinf@gmail.com) on 16-Nov-2016
 */
public class SmaatoExpandedBannerActivity extends ExpandedBannerActivity {
  public static final String TAG = SmaatoExpandedBannerActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "SmaatoExpandedBannerActivity onCreate");
    try {
      SmaatoWebView smaatoWebView = new SmaatoWebView(
              this,
              getCurrentPackage().getBanner(),
              getCurrentPackage().getBannerView()
      );
      getCurrentPackage().setView(smaatoWebView);
      Log.d(TAG, "Set view");
      super.onCreate(savedInstanceState);

//            Field bannerWebViewField = ExpandedBannerActivity.class.getDeclaredField
// ("bannerView");
//            bannerWebViewField.setAccessible(true);
//            BannerView bannerView = (BannerView) bannerWebViewField.get(this);
//            if (bannerView == null){
//                Log.e(TAG, "Null banner view");
//                return;
//            }


    } catch (CloseButtonBitmapFailed closeButtonBitmapFailed) {
      Log.e(TAG, "", closeButtonBitmapFailed);
    } catch (PixelToDpFailed pixelToDpFailed) {
      Log.e(TAG, "", pixelToDpFailed);
    }

  }
}

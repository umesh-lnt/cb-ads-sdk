package com.cloudbanter.adssdk.ad.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cloudbanter.adssdk.ProxyManager;
import com.cloudbanter.adssdk.R;
import com.cloudbanter.adssdk.ad.manager.BannerGrabber;
import com.smaato.soma.ExpandedBannerActivity;
import com.smaato.soma.bannerutilities.AbstractBannerPackage;
import com.smaato.soma.bannerutilities.ImageBanner;
import com.smaato.soma.bannerutilities.RichMediaBanner;

public class CbWebBrowserActivity extends AppCompatActivity {

  public static final String TAG = CbWebBrowserActivity.class.getSimpleName();

  WebView webView;

  public static final String EXTRA_URL = "web_browser_url";
  public static final String EXTRA_SOURCE = "source";

  private boolean needAdUrlResolution = false;
  private boolean finishedLoading = false;
  private String lastUrl;
  private String source;

  private Handler handler;
  private Runnable finalPageLoadedRunnable;

  private int thisRunId;


  private ProgressDialog progressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cb_web_browser);
    thisRunId = (int) (Math.random() * Integer.MAX_VALUE);

    handler = new Handler();
    finalPageLoadedRunnable = new Runnable() {
      @Override
      public void run() {
        if (progressDialog.isShowing()) {
          progressDialog.dismiss();
          finishedLoading = true;
          Log.d(TAG, "Reporting last url: " + lastUrl);
          BannerGrabber.getInstance().reportFinalUrl(thisRunId, source, lastUrl);
        }
      }
    };

    Intent intent = getIntent();
    String url = (String) intent.getStringExtra(EXTRA_URL);
    source = intent.getStringExtra(EXTRA_SOURCE);
    if (source != null) {
      needAdUrlResolution = true;
    }
    if (url == null) {
      Log.d(TAG, "Component: " + intent.getComponent().toString());
      if (intent.getComponent().getClassName().contains("ExpandedBannerActivity")) {
        AbstractBannerPackage bannerPackage = ExpandedBannerActivity.getCurrentPackage();
        Log.d(TAG, "Banner package class: " + bannerPackage);
        if (bannerPackage instanceof ImageBanner) {
          Log.d(TAG, "Image banner");
          url = bannerPackage.getView().getUrl();
        }
        if (bannerPackage instanceof RichMediaBanner) {
          Log.d(TAG, "Rich media banner");
          url = bannerPackage.getView().getUrl();
        }
        Log.d(TAG, "Resolved smaato url: " + url);

      }

    }

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    if (url == null || url.equals("")) {
      finish();
    }

    Log.d(TAG, "Loading url: " + url);

    progressDialog = ProgressDialog.show(CbWebBrowserActivity.this,
            getResources().getString(R.string.text_loading_webpage), "", true, true);
    progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
      @Override
      public void onCancel(DialogInterface dialog) {
        finish();
      }
    });
    webView = (WebView) findViewById(R.id.browser_webview);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.setBackgroundColor(0);
    webView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d(TAG, "Requested url: " + url);
        if (!url.startsWith("http")) {
          Log.d(TAG, "Not a http url, we don't want to handle this");
          if (needAdUrlResolution) {
            BannerGrabber.getInstance().reportFinalUrl(thisRunId, source, url);
          }
          Intent chromeIntent = new Intent(Intent.ACTION_VIEW);
          chromeIntent.setData(Uri.parse(url));
          chromeIntent.putExtra(BannerGrabber.BYPASS_WEBVIEW_FILTER, "1");
          getApplication().startActivity(chromeIntent);
          finish();
          return false;
        }
        if (needAdUrlResolution) {
          if (finalPageLoadedRunnable != null) {
            handler.removeCallbacks(finalPageLoadedRunnable);
          }
          if (!finishedLoading) {
            Log.d(TAG, "This is a redirect! New url: " + url + " previous url: " + lastUrl);
          } else {
            Log.d(TAG, "This is user click");
          }
          lastUrl = url;
        }

        view.loadUrl(url);
        return true;
      }

      public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        Log.d(TAG, "ssl error " + error.toString());
        handler.cancel();
      }

      @Override
      public void onReceivedError(WebView view, WebResourceRequest request,
                                  WebResourceError error) {
        Log.d(TAG, "Error");
        super.onReceivedError(view, request, error);
      }

      public void onPageFinished(WebView view, String url) {
        Log.d(TAG, "Page Loaded: " + url);
        if (needAdUrlResolution) {
          if (!finishedLoading) {
            lastUrl = url;
          }
          handler.postDelayed(finalPageLoadedRunnable, 1000);
        } else {
          if (progressDialog.isShowing()) {
            progressDialog.dismiss();
          }
        }


      }


    });
    if (!url.startsWith("http")) {
      Log.d(TAG, "Not a http url, we don't want to handle this");
      if (needAdUrlResolution) {
        Log.d(TAG, "Reporting url: " + url);
        BannerGrabber.getInstance().reportFinalUrl(thisRunId, source, url);
      }
      if (url.contains("moat-bridge:")) {
        Log.w(TAG, "Moat-bridge url, crashes the app as it cannot be resolved, returning");
        finish();
        return;
      }
      Intent chromeIntent = new Intent(Intent.ACTION_VIEW);
      chromeIntent.setData(Uri.parse(url));
      chromeIntent.putExtra(BannerGrabber.BYPASS_WEBVIEW_FILTER, "1");
      getApplication().startActivity(chromeIntent);
      finish();
      return;
    }
    webView.loadUrl(url);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    switch (id) {
      case android.R.id.home:
        if (!needAdUrlResolution) {
          finish();
          return true;
        } else {
          Intent intent = new Intent(this, CloudbanterCentralActivity.class);
          startActivity(intent);
          finish();
          return true;
        }
    }
    return false;
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (webView != null) {
      ProxyManager.setProxy(webView, null, 0);
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    webView.onPause();
    ProxyManager.restoreProxyAfterLeavingWebView();
  }
}

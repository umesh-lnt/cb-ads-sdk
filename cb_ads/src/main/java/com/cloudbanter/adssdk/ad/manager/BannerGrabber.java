package com.cloudbanter.adssdk.ad.manager;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cloudbanter.adssdk.ad.ui.CbWebBrowserActivity;
import com.cloudbanter.adssdk.ad.util.CbSharedPreferences;
import com.facebook.ads.AdView;
import com.inmobi.ads.InMobiBanner;
import com.millennialmedia.internal.MMWebView;
import com.millennialmedia.internal.utils.Utils;
import com.mopub.mobileads.HtmlBannerWebView;
import com.mopub.mobileads.MoPubView;
import com.smaato.soma.BannerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Ugljesa Jovanovic (ionspinf@gmail.com) on 02-Sep-2016
 */
public class BannerGrabber {
  public static final String TAG = BannerGrabber.class.getSimpleName();
  
  public static final String SOURCE_GOOGLE_AD = "googleAd";
  public static final String SOURCE_FACEBOOK_AD = "facebookAd";
  public static final String SOURCE_MILLENIAL_AD = "millenialAd";
  public static final String SOURCE_INMOBI_AD = "inmobiAd";
  public static final String SOURCE_SMAATO_AD = "smaatoAd";
  public static final String SOURCE_MOPUB_MARKET_AD = "mopubMarketAd";
  
  public static final String SOURCE_UNKNOWN_SOURCE = "unknownSource";
  
  public static final String BYPASS_WEBVIEW_FILTER = "webViewBypass";
  
  public static String GRABBED_IMAGE_PATH;
  
  private static BannerGrabber instance;
  
  private Handler handler;
  
  private Context context;
  
  private com.google.android.gms.ads.AdView currentAdView;
  
  private View currentMoPubView;
  private String currentSource;
  private Map<String, Bitmap> currentSourceBitmapMap;
  
  private Integer lastReportId = -1;
  
  private List<ExternalBannerGrab> grabbedEntries;
  
  private BannerGrabber(Context context) {
    this.context = context;
    GRABBED_IMAGE_PATH = context.getFilesDir().toString();
    currentSourceBitmapMap = new HashMap<>();
    List<ExternalBannerGrab> storedGrabbedEntries = CbSharedPreferences.getGrabbedAds(context);
    if (storedGrabbedEntries == null) {
      this.grabbedEntries = new LinkedList<>();
    } else {
      this.grabbedEntries = storedGrabbedEntries;
    }
  }
  
  public static synchronized void init(Application applicationContext) {
    if (instance != null) {
      Log.w(TAG, "Banner grabber already initialized");
    } else {
      instance = new BannerGrabber(applicationContext);
    }
  }
  
  public static BannerGrabber getInstance() {
    return instance;
  }
  
  public String handleFavourites(ViewGroup banner) {
    Log.d(TAG, "Handling favourites");
    if (banner == null) {
      Log.d(TAG, "Null banner!");
      return null;
    }
    printViewTree(banner);
    
    
    InMobiBanner inMobiBanner = (InMobiBanner) findViewWithClass(banner, InMobiBanner.class);
    if (inMobiBanner != null) {
      Log.d(TAG, "Got inmobi: " + inMobiBanner.toString());
      currentMoPubView = inMobiBanner;
      currentSource = SOURCE_INMOBI_AD;
    }
    AdView facebookBannerView = (AdView) findViewWithClass(banner, AdView.class);
    if (facebookBannerView != null) {
      Log.d(TAG, "Got facebook: " + facebookBannerView.toString());
      currentMoPubView = facebookBannerView;
      currentSource = SOURCE_FACEBOOK_AD;
    }
    
    com.google.android.gms.ads.AdView googleBannerView =
            (com.google.android.gms.ads.AdView) findViewWithClass(
                    banner,
                    com.google.android.gms.ads.AdView.class
            );
    if (googleBannerView != null) {
      Log.d(TAG, "Got google banner: " + googleBannerView.toString());
      currentMoPubView = googleBannerView;
      currentSource = SOURCE_GOOGLE_AD;
    }
    
    MMWebView millenialBannerView = (MMWebView) findViewWithClass(banner, MMWebView.class);
    if (millenialBannerView != null) {
      handleMillenialBanner(millenialBannerView);
      currentMoPubView = millenialBannerView;
      currentSource = SOURCE_MILLENIAL_AD;
    }
    HtmlBannerWebView mopubHtmlBanner =
            (HtmlBannerWebView) findViewWithClass(banner, HtmlBannerWebView.class);
    if (mopubHtmlBanner != null) {
      Log.d(TAG, "Got mopub banner");
      currentMoPubView = mopubHtmlBanner;
      currentSource = SOURCE_MOPUB_MARKET_AD;
      handleMarketplaceBanner(mopubHtmlBanner);
    }
    BannerView smaatoBannerView = (BannerView) findViewWithClass(banner, BannerView.class);
    if (smaatoBannerView != null) {
      Log.d(TAG, "Got smaato banner");
      currentMoPubView = smaatoBannerView;
      currentSource = SOURCE_SMAATO_AD;
//            handleSmaatoBanner
    }
    if (currentMoPubView == null) {
      currentMoPubView = banner;
      currentSource = SOURCE_UNKNOWN_SOURCE;
    }
    
    Log.d(TAG, "Scheduling banner grab");
    handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "Grabbing bitmap");
        Bitmap bitmap = getBitmapFromView(currentMoPubView);
        if (bitmap == null) {
          Log.e(TAG, "Couldn't grab bitmap! Source: " + currentSource);
        }
        currentSourceBitmapMap.put(currentSource, bitmap);
        
        
      }
    }, 500);
    
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "Grabbing bitmap fallback attempt");
        Bitmap bitmap = getBitmapFromView(currentMoPubView);
        if (bitmap == null) {
          Log.e(TAG, "Couldn't grab bitmap! Source: " + currentSource);
        }
        currentSourceBitmapMap.put(currentSource, bitmap);
        
        
      }
    }, 1500);
    
    
    return currentSource;
    
    
  }
  
  private File saveBitmapToFile(Bitmap bitmap, String source) {
    if (bitmap != null) {
      final String dir = GRABBED_IMAGE_PATH;
      File outputFile = new File(dir + source + "-banner-" + System.currentTimeMillis() + ".png");
      if (outputFile.exists()) {
        outputFile.delete();
      }
      FileOutputStream fos = null;
      try {
        fos = new FileOutputStream(outputFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        return outputFile;
      } catch (FileNotFoundException e) {
        Log.e(TAG, "", e);
      } catch (IOException e) {
        Log.e(TAG, "", e);
      }
    }
    return null;
  }
  
  public void storeBitmapForSmato(View view) {
    handler = new Handler();
    //Try to get a bitmap immediately
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "Grabbing smaato bitmap");
        Bitmap bitmap = getBitmapFromView(currentMoPubView);
        if (bitmap == null) {
          Log.e(TAG, "Couldn't grab bitmap! Source: " + currentSource);
        }
        currentSourceBitmapMap.put(currentSource, bitmap);
        
        
      }
    }, 500);
    //And after some time, if the first suceeded, this will just overwrite with same data
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "Grabbing smaato bitmap");
        Bitmap bitmap = getBitmapFromView(currentMoPubView);
        if (bitmap == null) {
          Log.e(TAG, "Couldn't grab bitmap! Source: " + currentSource);
        }
        currentSourceBitmapMap.put(currentSource, bitmap);
        
        
      }
    }, 6000); //Smato javascript can take a LOT of time to load
  }
  
  public void handleFavouritesGoogleBypass(com.google.android.gms.ads.AdView adView) {
    Log.d(TAG, "Bypass for google");
    Bitmap bitmap = getBitmapFromView(adView);
    final String dir = Environment.getExternalStorageDirectory() + "/" +
            Environment.DIRECTORY_DOWNLOADS + "/";
    File outputFile = new File(dir + "banner-" + System.currentTimeMillis() + ".png");
    if (outputFile.exists()) {
      outputFile.delete();
    }
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(outputFile);
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
      fos.flush();
      
    } catch (FileNotFoundException e) {
      Log.e(TAG, "", e);
    } catch (IOException e) {
      Log.e(TAG, "", e);
    }
  }
  
  private Bitmap getBitmapFromView(View view) {
    if (view == null) {
      Log.e(TAG, "Null view, can't grab bitmap, returning");
      return null;
    }
    view.clearFocus();
    view.setPressed(false);
    boolean willNotCacheValue = view.willNotCacheDrawing();
    view.setWillNotCacheDrawing(false);
    
    int color = view.getDrawingCacheBackgroundColor();
    view.setDrawingCacheBackgroundColor(Color.WHITE);
    
    if (color != 0) {
      view.destroyDrawingCache();
    }
    view.buildDrawingCache();
    Bitmap cacheBitmap = view.getDrawingCache();
    if (cacheBitmap == null) {
      Log.e(TAG, "Bitmap retrieval failed!");
      cacheBitmap = loadBitmapFromViewFallback(view);
      if (cacheBitmap == null) {
        Log.e(TAG, "Bitmap fallback failed!");
        return null;
      }
      
    }
    
    Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
    
    view.destroyDrawingCache();
    view.setWillNotCacheDrawing(willNotCacheValue);
    view.setDrawingCacheBackgroundColor(color);
    return bitmap;
  }
  
  public Bitmap loadBitmapFromViewFallback(View v) {
    if (v.getLayoutParams() != null && v.getLayoutParams().width > 0 &&
            v.getLayoutParams().height > 0) {
      Bitmap b = Bitmap.createBitmap(v.getLayoutParams().width, v.getLayoutParams().height,
              Bitmap.Config.ARGB_8888);
      Canvas c = new Canvas(b);
      v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
      v.draw(c);
      return b;
    } else {
      Log.e(TAG, "Banner with 0 width and height");
      return null;
    }
  }
  
  private View findViewWithClass(ViewGroup rootView, Class<?> targetClass) {
    if (targetClass.isInstance(rootView)) {
      Log.d(TAG, "Root view is appropriate class");
      return rootView;
    }
    int childCount = rootView.getChildCount();
//        Log.d(TAG, "Child count: " + childCount);
    for (int i = 0; i < childCount; i++) {
      View childView = rootView.getChildAt(i);
//            Log.d(TAG, "Child: " + childView);
      if (targetClass.isInstance(childView)) {
//                Log.d(TAG, "Found: " + targetClass);
        return childView;
      } else {
        if (childView instanceof ViewGroup) {
          View result = findViewWithClass((ViewGroup) childView, targetClass);
          if (result != null) {
            return result;
          }
          
        }
      }
    }
//        Log.d(TAG, "Couldn't find view with type: " + targetClass);
    return null;
    
  }
  
  private static void printViewTree(View view) {
    printViewTreeInternal(view, 0);
  }
  
  private static void printViewTreeInternal(View view, int level) {
    ViewGroup viewGroup = null;
    StringBuffer levelBuffer = new StringBuffer();
    if (level > 0) {
      levelBuffer.append("|");
      for (int i = 0; i < level; i++) {
        levelBuffer.append('-');
      }
      Log.d(TAG, levelBuffer.toString() + " " + view.getClass().getSimpleName());
    } else {
      Log.d(TAG, view.getClass().getSimpleName());
    }
    if (view instanceof ViewGroup) {
      viewGroup = (ViewGroup) view;
      for (int i = 0; i < viewGroup.getChildCount(); i++) {
        printViewTreeInternal(viewGroup.getChildAt(i), level + 1);
      }
    } else {
      return;
    }
    
    
  }
  
  private void handleMillenialBanner(MMWebView millenialBannerView) {
    if (millenialBannerView != null) {
      Log.d(TAG, "Got millenial banner: " + millenialBannerView);
      Log.d(TAG, "Trying to get url");
      
      Class mmWebViewClientClass = null;
      Class[] mmWebViewClasses = MMWebView.class.getDeclaredClasses();
      for (Class clazz : mmWebViewClasses) {
        Log.d(TAG, "Declared class: " + clazz.getSimpleName());
        if (clazz.getSimpleName().contains("MMWebViewClient")) {
          mmWebViewClientClass = clazz;
        }
      }
      if (mmWebViewClientClass != null) {
        Log.d(TAG, "Trying to intercept url");
        
        try {
          //public void onReceivedError(WebView webView, int errorCode, String description,
          // String failingUrl) {
          final Method clientOnReceiveErrorMethod =
                  mmWebViewClientClass.getDeclaredMethod(
                          "onReceivedError",
                          WebView.class, int.class, String.class, String.class
                  );
          //public boolean shouldOverrideUrlLoading(WebView webView, String url) {
          final Method clientShouldOverrideUrlLoadingMethod =
                  mmWebViewClientClass.getDeclaredMethod(
                          "shouldOverrideUrlLoading",
                          WebView.class, String.class
                  );
          Constructor clientConstructor = mmWebViewClientClass.getDeclaredConstructor();
          clientConstructor.setAccessible(true);
          final Object clientObject = clientConstructor.newInstance();
          WebViewClient interceptorClient = new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                                        String failingUrl) {
              try {
                clientOnReceiveErrorMethod.invoke(clientObject, view, errorCode, description,
                        failingUrl);
              } catch (IllegalAccessException e) {
                Log.e(TAG, "", e);
              } catch (InvocationTargetException e) {
                Log.e(TAG, "", e);
              }
            }
            
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
              try {
                Log.d(TAG, "OVERRIDE URL: " + url + "from view: " + view);
                if (view instanceof MMWebView) {
                  MMWebView mmWebView = (MMWebView) view;
                  Method isOriginalUrlMethod = MMWebView.class.getDeclaredMethod(
                          "isOriginalUrl",
                          String.class
                  );
                  isOriginalUrlMethod.setAccessible(true);
                  if (((Boolean) isOriginalUrlMethod.invoke(mmWebView, url))) {
                    return true;
                  } else {
                    if (Utils.startActivityFromUrl(url)) {
                      Intent intent = new Intent(context, CbWebBrowserActivity.class);
                      intent.putExtra(CbWebBrowserActivity.EXTRA_URL, url);
                      intent.putExtra(CbWebBrowserActivity.EXTRA_SOURCE, SOURCE_MILLENIAL_AD);
                      context.startActivity(intent);
                      
                      Field webViewListenerField =
                              MMWebView.class.getDeclaredField("webViewListener");
                      webViewListenerField.setAccessible(true);
                      MMWebView.MMWebViewListener webViewListener =
                              (MMWebView.MMWebViewListener) webViewListenerField.get(mmWebView);
                      webViewListener.onAdLeftApplication();
                      
                      
                    }
                    
                    return true;
                  }
                } else {
                  return false;
                }
              } catch (IllegalAccessException e) {
                Log.e(TAG, "", e);
              } catch (InvocationTargetException e) {
                Log.e(TAG, "", e);
              } catch (NoSuchMethodException e) {
                Log.e(TAG, "", e);
              } catch (NoSuchFieldException e) {
                Log.e(TAG, "", e);
              }
              return false;
            }
          };
          millenialBannerView.setWebViewClient(interceptorClient);
          
        } catch (NoSuchMethodException e) {
          Log.e(TAG, "", e);
        } catch (IllegalAccessException e) {
          Log.e(TAG, "", e);
        } catch (InstantiationException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      } else {
        Log.d(TAG, "Class was null!");
      }
      
    }
  }
  
  private void handleMarketplaceBanner(HtmlBannerWebView banner) {
    if (banner != null) {
      
    }
  }
  
  public void handleGoogleBanner(com.google.android.gms.ads.AdView adView) {
    currentAdView = adView;
    
  }
  
  public void handleGoogleIntent(Intent intent) {
    if (intent != null) {
      Log.d(TAG, "Handling google show ad intent ");
      Log.d(TAG, "Intent: " + intent.getType());
      Log.d(TAG, "Class: " + intent.getComponent().flattenToString());
      Bundle extras = intent.getExtras();
      if (extras != null) {
        for (String key : extras.keySet()) {
          Log.d(TAG, "Key: " + key + " value: " + extras.get(key));
          if (key.equals("com.google.android.gms.ads.inernal.overlay.AdOverlayInfo")) {
            Bundle innerBundle = extras.getBundle(key);
            for (String innerKey : innerBundle.keySet()) {
              Log.d(TAG, "InnerKey: " + innerKey + " value: " + innerBundle.get(innerKey));
              if (innerKey.equals("com.google.android.gms.ads.inernal.overlay.AdOverlayInfo")) {
                Object parcelable = innerBundle.get(innerKey);
                if (parcelable != null) {
                  Log.d(TAG, "Got parcelable, trying to modify");
                  
                  try {
                    Class parcelableClass = parcelable.getClass();
                    Field adLauncherField = parcelableClass.getDeclaredField("b");
                    adLauncherField.setAccessible(true);
                    Class adLauncherClass = adLauncherField.getType();
                    Field intentField = adLauncherClass.getDeclaredField("i");
                    intentField.setAccessible(true);
                    Object adLauncher = adLauncherField.get(parcelable);
                    Intent launcherIntent = (Intent) intentField.get(adLauncher);
                    if (launcherIntent != null) {
                      Log.d(TAG, "Got intent! Component: " +
                              launcherIntent.getComponent().flattenToString());
                      launcherIntent.setComponent(
                              new ComponentName(context, CbWebBrowserActivity.class));
                      launcherIntent.putExtra(CbWebBrowserActivity.EXTRA_URL,
                              launcherIntent.getData().toString());
                      launcherIntent.putExtra(CbWebBrowserActivity.EXTRA_SOURCE, SOURCE_GOOGLE_AD);
                      
                    }
                  } catch (IllegalAccessException e) {
                    Log.e(TAG, "", e);
                  } catch (NoSuchFieldException e) {
                    Log.e(TAG, "", e);
                  }
                } else {
                  Log.d(TAG, "Failed when trying to get parcelable");
                }
              }
            }
          }
        }
      }
    }
  }
  
  public void handleMoPubMarketplaceBanner(Intent intent) {
    Log.d(TAG, "Replacing component browser with modified browser");
    intent.putExtra(CbWebBrowserActivity.EXTRA_URL,
            intent.getStringExtra(ModifiedMoPubBrowser.DESTINATION_URL_KEY));
    intent.putExtra(CbWebBrowserActivity.EXTRA_SOURCE, SOURCE_MOPUB_MARKET_AD);
    intent.setComponent(new ComponentName(context, CbWebBrowserActivity.class));
    
  }
  
  public void reportFinalUrl(int id, String source, String url) {
    Log.d(TAG, "Reporting back from web view: " + id + " source: " + source + " url: " + url);
    List<ExternalBannerGrab> storedGrabbedEntries = CbSharedPreferences.getGrabbedAds(context);
    if (storedGrabbedEntries != null) {
      grabbedEntries = storedGrabbedEntries;
    }
    if (lastReportId != null) {
      if (lastReportId == id) {
        Log.w(TAG, "Double report, discarding");//TODO investigate cause
        
        return;
      }
    } else {
      lastReportId = id;
    }
    if ((currentSourceBitmapMap.get(source) != null) || source.equals(SOURCE_UNKNOWN_SOURCE)) {
      if (source.equals(SOURCE_UNKNOWN_SOURCE)) {
        Log.w(TAG, "Unknown source! Replacing with current");
        source = currentSource;
        
      }
      if (grabbedEntries == null) {
        Log.e(TAG, "Grabbed entries not loaded yet!");
        return;
      }
      for (ExternalBannerGrab grab : grabbedEntries) {
        if (grab.getBannerUrl().equals(url)) {
          Log.w(TAG, "Duplicate banner: " + url);
          return;
        }
      }
      Log.d(TAG, "Source match! Saving banner");
      for (String key : currentSourceBitmapMap.keySet()) {
        Log.d(TAG, "Key: " + key);
        if (currentSourceBitmapMap.get(key) != null) {
          Log.d(TAG, "Bitmap: " + currentSourceBitmapMap.get(key).toString());
        } else {
          Log.d(TAG, "Bitmap: Null");
        }
      }
      Bitmap bitmap = currentSourceBitmapMap.get(source);
      File bannerSaveFile = saveBitmapToFile(bitmap, source);
      ExternalBannerGrab externalBannerGrab = new ExternalBannerGrab(bannerSaveFile, url);
      
      grabbedEntries.add(externalBannerGrab);
      CbSharedPreferences.setGrabbedAds(context, grabbedEntries);
    } else {
      Log.e(TAG, "Source not present in the bitmap map");
      Log.e(TAG, "Source: " + source);
    }
  }
  
  public List<ExternalBannerGrab> getGrabbedEntries() {
    return grabbedEntries;
  }
  
  public void removeEntry(ExternalBannerGrab entry) {
    if (grabbedEntries.remove(entry)) {
      Log.d(TAG, "Entry removed");
    } else {
      Log.w(TAG, "No entry to remove");
    }
    CbSharedPreferences.setGrabbedAds(context, grabbedEntries);
    
  }
  
  private void replaceMoPubContext(MoPubView moPubView) {
    
  }
  
  
}



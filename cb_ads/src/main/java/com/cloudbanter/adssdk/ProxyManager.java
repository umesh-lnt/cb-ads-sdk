package com.cloudbanter.adssdk;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.ProxyInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Parcelable;
import android.util.ArrayMap;
import android.util.Log;
import android.webkit.WebView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;

/**
 * Created by Ugljesa Jovanovic (jovanovic.ugljesa@gmail.com) on 01-Nov-2016.
 */
public class ProxyManager {
  public static final String TAG = ProxyManager.class.getSimpleName();

  public static final String PROXY_CHANGE_ACTION = "android.intent.action.PROXY_CHANGE";

  private WifiManager wifiManager;
  private static WeakReference<Context> contextReference;


  public static void init(Context context) {
    if (CbAdsSdk.PROXY_ADS) {
      restoreProxyAfterLeavingWebView();
      contextReference = new WeakReference<>(context);
      ProxySelector defaultProxySelector = ProxySelector.getDefault();
      List<Proxy> proxyList = defaultProxySelector.select(URI.create("http://www.google.com"));
      Log.d(TAG, "For http:");
      for (Proxy proxy : proxyList) {
        Log.d(TAG, "Proxy: " + proxy.toString());
      }
      Log.d(TAG, "For https:");
      proxyList = defaultProxySelector.select(URI.create("https://www.google.com"));
      for (Proxy proxy : proxyList) {
        Log.d(TAG, "Proxy: " + proxy.toString());

      }
    } else {
      Log.d(TAG, "Not using proxy for ads");
    }

  }


  public static boolean setProxy(WebView webview, String host, int port) {
    if (CbAdsSdk.PROXY_ADS) {
      String applicationClassName = CbAdsSdk.class.getName();
      return setProxyForWebView(webview, host, port, applicationClassName);
    }
    return false;

  }

  public static void restoreProxyAfterLeavingWebView() {
    if (CbAdsSdk.PROXY_ADS) {
      if (contextReference != null && contextReference.get() != null) {
        ConnectivityManager connectivityManager = (ConnectivityManager) contextReference.get()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
          Log.d(TAG, "Connected to wifi, not using proxy");
          System.setProperty("http.proxyHost", "");
          System.setProperty("http.proxyPort", "");
          System.setProperty("https.proxyHost", "");
          System.setProperty("https.proxyPort", "");
          return;
        } else {
          Log.d(TAG, "Not on wifi, using proxy");

          System.setProperty("http.proxyHost", CbAdsSdk.PROXY_URL);
          System.setProperty("http.proxyPort", CbAdsSdk.PROXY_PORT);
          System.setProperty("https.proxyHost", CbAdsSdk.PROXY_URL);
          System.setProperty("https.proxyPort", CbAdsSdk.PROXY_PORT);
        }
      }
//            Authenticator.setDefault(new Authenticator() {
//                @Override
//                protected PasswordAuthentication getPasswordAuthentication() {
//                    if (getRequestorType() == RequestorType.PROXY){
//                        Log.d(TAG, "Proxy request: " + getRequestorType());
//                        Log.d(TAG, "" + getRequestingHost());
//                        Log.d(TAG, "" + getRequestingPort());
//                        Log.d(TAG, "" + getRequestingURL().toString());
//                        return new PasswordAuthentication(MmsConfig.PROXY_USERNAME, MmsConfig
// .PROXY_PASSWORD.toCharArray());
//                    } else {
//                        Log.d(TAG, "Requestor type: " + getRequestorType());
//                    }
//                    return null;
//                }
//            });
    }
  }


  // from https://stackoverflow.com/questions/19979578/android-webview-set-proxy-programatically
  // -kitkat
  private static boolean setProxyForWebView(WebView webView, String host, int port,
                                            String applicationClassName) {
    Log.d(TAG, "Setting proxy, application class name: " + applicationClassName);
    Log.d(TAG, "Host: " + host);
    Log.d(TAG, "Port: " + port);
    if (host == null) {
      host = "";
    }

    Context appContext = webView.getContext().getApplicationContext();
    System.setProperty("http.proxyHost", host);
    System.setProperty("http.proxyPort", port + "");
    System.setProperty("https.proxyHost", host);
    System.setProperty("https.proxyPort", port + "");
    try {
      Class applictionCls = Class.forName(applicationClassName);
      Field loadedApkField = applictionCls.getField("mLoadedApk");
      loadedApkField.setAccessible(true);
      Object loadedApk = loadedApkField.get(appContext);
      Class loadedApkCls = Class.forName("android.app.LoadedApk");
      Field receiversField = loadedApkCls.getDeclaredField("mReceivers");
      receiversField.setAccessible(true);
      ArrayMap receivers = (ArrayMap) receiversField.get(loadedApk);
      for (Object receiverMap : receivers.values()) {
        for (Object rec : ((ArrayMap) receiverMap).keySet()) {
          Class clazz = rec.getClass();
          if (clazz.getName().contains("ProxyChangeListener")) {
            Log.d(TAG, "Found receiver: " + clazz.getName());
            Method onReceiveMethod =
                    clazz.getDeclaredMethod("onReceive", Context.class, Intent.class);
            Intent intent = new Intent(PROXY_CHANGE_ACTION);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
              Log.d(TAG, "Using proxy properties");
              final String CLASS_NAME = "android.net.ProxyProperties";
              Class cls = Class.forName(CLASS_NAME);
              Constructor constructor =
                      cls.getConstructor(String.class, Integer.TYPE, String.class);
              constructor.setAccessible(true);
              Object proxyProperties = constructor.newInstance(host, port, null);
              intent.putExtra("proxy", (Parcelable) proxyProperties);
            } else {
              Log.d(TAG, "Using proxy info");
              ProxyInfo proxyInfo = ProxyInfo.buildDirectProxy(host, port);
              intent.putExtra("proxy", proxyInfo);

            }

            onReceiveMethod.invoke(rec, appContext, intent);
          }
        }
      }
//            Authenticator.setDefault(new Authenticator() {
//                @Override
//                protected PasswordAuthentication getPasswordAuthentication() {
//                    if (getRequestorType() == RequestorType.PROXY){
//                        Log.d(TAG, "Web view proxy request: " + getRequestorType());
//                        Log.d(TAG, "" + getRequestingHost());
//                        Log.d(TAG, "" + getRequestingPort());
//                        Log.d(TAG, "" + getRequestingURL().toString());
//                    } else {
//                        Log.d(TAG, "Requestor type: " + getRequestorType());
//                        Log.d(TAG, "Url: " + getRequestingURL().toString());
//                    }
//                    return null;
//                }
//            });

      Log.d(TAG, "Setting proxy successful!");
      return true;
    } catch (ClassNotFoundException | NoSuchFieldException | IllegalArgumentException |
            NoSuchMethodException | InvocationTargetException | InstantiationException |
            IllegalAccessException e) {
      Log.e(TAG, "", e);
    }
    return false;
  }

  public static void notifyAboutProxy(Context context, String host, String port,
                                      String applicationClassName) {
    Log.d(TAG, "Setting proxy, application class name: " + applicationClassName);
    Log.d(TAG, "Host: " + host);
    Log.d(TAG, "Port: " + port);
    if (host == null) {
      host = "";
    }
    if (contextReference.get() == null) {
      contextReference = new WeakReference<>(context);
    }
    ConnectivityManager connectivityManager =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
      Log.d(TAG, "Connected to wifi, not using proxy");
      host = "";
      port = "0";
    } else {
      Log.d(TAG, "Not on wifi, using proxy");
    }


    Context appContext = context.getApplicationContext();
    System.setProperty("http.proxyHost", host);
    System.setProperty("http.proxyPort", port + "");
    System.setProperty("https.proxyHost", host);
    System.setProperty("https.proxyPort", port + "");
    try {
      Class applictionCls = Class.forName(applicationClassName);
      Field loadedApkField = applictionCls.getField("mLoadedApk");
      loadedApkField.setAccessible(true);
      Object loadedApk = loadedApkField.get(appContext);
      Class loadedApkCls = Class.forName("android.app.LoadedApk");
      Field receiversField = loadedApkCls.getDeclaredField("mReceivers");
      receiversField.setAccessible(true);
      ArrayMap receivers = (ArrayMap) receiversField.get(loadedApk);
      for (Object receiverMap : receivers.values()) {
        for (Object rec : ((ArrayMap) receiverMap).keySet()) {
          Class clazz = rec.getClass();
          Log.d(TAG, "Receiver: " + clazz.getSimpleName());
          if (clazz.getName().contains("ProxyChangeListener")) {
            Log.d(TAG, "Found receiver: " + clazz.getName());
            Method onReceiveMethod =
                    clazz.getDeclaredMethod("onReceive", Context.class, Intent.class);
            Intent intent = new Intent(PROXY_CHANGE_ACTION);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
              Log.d(TAG, "Using proxy properties");
              final String CLASS_NAME = "android.net.ProxyProperties";
              Class cls = Class.forName(CLASS_NAME);
              Constructor constructor =
                      cls.getConstructor(String.class, Integer.TYPE, String.class);
              constructor.setAccessible(true);
              Object proxyProperties = constructor.newInstance(host, port, null);
              intent.putExtra("proxy", (Parcelable) proxyProperties);
            } else {
              Log.d(TAG, "Using proxy info");
              ProxyInfo proxyInfo = ProxyInfo.buildDirectProxy(host, Integer.parseInt(port));
              intent.putExtra("proxy", proxyInfo);

            }

            onReceiveMethod.invoke(rec, appContext, intent);
          }
        }
      }
//            Authenticator.setDefault(new Authenticator() {
//                @Override
//                protected PasswordAuthentication getPasswordAuthentication() {
//                    if (getRequestorType() == RequestorType.PROXY){
//                        Log.d(TAG, "Web view proxy request: " + getRequestorType());
//                        Log.d(TAG, "" + getRequestingHost());
//                        Log.d(TAG, "" + getRequestingPort());
//                        Log.d(TAG, "" + getRequestingURL().toString());
//                    } else {
//                        Log.d(TAG, "Requestor type: " + getRequestorType());
//                        Log.d(TAG, "Url: " + getRequestingURL().toString());
//                    }
//                    return null;
//                }
//            });

      Log.d(TAG, "Setting proxy successful!");
    } catch (ClassNotFoundException e) {
      Log.e(TAG, "", e);
    } catch (NoSuchFieldException e) {
      Log.e(TAG, "", e);
    } catch (IllegalAccessException e) {
      Log.e(TAG, "", e);
    } catch (IllegalArgumentException e) {
      Log.e(TAG, "", e);
    } catch (NoSuchMethodException e) {
      Log.e(TAG, "", e);
    } catch (InvocationTargetException e) {
      Log.e(TAG, "", e);
    } catch (InstantiationException e) {
      Log.e(TAG, "", e);
    }
  }
}

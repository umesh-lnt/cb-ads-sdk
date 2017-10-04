package com.cloudbanter.adssdk.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utility class that has common and standar network operations
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 17/3/17
 */
public final class NetworkUtils {
  
  /** Tag for logs **/
  private static final String TAG = NetworkUtils.class.getSimpleName();
  
  /**
   * Checks if there's internet connection
   *
   * @param context
   *         Original context
   *
   * @return True if there's internet connection
   */
  public static boolean isOnline(Context context) {
    ConnectivityManager cm =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    return netInfo != null && netInfo.isConnectedOrConnecting();
  }
  
}

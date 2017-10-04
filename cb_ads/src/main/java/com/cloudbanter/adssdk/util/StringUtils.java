package com.cloudbanter.adssdk.util;

import android.support.annotation.NonNull;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Set;

/**
 * @author <a href="mailto:aajn88@gmail.com">Antonio</a>
 * @since 8/8/17
 */
public final class StringUtils {
  
  private static final String TAG = StringUtils.class.getSimpleName();
  
  private StringUtils() {}
  
  @NonNull
  public static String concatByComa(Collection collection) {
    if (collection == null || collection.isEmpty()) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (Object o : collection) {
      if (!first) {
        sb.append(",");
      } else {
        first = false;
      }
      sb.append(o);
    }
    return sb.toString();
  }
  
  public static void checkAndAddKeyword(Set<String> usedKeywords, String message,
                                        String baseKeyword) {
    if (message.contains(baseKeyword)) {
      usedKeywords.add(baseKeyword);
    }
  }
  
  public static final String md5(final String s) {
    try {
      // Create MD5 Hash
      MessageDigest digest = MessageDigest
              .getInstance("MD5");
      digest.update(s.getBytes());
      byte messageDigest[] = digest.digest();
      
      // Create Hex String
      StringBuilder hexString = new StringBuilder();
      for (byte aMessageDigest : messageDigest) {
        String h = Integer.toHexString(0xFF & aMessageDigest);
        while (h.length() < 2) {
          h = "0" + h;
        }
        hexString.append(h);
      }
      return hexString.toString();
      
    } catch (NoSuchAlgorithmException e) {
      Log.e(TAG, "Error finding algorithm", e);
    }
    return "";
  }
  
}

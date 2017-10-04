package com.cloudbanter.adssdk.util;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.model.ad_blender.Sms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.cloudbanter.adssdk.util.StringUtils.checkAndAddKeyword;

/**
 * Sms utils for basic and common SMS management
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 30/1/17
 */
public final class SmsUtils {
  
  /**
   * Retrieves all sms sorted using date field
   *
   * @param requestCode
   *         Request code
   * @param context
   *         Requesting activity
   * @param callback
   *         Callback used to return the result of the query
   */
  public static void getAllSms(final int requestCode, final Context context,
                               final GetSmsCallback callback) {
    final Uri smsUri = Uri.parse("content://sms/");
    
    final CursorLoader cursorLoader = new CursorLoader(context, smsUri, null, null, null, "date");
    final Loader.OnLoadCompleteListener<Cursor> listener =
            new Loader.OnLoadCompleteListener<Cursor>() {
              @Override
              public void onLoadComplete(Loader<Cursor> loader, Cursor cursor) {
                List<Sms> smsList = retrieveSms(cursor);
                destroyCursorLoader(cursorLoader, this);
                callback.onSmsRetrieved(requestCode, smsList);
              }
            };
    cursorLoader.registerListener(requestCode, listener);
    cursorLoader.startLoading();
  }
  
  private static void destroyCursorLoader(CursorLoader cursorLoader,
                                          Loader.OnLoadCompleteListener<Cursor> listener) {
    if (cursorLoader != null) {
      cursorLoader.unregisterListener(listener);
      cursorLoader.cancelLoad();
      cursorLoader.stopLoading();
    }
  }
  
  @NonNull
  private static List<Sms> retrieveSms(Cursor c) {
    int totalSms = c.getCount();
    List<Sms> lstSms = new ArrayList<>(totalSms);
    
    if (c.moveToFirst()) {
      for (int i = 0; i < totalSms; i++) {
        
        Sms objSms = new Sms();
        objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
        objSms.setAddress(c.getString(c.getColumnIndex("address")));
        objSms.setMsg(c.getString(c.getColumnIndex("body")));
        objSms.setRead(c.getString(c.getColumnIndex("read")).contains("1"));
        objSms.setTime(c.getLong(c.getColumnIndex("date")));
        objSms.setSent(c.getString(c.getColumnIndex("type")).contains("0"));
        
        lstSms.add(objSms);
        c.moveToNext();
      }
      c.close();
    } else {
      lstSms = Collections.emptyList();
    }
    return lstSms;
  }
  
  public static List<String> extractKeywordsList(List<Sms> smsList) {
    List<String> baseKeywords = CbAdsSdk.getKeywords();
    if (smsList == null || smsList.isEmpty() || baseKeywords == null || baseKeywords.isEmpty()) {
      return new ArrayList<>(0);
    }
    Set<String> usedKeywords = new HashSet<>();
    for (Sms sms : smsList) {
      String message = sms.getMsg() == null ? "" : sms.getMsg().toLowerCase();
      for (String baseKeyword : baseKeywords) {
        checkAndAddKeyword(usedKeywords, message, baseKeyword);
      }
    }
    return new ArrayList<>(usedKeywords);
  }
  
  /**
   * Used to return the result of requesting SMS using {@link #getAllSms(int, Context,
   * GetSmsCallback)}
   */
  public interface GetSmsCallback {
    
    /**
     * Used to return the information gather from the sms query
     *
     * @param requestCode
     *         Original request code
     * @param smsList
     *         Retrieves SMS list
     */
    void onSmsRetrieved(int requestCode, List<Sms> smsList);
  }
  
}

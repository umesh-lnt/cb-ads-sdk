package com.cloudbanter.adssdk.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.ad.model.CbDevice;
import com.cloudbanter.adssdk.ad.model.CbSchedule;
import com.cloudbanter.adssdk.ad.repo.DatabaseFactory;
import com.cloudbanter.adssdk.ad.service.CbRestService;
import com.cloudbanter.adssdk.ad.service.callbacks.CallbackHandler;
import com.cloudbanter.adssdk.ad.service.callbacks.ICallback;
import com.cloudbanter.adssdk.ad.service.callbacks.IGenerateScheduleCallback;
import com.cloudbanter.adssdk.ad.service.callbacks.IGetDefaultScheduleCallback;
import com.cloudbanter.adssdk.ad.util.CbSharedPreferences;
import com.cloudbanter.adssdk.model.ad_blender.Sms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class for Schedule common methods
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 8/2/17
 */
public class ScheduleUtils {
  
  /** Tag for logs **/
  private static final String TAG = ScheduleUtils.class.getSimpleName();
  
  /** Callbacks **/
  private static final List<ICallback> CALLBACKS = new ArrayList<>();
  
  /** Private constructor to avoid instances **/
  private ScheduleUtils() {
  }
  
  /**
   * Generates a new schedule based on keywords
   *
   * @param context
   *         Base context
   */
  public static void generateSchedule(final Context context) {
    generateSchedule(context, null);
  }
  
  /**
   * Generates a new schedule based on keywords
   *
   * @param context
   *         Base context
   * @param callback
   *         Callback to return the result
   */
  public static void generateSchedule(final Context context, final ScheduleCallback callback) {
    SmsUtils.getAllSms(0, context, new SmsUtils.GetSmsCallback() {
      @Override
      public void onSmsRetrieved(int requestCode, final List<Sms> smsList) {
        scheduleAds(context, smsList, callback);
      }
    });
  }
  
  /**
   * Gets the default schedule
   *
   * @param context
   *         Base context
   * @param callback
   *         Callback to return the result
   */
  @SuppressWarnings("unchecked")
  public static void getDefaultSchedule(final Context context, final ScheduleCallback callback) {
    final ICallback defaultScheduleCallback = new IGetDefaultScheduleCallback() {
      @Override
      public void onGetDefaultScheduleComplete(CbSchedule obj) {
        onSuccess(obj);
      }
      
      @Override
      public void handleError(String s) {
        if (callback != null) {
          callback.onScheduleFailure(new Throwable(s));
        }
      }
      
      @Override
      public void onSuccess(Object data) {
        if (callback != null) {
          callback.onScheduleSuccess();
        }
      }
    };
    CALLBACKS.add(defaultScheduleCallback);
    Intent defaultAdsIntent = CbRestService.getDefaultScheduleIntent(context, new CallbackHandler(
            defaultScheduleCallback));
    context.startService(defaultAdsIntent);
  }
  
  /**
   * Gets the stored schedule
   *
   * @param context
   *         Context
   *
   * @return The stored schedule. If there's no schedule, then null is returned
   */
  public static CbSchedule getGeneratedSchedule(Context context) {
    return DatabaseFactory.getCbDatabase(context).getCbSchedule();
  }
  
  /**
   * Schedule ads given a sms list to match keywords
   *
   * @param context
   *         Base context
   * @param smsList
   *         List of sms
   */
  @SuppressWarnings("unchecked")
  private static void scheduleAds(Context context, List<Sms> smsList,
                                  final ScheduleCallback callback) {
    Log.i(TAG, "Starting gathering of scheduled ads");
    List<String> keywords = SmsUtils.extractKeywordsList(smsList);
    keywords.addAll(extractCustomKeywordsList(context));
    
    CbDevice cbDevice = CbDevice.restore(context);
    if (cbDevice == null) {
      return;
    }
    Log.d(TAG, "Device: " + cbDevice.phoneNumber);
    final ICallback generateScheduleCallback = new IGenerateScheduleCallback() {
      @Override
      public void onGenerateScheduleComplete(CbSchedule obj) {
        onSuccess(obj);
      }
      
      @Override
      public void handleError(String s) {
        if (callback != null) {
          callback.onScheduleFailure(new Throwable(s));
        }
      }
      
      @Override
      public void onSuccess(Object data) {
        if (callback != null) {
          callback.onScheduleSuccess();
        }
      }
    };
    CALLBACKS.add(generateScheduleCallback);
    Intent getScheduleIntent =
            CbRestService.getScheduleIntent(context, new CallbackHandler(generateScheduleCallback),
                    cbDevice, keywords);
    context.startService(getScheduleIntent);
  }
  
  private static List<String> extractCustomKeywordsList(Context context) {
    List<String> customKeywords = CbSharedPreferences.getCustomKeywordsPreferences(context);
    List<String> baseKeywords = CbAdsSdk.getKeywords();
    if (customKeywords.isEmpty() || baseKeywords == null || baseKeywords.isEmpty()) {
      return new ArrayList<>(0);
    }
    Set<String> matchedKeywords = new HashSet<>();
    for (String customKeyword : customKeywords) {
      for (String baseKeyword : baseKeywords) {
        StringUtils.checkAndAddKeyword(matchedKeywords, customKeyword, baseKeyword);
      }
    }
    return new ArrayList<>(matchedKeywords);
  }
  
  /**
   * Used to notify when schedule process has successfully finished or if it has failed
   */
  public interface ScheduleCallback {
    
    /**
     * The requested schedule was successfully loaded
     */
    void onScheduleSuccess();
    
    /**
     * The requested schedule has failed
     *
     * @param t
     *         The thrown exception
     */
    void onScheduleFailure(Throwable t);
    
  }
  
}

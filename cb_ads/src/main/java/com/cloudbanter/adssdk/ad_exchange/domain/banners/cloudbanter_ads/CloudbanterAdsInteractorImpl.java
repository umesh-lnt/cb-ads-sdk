package com.cloudbanter.adssdk.ad_exchange.domain.banners.cloudbanter_ads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.cloudbanter.adssdk.ad.manager.DefaultSchedule;
import com.cloudbanter.adssdk.ad.model.CbSchedule;
import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;
import com.cloudbanter.adssdk.ad_exchange.domain.banners.common.OnNextAdCallback;
import com.cloudbanter.adssdk.util.ScheduleUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Implementation of the {@link CloudbanterAdsInteractor}
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 10/3/17
 */
class CloudbanterAdsInteractorImpl implements CloudbanterAdsInteractor,
        ScheduleUtils.ScheduleCallback {

  /** Tag for logs **/
  private static final String TAG = CloudbanterAdsInteractorImpl.class.getSimpleName();

  /** Console Ads queue **/
  private Queue<CbScheduleEntry> mConsoleAdsQueue = new LinkedList<>();

  /** Context **/
  private Context mContext;

  /** Flag used to know if the current schedule request is for operator or default ads **/
  private boolean mIsDefaultAds;

  /**
   * Constructor
   */
  CloudbanterAdsInteractorImpl(Context context) {
    this.mContext = context;
    generateSchedule(context);
  }

  /**
   * Generates the operator schedule
   *
   * @param context
   *         Context
   */
  private void generateSchedule(Context context) {
    Log.i(TAG, "Loading operator ads");
    ScheduleUtils.generateSchedule(context, this);
  }

  @Override
  public void getNextAd(@NonNull OnNextAdCallback callback) {
    if (mConsoleAdsQueue.isEmpty()) {
      callback.onNoAdsAvailable();
      return;
    }

    CbScheduleEntry entry = mConsoleAdsQueue.poll();
    mConsoleAdsQueue.add(entry);
    callback.onNextAd(entry);
  }

  /**
   * Loads the scheduled ads if they wasn't loaded already
   *
   * @return True if the schedule was successfully loaded, that means that it wasn't null or empty
   */
  private boolean loadScheduledAdsIfNeeded() {
    CbSchedule schedule = mIsDefaultAds ? DefaultSchedule.getSchedule() :
            ScheduleUtils.getGeneratedSchedule(mContext);
    return replaceQueuedSchedule(mConsoleAdsQueue, schedule);
  }

  /**
   * Replaces the existing enqueued schedule with a given schedule. If schedule is null or empty,
   * then it is not replaced
   *
   * @param adsQueue
   *         Target ads queue
   * @param schedule
   *         Schedule to be replaced
   *
   * @return True if the schedule was successfully replaced, that means that it wasn't null or empty
   */
  private boolean replaceQueuedSchedule(Queue<CbScheduleEntry> adsQueue, CbSchedule schedule) {
    if (schedule == null || schedule.entries == null || schedule.entries.length == 0) {
      return false;
    }
    adsQueue.clear();
    Collections.addAll(adsQueue, schedule.entries);
    Log.i(TAG, "Schedule loaded");
    return true;
  }

  @Override
  public void onScheduleSuccess() {
    if (!loadScheduledAdsIfNeeded()) {
      onScheduleFailure(new Throwable("Invalid schedule"));
    } else {
      Log.i(TAG,
              "Schedule successfully loaded " + (mIsDefaultAds ? "Default ads" : "Operator ads"));
    }
  }

  @Override
  public void onScheduleFailure(Throwable t) {
    Log.e(TAG, "Failed to load the schedule", t);
    if (!mIsDefaultAds) {
      Log.i(TAG, "Loading default ads");
      mIsDefaultAds = true;
      ScheduleUtils.getDefaultSchedule(mContext, this);
    }
  }
}

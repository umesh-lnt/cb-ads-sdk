package com.cloudbanter.adssdk.ad_exchange.domain.banners.preload_ads;

import android.support.annotation.NonNull;
import android.util.Log;

import com.cloudbanter.adssdk.ad.manager.PreloadSchedule;
import com.cloudbanter.adssdk.ad.model.CbSchedule;
import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;
import com.cloudbanter.adssdk.ad_exchange.domain.banners.common.OnNextAdCallback;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Implementation of the {@link PreloadedAdsInteractor}
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 10/3/17
 */
class PreloadedAdsInteractorImpl implements PreloadedAdsInteractor {

  /** Tag for logs **/
  private static final String TAG = PreloadedAdsInteractorImpl.class.getSimpleName();

  /** Preloaded Ads queue **/
  private Queue<CbScheduleEntry> mPreloadedAdsQueue = new LinkedList<>();

  /**
   * Constructor
   */
  PreloadedAdsInteractorImpl() {
    loadPreloadedAds();
  }

  /**
   * Loads the preloaded ads
   */
  private void loadPreloadedAds() {
    Log.i(TAG, "Loading preloaded ads");
    CbSchedule schedule = PreloadSchedule.getSchedule();
    replaceQueuedSchedule(mPreloadedAdsQueue, schedule);
  }

  @Override
  public void getNextAd(@NonNull OnNextAdCallback callback) {
    if (mPreloadedAdsQueue.isEmpty()) {
      callback.onNoAdsAvailable();
      return;
    }

    CbScheduleEntry entry = mPreloadedAdsQueue.poll();
    mPreloadedAdsQueue.add(entry);
    callback.onNextAd(entry);
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
}

package com.cloudbanter.adssdk.ad.manager;

import android.util.Log;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.ad.manager.images.ImageRef;
import com.cloudbanter.adssdk.ad.model.CbSchedule;
import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;
import com.cloudbanter.adssdk.ad.repo.CbDatabase;
import com.cloudbanter.adssdk.ad.repo.DatabaseFactory;


/**
 * Created by eric on 5/2/16.
 */
public class DefaultSchedule extends CbSchedule {
  public static final String TAG = CbSchedule.class.getSimpleName();

  private static CbSchedule schedule = null;

  public static void startImageDownload() {
    for (CbScheduleEntry entry : schedule.entries) {
      ImageRef.newImageDetailDownload(CbAdsSdk.getApplication().getApplicationContext(),
              entry.advert);
    }
  }

  public static CbSchedule setSchedule(CbSchedule schedule) {
    DefaultSchedule.schedule = (CbSchedule) schedule;  // TODO check default schedule flag
    schedule.scheduleType = "DEFAULT";
    save();
    startImageDownload();
    return schedule;
  }

  public static CbSchedule getSchedule() {
    Log.d(TAG, "getting schedule " + schedule);
    return schedule;
  }

  public static void save() {
    CbDatabase database =
            DatabaseFactory.getCbDatabase(CbAdsSdk.getApplication().getApplicationContext());
    database.saveDefaultSchedule(schedule);
  }

  public static void restore() {
    CbDatabase database =
            DatabaseFactory.getCbDatabase(CbAdsSdk.getApplication().getApplicationContext());
    schedule = database.getDefaultSchedule();
  }

  public static boolean hasDefault() {
    return null != schedule;
  }

}

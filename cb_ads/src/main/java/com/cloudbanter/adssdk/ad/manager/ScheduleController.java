package com.cloudbanter.adssdk.ad.manager;

import android.content.Context;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.ad.model.AModel;

import java.util.ArrayList;

/**
 * Created by eric on 8/18/15.
 */
public class ScheduleController {

  private static LoadedSchedules ls = new LoadedSchedules();

  public static void add(String scheduleId) {
    if (ls.contains(scheduleId)) {
      return;
    } else {
      ls.add(scheduleId);
    }
  }

  public static boolean contains(String scheduleId) {
    return ls.contains(scheduleId);
  }
// triggers for schedule update

  public static class LoadedSchedules extends AModel<LoadedSchedules> {
    ArrayList loadedSchedules = new ArrayList<String>();

    public void add(String id) {
      loadedSchedules.add(id);
    }

    ;

    public boolean contains(String id) {
      return loadedSchedules.contains(id);
    }

    // persist schedule data
    public String toJson() {
      return gson.toJson(this, ScheduleController.class);
    }

    public static ScheduleController fromJson(String json) {
      return (ScheduleController) gson.fromJson(json, ScheduleController.class);
    }
  }

  // TODO persist loadedSchedules refs
  static final Context mContext = CbAdsSdk.getApplication().getApplicationContext();

  public static void save() {
    // DatabaseFactory.getCbDatabase(mContext).upsertLoadedSchedules(ls);
  }

  public static void restore() {
    // ls = DatabaseFactory.getCbDatabase(mContext).getCbLoadedSchedules();
  }
}

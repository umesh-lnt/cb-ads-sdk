package com.cloudbanter.adssdk.ad.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// transient class used for cb-central database access object
public class CbCentral extends AModel<CbSchedule> {
  /**
   *
   */
  private static final long serialVersionUID = -4987729123683589157L;

  public final String name = "CbCentral";
  public String deviceId;
  public String createdAt;
  public String updatedAt;
  public CbScheduleEntry[] entries;

  public CbCentral(ArrayList<CbScheduleEntry> entries) {
    this.addEntries(entries);
  }

  public CbCentral addEntries(List<CbScheduleEntry> list) {
    entries = new CbScheduleEntry[list.size()];
    int i = 0;
    for (CbScheduleEntry e : list) {
      entries[i++] = e;
    }
    return this;
  }

  public List<CbScheduleEntry> getEntries() {
    return new ArrayList<CbScheduleEntry>(Arrays.asList(entries));
  }

  public String toJson() {
    return gson.toJson(this, CbCentral.class);
  }

  public static CbCentral fromJson(String json) {
    return (CbCentral) gson.fromJson(json, CbCentral.class);
  }

}

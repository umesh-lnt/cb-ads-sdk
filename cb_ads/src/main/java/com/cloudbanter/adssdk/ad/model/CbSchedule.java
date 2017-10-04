package com.cloudbanter.adssdk.ad.model;


import com.cloudbanter.adssdk.ad.util.ArrayUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by eric on 8/18/15.
 */
public class CbSchedule extends AModel<CbSchedule> {
  public String name;
  public String deviceId;
  public String createdAt;
  public String updatedAt;
  public String scheduleType;

  // used for json conversion
  public CbScheduleEntry[] entries;

  // list of schedule entries
  private HashMap<String, CbScheduleEntry> entryMap = new HashMap<String, CbScheduleEntry>();
  private TreeMap<String, CbScheduleEntry> displayOrderMap = new TreeMap<String, CbScheduleEntry>();

  public CbScheduleEntry addItem(CbScheduleEntry entry) {
    entryMap.put(entry._id, entry);
    displayOrderMap.put(entry._id, entry);  // TODO fix order hack...
    return entry;
  }

  public void addAll(CbScheduleEntry[] entries) {
    for (CbScheduleEntry entry : entries) {
      // Log.d("CbSchedule", "ADDALL(" + (null == entry.advertiser ? "ADEMPTY) " : "e) ") + entry
      // .toJson().toString() );
      if (null == entry) {
        return;
      }
      entry.displayOrder = entry._id;  // TODO fix order hack...
      if (null != entry.advert && null != entry.advertiser) {
        entry.advert.advertiser = entry.advertiser;
      }
      entry.advert.setImageIds();
      entry.advert.setImageRefs();

      addItem(entry);
    }
  }

  public CbSchedule replaceAll(Collection<CbScheduleEntry> entries) {
    entryMap = new HashMap<String, CbScheduleEntry>();
    displayOrderMap = new TreeMap<String, CbScheduleEntry>();
    for (CbScheduleEntry ent : entries) {
      addItem(ent);
    }
    return this;
  }

  public HashMap<String, CbScheduleEntry> getEntries() {
    return entryMap;
  }

  public TreeMap<String, CbScheduleEntry> getDisplayOrderMap() {
    return displayOrderMap;
  }

  public String toJson() {
    entries = new CbScheduleEntry[entryMap.size()];
    int i = 0;
    for (CbScheduleEntry entry : entryMap.values()) {
      entries[i++] = entry;
    }
    return gson.toJson(this, CbSchedule.class);
  }

  public static CbSchedule fromJson(String json) {
    CbSchedule schedule = (CbSchedule) gson.fromJson(json, CbSchedule.class);
    if (null != schedule.entries) {
      schedule.addAll(schedule.entries);
    }
    return schedule;
  }

  public CbScheduleEntry findEntryById(String id) {
    return entryMap.get(id);
  }

  public boolean removeEntry(String id) {
    CbScheduleEntry entry = entryMap.remove(id);
    displayOrderMap.remove(id);
    entries = ArrayUtils.removeElement(CbScheduleEntry.class, entries, entry);
    return true;
  }

  public boolean isEmpty() {
    return entryMap.isEmpty();
  }

  public boolean isPrefs() {
    return "PREFS".equalsIgnoreCase(scheduleType);
  }

  public boolean isDefault() {
    return "DEFAULT".equalsIgnoreCase(scheduleType);
  }

  public boolean isPreload() {
    return "PRELOAD".equalsIgnoreCase(scheduleType);
  }
}

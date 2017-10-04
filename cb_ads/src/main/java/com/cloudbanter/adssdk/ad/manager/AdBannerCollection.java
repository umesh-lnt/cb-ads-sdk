package com.cloudbanter.adssdk.ad.manager;

import android.text.TextUtils;
import android.util.Log;

import com.cloudbanter.adssdk.ad.manager.images.CbImageManager;
import com.cloudbanter.adssdk.ad.manager.images.Images;
import com.cloudbanter.adssdk.ad.model.CbSchedule;
import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

public class AdBannerCollection {
  private static final String TAG = "AdBannerCollection";

  private static AdCollection mAdvertCollection = new AdCollection();

  private static String curKey = null;

  private static CbImageManager mImageManager = CbImageManager.mImageManager;

  public static CbScheduleEntry getCurrent() {
    return mAdvertCollection.get(curKey);
  }

  public static CbScheduleEntry getNext() {
    mAdvertCollection.applyLimitations();
    if (mAdvertCollection.isEmpty() || hasNoImages()) {
      curKey = null;
      return null;
    } else if (null == curKey || null == (curKey = mAdvertCollection.higherKey(curKey))) {
      curKey = mAdvertCollection.firstKey();
    } else if (!Images.isPresent((mAdvertCollection.get(curKey)).advert.bannerImageFileName)) {
      // TODO kludgy recursion - will have already failed if no images.
      Log.d(TAG, "file not found recursing: " +
              (mAdvertCollection.get(curKey)).advert.bannerImageFileName);
      getNext();
    }
    return mAdvertCollection.get(curKey);
  }

  public static ArrayList<CbScheduleEntry> getAll() {
    return new ArrayList<CbScheduleEntry>(mAdvertCollection.values());
  }


  public static void removeItem(String id) {
    if (null == id) {
      return;
    }
    mAdvertCollection.remove(id);
  }

  public static CbSchedule updateWithNewSchedule(CbSchedule sched) {
    if (null == sched) {
      sched = new CbSchedule();
    }
    if (null == mAdvertCollection) {
      mAdvertCollection = new AdCollection();
    }

    AdCollection tmpAds = new AdCollection();
    if (null != sched.entries) {
      for (CbScheduleEntry entry : sched.entries) {
        if (tmpAds.keySet().contains(entry.advert._id) ||
                CloudbanterCentral.contains(entry.advert._id)) {
          continue;
        } else {
          tmpAds.addEntry(entry);
        }
      }
    }

    mAdvertCollection = tmpAds;
    return sched.replaceAll(tmpAds.getEntryArray());
  }

  public static CbSchedule addAll(CbSchedule sched) {
    mAdvertCollection = new AdCollection();
    mAdvertCollection.addAll(sched);
    return sched;
  }
  // TODO update sync routing
  /*


  public CbSchedule syncSchedule(CbSchedule sched) {
    if (null == sched || sched.entries.length == 0) return sched;
    if (null == mAdvertCollection) mAdvertCollection = new TreeMap<String, CbScheduleEntry>();
    TreeMap<String, CbAdvert> adverts = new TreeMap<String, CbAdvert>();

    // check to see if the ads are already in the devices collection.
    for (CbScheduleEntry entry: mAdvertCollection.values()) {
      adverts.put(entry.advert._id, entry.advert);
    }
    for (CbScheduleEntry entry: sched.entries) {
      if (mAdvertCollection.keySet().contains(entry._id)
        || adverts.keySet().contains(entry.advert._id))
        continue;
      else
        mAdvertCollection.addEntry(entry);
    }

    // check to see if the ads are already in the devices collection.
    // back fill the schedule from extra adverts.
    ArrayList<String> schedEntries = new ArrayList<String>();
    for (CbScheduleEntry e: sched.entries) schedEntries.add(e.advert._id);

    for (CbScheduleEntry e: mAdvertCollection.values()) {
      if (schedEntries.contains(e._id))
              continue;
      else
        sched.addItem(e);
    }
    // TODO
    // update CbSched entries array

      return sched;
  }
  */

  // inefficient - aargh - 
  private static boolean hasNoImages() {
    for (String key : mAdvertCollection.keySet()) {
      CbScheduleEntry entry = mAdvertCollection.get(key);
      if (null != entry.advert
              && !TextUtils.isEmpty(entry.advert.bannerImageFileName)
              && Images.isPresent((mAdvertCollection.get(key)).advert.bannerImageFileName)) {
        return false;
      }
    }
    return true;
  }

  public static boolean isEmpty() {
    return null == mAdvertCollection || mAdvertCollection.isEmpty();
  }

  public static class AdCollection extends TreeMap<String, CbScheduleEntry> {

    public CbScheduleEntry addEntry(CbScheduleEntry entry) {
      if (null == entry) {
        return null;
      }
      this.put(entry._id, entry);
      return entry;
    }

    public AdCollection addAll(CbSchedule sched) {
      for (CbScheduleEntry entry : sched.entries) {
        addEntry(entry);
      }
      return this;
    }

    public Collection<CbScheduleEntry> getEntryArray() {
      return this.values();
    }

    public void applyLimitations() {
      return;
    /*
    Iterator<Entry<String, CbScheduleEntry>> i = this.entrySet().iterator();
    Entry<String, CbScheduleEntry> entry;
    Long date = new Date().getTime();
    while(i.hasNext()) {
      entry = i.next();
      if (entry.startDate > date || entry.endDate < date) {
        i.remove();
      }
      // TODO additional remove criteria here.
    }
    */
    }
  }

}

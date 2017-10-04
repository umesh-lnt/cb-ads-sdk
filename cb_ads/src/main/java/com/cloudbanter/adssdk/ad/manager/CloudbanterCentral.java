package com.cloudbanter.adssdk.ad.manager;

import android.util.Log;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.ad.model.CbCentral;
import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;
import com.cloudbanter.adssdk.ad.repo.CbDatabase;
import com.cloudbanter.adssdk.ad.repo.DatabaseFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * class used to manage full ads displayed in cloudbanter central
 *
 * @author eric
 */
public class CloudbanterCentral {
  public static final String TAG = CloudbanterCentral.class.getSimpleName();

  static {
    Log.d(TAG, "Creating CloudbanterCentral");

  }

  // k need adverts...downloaded from server
  private static ArrayList<CbScheduleEntry> ITEMS = new ArrayList<CbScheduleEntry>();
  private static Map<String, CbScheduleEntry> ITEM_MAP = new HashMap<String, CbScheduleEntry>();

  // add one 
  // add from click
  // if not in Map already...
  public static void addItem(CbScheduleEntry item) {
    Log.d(TAG, "Adding item to central: " + item._id + " advertiser: " + item.advertiser);
    Log.d(TAG, "Previous size: " + ITEMS.size());
    if (!ITEM_MAP.containsKey(item._id)) {
      ITEMS.add(item);
      ITEM_MAP.put(item._id, item);
    }
    saveCbCentral();
    Log.d(TAG, "Size after adding ad: " + ITEMS.size());
  }

  public static synchronized ArrayList<CbScheduleEntry> getItems() {
    Log.d(TAG, "Returning number of items: " + ITEMS.size());
    return ((ArrayList<CbScheduleEntry>) ITEMS.clone());
  }

  public static CbScheduleEntry getItem(String itemId) {
    return ITEM_MAP.get(itemId);
  }

  public static CbScheduleEntry getItem(int pos) {
    return ITEMS.get(pos);
  }

  public static boolean contains(String itemId) {
    return null != ITEM_MAP.get(itemId);
  }

  public static boolean isEmpty() {
    return ITEM_MAP.isEmpty() || ITEMS.isEmpty();
  }

  public static void removeItem(String itemId) {
    Log.d(TAG, "Removing item: " + itemId);
    CbScheduleEntry ad = ITEM_MAP.get(itemId);
    if (null != ad) {
      for (Iterator<CbScheduleEntry> iter = ITEMS.listIterator(); iter.hasNext(); ) {
        CbScheduleEntry item = iter.next();
        if (ad == item) {
          iter.remove();
        }
      }
      ITEM_MAP.remove(itemId);
      saveCbCentral();
    }
  }

  public static synchronized void addAll(List<CbScheduleEntry> entries) {
    for (Iterator<CbScheduleEntry> i = entries.iterator(); i.hasNext(); ) {
      addItem((CbScheduleEntry) i.next());
    }
  }

  // clear all
  public static synchronized void clearAll() {
    Log.d(TAG, "Removing all items from cb central");
    ITEMS = new ArrayList<CbScheduleEntry>();
    ITEM_MAP = new HashMap<String, CbScheduleEntry>();
  }

  // SQLite persistence
  public static void saveCbCentral() {
    CbDatabase database = DatabaseFactory.getCbDatabase(CbAdsSdk.getApplication());
    database.upsert(new CbCentral(ITEMS));
  }

  // add from stored
  public static void restoreCbCentral() {
    clearAll();
    CbDatabase database =
            DatabaseFactory.getCbDatabase(CbAdsSdk.getApplication().getApplicationContext());
    CbCentral central = database.getCbCentral();
    Log.d(TAG, "Retrieved cb central: " + central);
    if (central != null && central.entries != null) {
      Log.d(TAG, "Entries: " + central.entries.length);
      addAll(central.getEntries());
    }

  }

  public static void init() {
    restoreCbCentral();
  }
}

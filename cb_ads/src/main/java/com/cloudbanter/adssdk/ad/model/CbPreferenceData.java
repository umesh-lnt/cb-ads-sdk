package com.cloudbanter.adssdk.ad.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CbPreferenceData extends AModel<CbPreferenceData> {
  private static final String TAG = "CbPref";
  private static final String ATTRIBUTED = "attributed";
  private static final String CUSTOM = "custom";
  private static final String PREFERENCES = "preferences";

  public Map<String, String> attributed = new HashMap<String, String>();
  public List<String> custom = new ArrayList<String>();

  public String toJson() {
    JSONObject prefs = new JSONObject();
    JSONObject attr = new JSONObject();
    JSONArray cust = new JSONArray();

    try {
      for (Entry<String, String> e : attributed.entrySet()) {
        attr.put(e.getKey(), e.getValue());
      }
      for (String s : custom) {
        cust.put(s);
      }
      prefs.put(ATTRIBUTED, attr);
      prefs.put(CUSTOM, cust);

      // Wrap entire preferences object
      JSONObject prefData = new JSONObject();
      prefData.put(PREFERENCES, prefs);
      Log.d(TAG, prefData.toString());

      return prefData.toString();
    } catch (JSONException je) {
      Log.d(TAG, je.getMessage());
      return "";
    }
  }

  public static CbPreferenceData fromJson(String json) {
    try {
      // TODO fix...
      boolean notFixed = true;
      Log.d(TAG, "de-serialize preferences broken");
      if (notFixed) {
        return null;
      }

      JSONObject o = new JSONObject(json);
      CbPreferenceData pref = new CbPreferenceData();
      // this doesn't work - have to detangle the the object heirarchy...
      pref.attributed = (Map<String, String>) o.getJSONObject(ATTRIBUTED);
      pref.custom = (List) o.getJSONArray(CUSTOM);
      return pref;
    } catch (JSONException je) {
      Log.d(TAG, je.getMessage());
      return null;
    }
  }

  public void addAttrPref(String attrib, Object value) {
    if (value instanceof String) {
      // index hack for male female...
      if (((String) value).equalsIgnoreCase("Hombre")) {
        value = "male";
      } else if (((String) value).equalsIgnoreCase("Mujer")) {
        value = "female";
      }

      attributed.put(attrib, (String) value);
    } else if (value instanceof Boolean) {
      attributed.put(attrib, (Boolean) value ? "true" : "false");
    }
  }

  public void addPref(String pref) {
    custom.add(pref.replaceAll("&", "%26").toUpperCase());
  }

}

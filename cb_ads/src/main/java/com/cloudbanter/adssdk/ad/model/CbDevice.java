package com.cloudbanter.adssdk.ad.model;

import android.content.Context;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.ad.util.CbSharedPreferences;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.util.Calendar;

/**
 * Created by eric on 7/9/15.
 */
public class CbDevice extends AModel<CbDevice> {

  public static final String ACTIVE = "active";

  public String phoneNumber;
  public String IMEI;
  public CbSchedule schedule;
  public String jwt;
  public Integer messages;
  public CbMobileOperator mobile_operator;
  public String operatorName;
  public String countryCode;
  public String createdAt;
  public String updatedAt;
  public CbUserInfo userInfo;
  public String status;
  public String buildInfo = CbAdsSdk.sVersionName;
  public Long registerDate;

  public CbDevice() {
  }

  public CbDevice(String s) {
    phoneNumber = s;
  }

  public CbDevice(String id, String mo_id) {
    _id = id;
    // mobile_operator = new CbUser(mo_id);
  }

  // handle conversion between app device and API devicew
  public static CbDevice convert(CbDevice2 d2) {
    CbDevice d = new CbDevice();
    d._id = d2._id;
    d.jwt = d2.jwt;
    d.createdAt = d2.createdAt;
    d.updatedAt = d2.updatedAt;
    d.phoneNumber = d2.phoneNumber;
    d.mobile_operator = new CbMobileOperator(d2.mobile_operator,
            "");  // TODO - d2 received object has mobile operator objectid / no email address...
    d.userInfo = d2.userInfo;
    d.status = d2.status;

    // schedule

    return d;
  }

  public String toJson() {
    if (null == userInfo) {
      userInfo = new CbUserInfo(true);
      registerDate = Calendar.getInstance().getTimeInMillis();
    }
    return gson.toJson(this, CbDevice.class);
  }


  public static CbDevice fromJson(String json) throws JsonSyntaxException {
    try {
      return (CbDevice) gson.fromJson(json, CbDevice.class);
    } catch (JsonSyntaxException e) {
      CbDevice2 d2 = CbDevice2.fromJson(json);
      return convert(d2);
    }
  }

  // Persist:
  public void save(Context context) {
    CbSharedPreferences.setCbDevice(context, this.toJson());
  }

  public static CbDevice restore(Context context) {
    String device = CbSharedPreferences.getCbDevice(context);
    if (null != device && device.length() > 0) {
      return CbDevice.fromJson(device);
    } else {
      return null;
    }
  }

  public CbDevice regSync(String body) {
    JsonParser jp = new JsonParser();
    JsonElement je = jp.parse(body);
    JsonObject jo = je.getAsJsonObject();

    _id = jo.get("_id").getAsString();
    createdAt = jo.get("createdAt").getAsString();
    updatedAt = jo.get("updatedAt").getAsString();
    jwt = jo.get("jwt").getAsString();
    mobile_operator._id = jo.get("mobile_operator").getAsString();
    status = jo.get("status").getAsString();

    // TODO set preferences if already registered. 

    return this;
  }

}

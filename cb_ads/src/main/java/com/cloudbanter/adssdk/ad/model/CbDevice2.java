package com.cloudbanter.adssdk.ad.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

// hack for device structure mis match...
public class CbDevice2 extends AModel<CbDevice2> {

  String createdAt;
  String updatedAt;
  String jwt;
  String mobile_operator;
  String phoneNumber;
  String schedule;
  String messages;
  CbUserInfo userInfo;
  String status;

  public String toJson() {
    return gson.toJson(this, CbDevice2.class);
  }

  public static CbDevice2 fromJson(String json) {
    try {
      return (CbDevice2) gson.fromJson(json, CbDevice2.class);
    } catch (JsonSyntaxException je) {
      return getCbDevice2(json);
    }
  }

  public static CbDevice2 getCbDevice2(String json) throws JsonSyntaxException {
    JsonParser jp = new JsonParser();
    JsonElement je = jp.parse(json);
    JsonObject jo = je.getAsJsonObject();

    CbDevice2 d2 = new CbDevice2();
    d2._id = jo.get("_id").getAsString();
    d2.createdAt = jo.get("createdAt").getAsString();
    d2.updatedAt = jo.get("updatedAt").getAsString();
    d2.jwt = jo.get("jwt").getAsString();
    d2.phoneNumber = jo.get("phoneNumber").getAsString();
    d2.mobile_operator = jo.get("mobile_operator").getAsString();
    d2.schedule = jo.get("schedule").getAsString();
    d2.messages = jo.get("messages").getAsString();
    d2.status = jo.get("status").getAsString();
    CbUserInfo u = new CbUserInfo();
    //jo.get("preferences").getAsString();
    d2.userInfo = u;

    return d2;
  }

}

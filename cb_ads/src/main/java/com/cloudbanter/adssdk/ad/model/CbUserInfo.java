package com.cloudbanter.adssdk.ad.model;

public class CbUserInfo extends AModel<CbUserInfo> {

  public String name;
  public String email;
  public String age;
  public String gender;

  public CbUserInfo() {
    this(false);
  }

  public CbUserInfo(boolean defaultData) {
    if (defaultData) {
      name = "Default";
      email = "default@default.com";
      age = "25";
      gender = "male";
    }
  }

  public String toJson() {
    return gson.toJson(this, CbUserInfo.class);
  }

  public CbUserInfo fromJson(String json) {
    return (CbUserInfo) gson.fromJson(json, CbUserInfo.class);
  }
}

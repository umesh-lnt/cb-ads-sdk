package com.cloudbanter.adssdk.ad.model;

import java.util.Date;

/**
 * Created by eric on 8/18/15.
 */
public class CbUser extends AModel<CbUser> {

  public String name;
  public String email;
  public Number balance;
  public Boolean acceptedAgreement;
  public CbTransaction[] transactions;
  public String role;
  public String countryFlag;
  public String companyLogo;
  public String hashedPassword;
  public String provider;
  public String salt;
  public Date createdAt;
  public Date updatedAt;

  public String toJson() {
    return gson.toJson(this, CbUser.class);
  }

  public CbUser fromJson(String json) {
    return (CbUser) gson.fromJson(json, CbUser.class);
  }

}

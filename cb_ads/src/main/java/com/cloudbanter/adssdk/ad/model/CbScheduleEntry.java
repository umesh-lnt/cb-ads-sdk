package com.cloudbanter.adssdk.ad.model;


import android.text.TextUtils;

/**
 * Created by eric on 8/18/15.
 */
public class CbScheduleEntry extends AModel<CbScheduleEntry> {

  private String name;
  private String schedule;
  private Boolean showNow;
  public String displayOrder;
  private Boolean showText;
  private Boolean showDisplay;
  private String useAck;
  private String useDisplay;
  public CbAdvert advert;
  private String advertText;
  public String advertiser;
  private String adImageURI;
  private String timeout;
  private String createdAt;
  private String updatedAt;
  private String created;
  public boolean isPreload;
  public boolean isSaved;
  private Boolean isDisplayed;
  public String startDate;
  public String endDate;

  public String toJson() {
    return gson.toJson(this, CbScheduleEntry.class);
  }

  public static CbScheduleEntry fromJson(String json) {
    CbScheduleEntry entry = (CbScheduleEntry) gson.fromJson(json, CbScheduleEntry.class);
    if (null != entry.advert && !TextUtils.isEmpty(entry.advertiser)) {
      entry.advert.advertiser = entry.advertiser;
    } else {
      entry.advert.advertiser = "MISSING ADVERTISER_ID";
    }
    return entry;
  }


  // counters on server authoritative -
// methods are only here if local operation is needed. 
// 
  public void onBannerClick() {
    advert.onBannerClick();
  }

  public void onBannerView() {
    advert.onBannerView();
  }

  public void onFullAdClick() {
    advert.onFullAdvertClick();
  }

  public void onFullAdView() {
    advert.onFullAdvertView();
  }

  public void onAckClick() {
    advert.onAckClick();
  }

  public void onAckView() {
    advert.onAckView();
  }

  public void onAdTextView() {
    advert.onAdTextView();
  }

  public void onAdTextClick() {
    advert.onAdTextClick();
  }

  public boolean hasUrl() {
    return !TextUtils.isEmpty(advert.url);
  }
}


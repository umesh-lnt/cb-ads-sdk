package com.cloudbanter.adssdk.ad.model;

import java.util.ArrayList;

/**
 * Created by eric on 3/11/16.
 */
public class CbTrackingModel {
  public String acknowledgement;
  public ArrayList<String> keywords;
  public String advertNote;
  public String keywordsValuation;
  public Float budget;
  public String imageURI;
  public String ackCopyId;
  public String history;
  public String dummyId;

  public String bannerURI;
  public String fullAdvertURI;
  public String fullAdvertTitle;
  public String fullAdvertText;
  public String ackURI;


  public int advertTextViewCount;
  public int advertTextClickCount;
  public int bannerViewCount;
  public int bannerClickCount;  // server side is authoritative -
  public int fullAdvertViewCount;
  public int fullAdvertClickCount;
  int ackClickCount;


}

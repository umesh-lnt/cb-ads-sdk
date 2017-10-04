package com.cloudbanter.adssdk.ad.service.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by eric on 2/25/16.
 */
public class CbGsonS {
  public String _id;

  protected static GsonBuilder gsonBuilder = new GsonBuilder();
  protected static Gson gson;

  static {
    gson = gsonBuilder.create();
  }

  protected CbGsonS() {
  }

}

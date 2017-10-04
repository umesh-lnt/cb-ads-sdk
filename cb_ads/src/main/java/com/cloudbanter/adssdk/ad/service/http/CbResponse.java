package com.cloudbanter.adssdk.ad.service.http;

/**
 * Created by eric on 6/24/15.
 */
public class CbResponse extends CbGsonS {
  int code;
  String message;
  public String body;

  public CbResponse(int code, String message, String body) {
    this.code = code;
    this.message = message;
    this.body = body;
  }

  public String setMessage(String s) {
    return message = s;
  }


  // serialization...
  public String toJson() {
    return gson.toJson(this, CbRequest.class);
  }

  public static CbResponse fromJson(String json) {
    CbResponse res = (CbResponse) gson.fromJson(json, CbResponse.class);
    return res;
  }

}



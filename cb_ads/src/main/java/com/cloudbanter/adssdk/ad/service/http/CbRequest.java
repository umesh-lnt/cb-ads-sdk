package com.cloudbanter.adssdk.ad.service.http;

/**
 * Created by eric on 6/24/15.
 */
public class CbRequest extends CbGsonS {
  String url;
  HttpMethod method;
  String body;
  String auth;

  public CbRequest(String url, HttpMethod method, String body) {
    this(url, method, null, body);
  }

  public CbRequest(String url, HttpMethod method, String auth, String body) {
    this.url = url;
    this.method = method;
    this.auth = auth;
    this.body = body;
  }

  // serialization...
  public String toJson() {
    return gson.toJson(this, CbRequest.class);
  }

  public static CbRequest fromJson(String json) {
    CbRequest req = (CbRequest) gson.fromJson(json, CbRequest.class);
    return req;
  }

}

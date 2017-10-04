package com.cloudbanter.adssdk.ad.service.http;

public class ServerAddress {
  public String ip;
  public String port;

  public ServerAddress(String ip, String port) {
    this.ip = ip;
    this.port = port;
  }

  public String getHttpURI() {
    return "https://" + ip + ":" + port + "/api/";
  }
}
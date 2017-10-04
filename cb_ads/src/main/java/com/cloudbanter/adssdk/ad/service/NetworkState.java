package com.cloudbanter.adssdk.ad.service;

public class NetworkState {
  public static int ERROR_NETWORK_STATE = -1;
  public static int UNKNOWN_NETWORK_STATE = 0;
  public static int CLOUDBANTER_PROTOCOL_STATE = 1;
  public static int WIFI_ENABLED_STATE = 2;
  public static int CELL_DATA_ENABLED_STATE = 3;

  public int getNetworkState() {
    return ERROR_NETWORK_STATE;
  }
  // TODO check on network state
  // TODO network state listeners
  // TODO rules for retries
}

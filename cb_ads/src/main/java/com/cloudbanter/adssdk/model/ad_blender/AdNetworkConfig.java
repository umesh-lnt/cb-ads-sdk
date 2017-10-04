package com.cloudbanter.adssdk.model.ad_blender;


import com.cloudbanter.adssdk.ad_exchange.ad_networks.AdNetwork;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Model for AdNetwork configuration
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio</a>
 * @since 8/16/17
 */
public class AdNetworkConfig {
  
  /** The AdNetwork **/
  @SerializedName("key")
  private AdNetwork adNetwork;
  
  /** Order **/
  private int order;
  
  /** AdNetwork data **/
  private Map<String, String> data;
  
  /** Default constructor **/
  public AdNetworkConfig() {
  }
  
  public AdNetworkConfig(AdNetwork adNetwork, int order, Map<String, String> data) {
    this.adNetwork = adNetwork;
    this.order = order;
    this.data = data;
  }
  
  /**
   * @return adNetwork
   */
  public AdNetwork getAdNetwork() {
    return adNetwork;
  }
  
  /**
   * @param adNetwork
   *         to be set
   */
  public AdNetworkConfig setAdNetwork(
          AdNetwork adNetwork) {
    this.adNetwork = adNetwork;
    return this;
  }
  
  /**
   * @return order
   */
  public int getOrder() {
    return order;
  }
  
  /**
   * @param order
   *         to be set
   */
  public AdNetworkConfig setOrder(int order) {
    this.order = order;
    return this;
  }
  
  /**
   * @return data
   */
  public Map<String, String> getData() {
    return data;
  }
  
  /**
   * @param data
   *         to be set
   */
  public AdNetworkConfig setData(Map<String, String> data) {
    this.data = data;
    return this;
  }
}

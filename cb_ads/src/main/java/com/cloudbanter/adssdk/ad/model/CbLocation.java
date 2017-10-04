package com.cloudbanter.adssdk.ad.model;

/**
 * @author <a href="mailto:aajn88@gmail.com">Antonio</a>
 * @since 8/10/17
 */
public class CbLocation {
  
  private double latitude;
  private double longitude;
  
  public CbLocation(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }
  
  /**
   * @return latitude
   */
  public double getLatitude() {
    return latitude;
  }
  
  /**
   * @param latitude
   *         to be set
   */
  public CbLocation setLatitude(double latitude) {
    this.latitude = latitude;
    return this;
  }
  
  /**
   * @return longitude
   */
  public double getLongitude() {
    return longitude;
  }
  
  /**
   * @param longitude
   *         to be set
   */
  public CbLocation setLongitude(double longitude) {
    this.longitude = longitude;
    return this;
  }
}

package com.cloudbanter.adssdk.ad.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Ugljesa Jovanovic (ionspinf@gmail.com) on 14-Oct-2016
 */
public class AdMix {
  public static final transient String TAG = AdMix.class.getSimpleName();

  private int console;
  @SerializedName("mopub")
  private int adExchange;

  public AdMix() {
  }

  public AdMix(int console, int adExchange) {
    this.console = console;
    this.adExchange = adExchange;
  }

  /**
   * @return the console
   */
  public int getConsole() {
    return console;
  }

  /**
   * @param console
   *         the console to set
   */
  public AdMix setConsole(int console) {
    this.console = console;
    return this;
  }

  /**
   * @return the adExchange
   */
  public int getAdExchange() {
    return adExchange;
  }

  /**
   * @param adExchange
   *         the adExchange to set
   */
  public AdMix setAdExchange(int adExchange) {
    this.adExchange = adExchange;
    return this;
  }

  @Override
  public String toString() {
    return "AdMix{" +
            "console=" + console +
            ", adExchange=" + adExchange +
            '}';
  }
}

package com.cloudbanter.adssdk.ad.manager.adstates;

public abstract class AdState {

  public abstract void onClick();

  public abstract AdState getNextAdState();

  public abstract String getBannerImageUri();

  public abstract String getFullImageUri();

  public abstract String getTitleText();

  public abstract String getAdText();

}

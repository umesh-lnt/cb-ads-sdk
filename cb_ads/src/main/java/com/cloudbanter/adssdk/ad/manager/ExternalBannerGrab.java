package com.cloudbanter.adssdk.ad.manager;

import android.util.Log;

import java.io.File;

/**
 * @author Ugljesa Jovanovic (ionspinf@gmail.com) on 07-Sep-2016
 */
public class ExternalBannerGrab {
  public static final String TAG = ExternalBannerGrab.class.getSimpleName();

  private File bannerImageFile;
  private String bannerUrl;

  public ExternalBannerGrab(File bannerImageFile, String bannerUrl) {
    Log.d(TAG, "Creating external banner grab: " + bannerImageFile.getAbsolutePath() + " url: " +
            bannerUrl);
    this.bannerImageFile = bannerImageFile;
    this.bannerUrl = bannerUrl;
  }

  public File getBannerImageFile() {
    return bannerImageFile;
  }

  public String getBannerUrl() {
    return bannerUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ExternalBannerGrab) {
      return bannerUrl.equals(((ExternalBannerGrab) o).bannerUrl);
    } else {
      return false;
    }
  }
}

package com.cloudbanter.adssdk.ad.model;

import android.text.TextUtils;
import android.util.Log;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.R;
import com.cloudbanter.adssdk.ad.manager.images.ImageRef;

/**
 * Created by eric on 7/9/15.
 */
public class CbAdvert extends AModel<CbAdvert> {

  // from server ? model
  public String id;
  // from server
  public String type;
  public String advertText;
  public String name;
  public String bannerImage;
  public String fullImage;
  public String fullExt; // graphical image type { gif, png }
  public String bannerExt;
  public String status;
  public String url;
  public String state;
  public String createdAt;
  public String updatedAt;
  public String advertiser;
  // filename
  public String fullImageFileName;
  public String bannerImageFileName;


  public void onAdTextClick() {
  }

  public void onAdTextView() {
  }

  public void onBannerClick() {
  }

  public void onBannerView() {
  }

  public void onFullAdvertClick() {
  }

  public void onFullAdvertView() {
  }

  public void onAckClick() {
  }

  public void onAckView() {
  }


  // serialization...
  public String toJson() {
    return gson.toJson(this, CbAdvert.class);
  }

  public static CbAdvert fromJson(String json) {
    CbAdvert ad = (CbAdvert) gson.fromJson(json, CbAdvert.class);
    ad.setImageIds();
    return ad;
  }

  // add filenames for download...
  public void setImageIds() {
//    Log.d("CbAdvert.setImageIds", this.toJson());
//    Log.d("CbAdvert.setImageIds", " full: " + fullImageFileName);
//    Log.d("CbAdvert.setImageIds", " bann: " + bannerImageFileName);

    if (TextUtils.isEmpty(advertiser)) {
      Log.d("CbAdvert", "MISSING ADVERTISER");
    }
    fullImageFileName = ImageRef.getImageFileName(advertiser, _id,
            CbAdsSdk.getApplication().getString(R.string.full_image_name), fullExt);
    bannerImageFileName = ImageRef.getImageFileName(advertiser, _id,
            CbAdsSdk.getApplication().getResources().getString(R.string.banner_image_name),
            bannerExt);
  }

  // add urls for download...
  public void setImageRefs() {
//    Log.d("CbAdvert.setImageRefs", this.toJson());

    fullImage = (null == fullImage) ? ImageRef.getImageUri(advertiser, _id,
            CbAdsSdk.getApplication().getResources().getString(R.string.full_image_name), fullExt) :
            fullImage;
    bannerImage = (null == bannerImage) ? ImageRef.getImageUri(advertiser, _id,
            CbAdsSdk.getApplication().getResources().getString(R.string.banner_image_name),
            bannerExt) : bannerImage;
  }
}

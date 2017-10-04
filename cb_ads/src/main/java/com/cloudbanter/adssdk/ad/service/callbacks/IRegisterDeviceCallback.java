package com.cloudbanter.adssdk.ad.service.callbacks;


import com.cloudbanter.adssdk.ad.model.CbDevice;

/**
 * Created by eric on 6/18/15.
 */
public interface IRegisterDeviceCallback extends ICallback {
  void onRegisterDeviceComplete(CbDevice device);
}


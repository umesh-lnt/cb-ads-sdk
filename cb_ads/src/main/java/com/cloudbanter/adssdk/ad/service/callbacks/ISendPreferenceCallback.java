package com.cloudbanter.adssdk.ad.service.callbacks;

import com.cloudbanter.adssdk.ad.model.CbPreferenceData;

// ec 1/1/15
public interface ISendPreferenceCallback {
  void onSendPreferenceComplete(CbPreferenceData obj);
}
package com.cloudbanter.adssdk.ad.service.callbacks;


import com.cloudbanter.adssdk.ad.model.CbEventResponse;

/**
 * Created by eric on 6/17/15.
 */
public interface ISendEventCallback extends ICallback {
  void onSendEventComplete(CbEventResponse event);
}

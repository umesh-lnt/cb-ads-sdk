package com.cloudbanter.adssdk.ad.service.callbacks;


import com.cloudbanter.adssdk.ad.model.CbUserInfo;

/**
 * Created by eric on 3/19/16.
 */
public interface ISendUserInfoCallback {
  void onSendUserInfoComplete(CbUserInfo obj);
}
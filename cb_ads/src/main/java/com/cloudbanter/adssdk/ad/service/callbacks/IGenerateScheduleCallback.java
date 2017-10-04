package com.cloudbanter.adssdk.ad.service.callbacks;

import com.cloudbanter.adssdk.ad.model.CbSchedule;

/**
 * Created by eric on 3/19/16.
 */
public interface IGenerateScheduleCallback extends ICallback {
  void onGenerateScheduleComplete(CbSchedule obj);
}
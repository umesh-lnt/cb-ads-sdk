package com.cloudbanter.adssdk.ad.service.callbacks;

import com.cloudbanter.adssdk.ad.model.CbSchedule;

/**
 * Created by eric on 3/19/16.
 */
public interface IGetDefaultScheduleCallback extends ICallback {
  void onGetDefaultScheduleComplete(CbSchedule obj);
}
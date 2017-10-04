package com.cloudbanter.adssdk.ad.model;

/**
 * Created by eric on 8/18/15.
 */
public class CbScheduleResponse extends AModel<CbScheduleResponse> {

  private CbSchedule schedule;

  public CbScheduleResponse(CbSchedule schedule) {
    this.schedule = schedule;
  }

  public static CbScheduleResponse fromJson(String json) {
    return gson.fromJson(json, CbScheduleResponse.class);
  }

  /**
   * @return the schedule
   */
  public CbSchedule getSchedule() {
    return schedule;
  }

  /**
   * @param schedule
   *         the schedule to set
   */
  public CbScheduleResponse setSchedule(CbSchedule schedule) {
    this.schedule = schedule;
    return this;
  }
}

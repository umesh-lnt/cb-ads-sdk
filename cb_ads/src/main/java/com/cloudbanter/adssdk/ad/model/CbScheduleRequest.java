package com.cloudbanter.adssdk.ad.model;

/**
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 30/1/17
 */
public class CbScheduleRequest extends AModel<CbScheduleRequest> {

  /** Request wrapper **/
  private CbScheduleRequestWrapper generateSchedule;

  /**
   * @return the generateSchedule
   */
  public CbScheduleRequestWrapper getGenerateSchedule() {
    return generateSchedule;
  }

  /**
   * @param generateSchedule
   *         the generateSchedule to set
   */
  public CbScheduleRequest setGenerateSchedule(
          CbScheduleRequestWrapper generateSchedule) {
    this.generateSchedule = generateSchedule;
    return this;
  }

  public String toJson() {
    return gson.toJson(this, CbScheduleRequest.class);
  }

}

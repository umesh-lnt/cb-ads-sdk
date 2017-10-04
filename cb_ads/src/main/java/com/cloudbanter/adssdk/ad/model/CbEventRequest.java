package com.cloudbanter.adssdk.ad.model;

/**
 * Request wrapper for Event request
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 7/2/17
 */
public class CbEventRequest extends AModel<CbEventRequest> {

  /** Raw event **/
  private CbEvent rawEvent;

  public CbEventRequest(CbEvent rawEvent) {
    this.rawEvent = rawEvent;
  }

  public String toJson() {
    return gson.toJson(this, CbEventRequest.class);
  }

  /**
   * @return the rawEvent
   */
  public CbEvent getRawEvent() {
    return rawEvent;
  }

  /**
   * @param rawEvent
   *         the rawEvent to set
   */
  public CbEventRequest setRawEvent(CbEvent rawEvent) {
    this.rawEvent = rawEvent;
    return this;
  }
}

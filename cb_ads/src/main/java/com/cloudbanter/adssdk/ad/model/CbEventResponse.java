package com.cloudbanter.adssdk.ad.model;

/**
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 7/2/17
 */
public class CbEventResponse extends AModel<CbEventResponse> {

  /** Events count **/
  private int events;

  /** Messages count **/
  private int messages;

  /**
   * @return the events
   */
  public int getEvents() {
    return events;
  }

  public static CbEventResponse fromJson(String json) {
    return gson.fromJson(json, CbEventResponse.class);
  }

  /**
   * @param events
   *         the events to set
   */
  public CbEventResponse setEvents(int events) {
    this.events = events;
    return this;
  }

  /**
   * @return the messages
   */
  public int getMessages() {
    return messages;
  }

  /**
   * @param messages
   *         the messages to set
   */
  public CbEventResponse setMessages(int messages) {
    this.messages = messages;
    return this;
  }
}

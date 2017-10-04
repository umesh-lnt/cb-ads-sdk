package com.cloudbanter.adssdk.model.ad_blender;

/**
 * Sms data
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 30/1/17
 */
public class Sms {

  private String id;
  private String address;
  private String msg;
  private boolean isRead; //"0" for have not read sms and "1" for have read sms
  private long time;
  private boolean isSent;

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id
   *         the id to set
   */
  public Sms setId(String id) {
    this.id = id;
    return this;
  }

  /**
   * @return the address
   */
  public String getAddress() {
    return address;
  }

  /**
   * @param address
   *         the address to set
   */
  public Sms setAddress(String address) {
    this.address = address;
    return this;
  }

  /**
   * @return the msg
   */
  public String getMsg() {
    return msg;
  }

  /**
   * @param msg
   *         the msg to set
   */
  public Sms setMsg(String msg) {
    this.msg = msg;
    return this;
  }

  /**
   * @return the isRead
   */
  public boolean isRead() {
    return isRead;
  }

  /**
   * @param isNotRead
   *         the isRead to set
   */
  public Sms setRead(boolean isNotRead) {
    this.isRead = isNotRead;
    return this;
  }

  /**
   * @return the time
   */
  public long getTime() {
    return time;
  }

  /**
   * @param time
   *         the time to set
   */
  public Sms setTime(long time) {
    this.time = time;
    return this;
  }

  /**
   * @return the isSent
   */
  public boolean isSent() {
    return isSent;
  }

  /**
   * @param isSent
   *         the isSent to set
   */
  public Sms setSent(boolean isSent) {
    this.isSent = isSent;
    return this;
  }
}

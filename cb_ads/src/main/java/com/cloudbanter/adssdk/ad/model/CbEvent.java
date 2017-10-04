package com.cloudbanter.adssdk.ad.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by eric on 7/9/15.
 */
public class CbEvent extends AModel<CbEvent> {

  public static final String EVENT_VIEW = "VIEW";
  public static final String EVENT_CLICK = "CLICK";
  public static final String EVENT_DELETE = "DELETE";
  public static final String REMOVE_OFFER = "REMOVE_OFFER";
  public static final String USE_OFFER = "USE_OFFER";

  public static final String SUBTYPE_AD_TEXT = "AD_TEXT";
  public static final String SUBTYPE_AD_BANNER = "AD_BANNER";
  public static final String SUBTYPE_AD_FULL = "AD_FULL";
  public static final String SUBTYPE_AD_ACK = "AD_ACK";

  public String deviceId;
  public String countryCode;
  @SerializedName("sms_received")
  public int smsReceived;
  @SerializedName("sms_sent")
  public int smsSent;
  @SerializedName("mms_received")
  public int mmsReceived;
  @SerializedName("mms_sent")
  public int mmsSent;
  public CbEventSummary[] references;

  /*
   * deviceId: { type: mongoose.Schema.Types.ObjectId, ref: 'Device' }, reference: { type:
   * mongoose.Schema.Types.ObjectId, ref: 'ScheduleEntry' }, type: {type: String, enum: ['VIEW',
   * 'CLICK', 'DELETE']}, subtype: {type: String, enum: ['AD_TEXT', 'AD_BANNER', 'AD_FULL',
   * 'AD_ACK']}, createdAt: { type: Date }, updatedAt: { type: Date }
   */

  public CbEvent(String deviceId, int smsSent, int smsReceived, int mmsSent, int mmsReceived,
                 CbEventSummary[] summaries, String countryCode) {
    this.deviceId = deviceId;
    this.references = summaries;
    this.countryCode = countryCode;
    this.smsSent = smsSent;
    this.smsReceived = smsReceived;
    this.mmsSent = mmsSent;
    this.mmsReceived = mmsReceived;

  }

  public String toJson() {
    return gson.toJson(this, CbEvent.class);
  }

  public CbEvent fromJson(String json) {
    return (CbEvent) gson.fromJson(json, CbEvent.class);
  }
}

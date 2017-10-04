package com.cloudbanter.adssdk.ad.service;

import android.util.Log;

import com.cloudbanter.adssdk.ad.manager.EventAggregator;
import com.cloudbanter.adssdk.ad.model.AdMix;
import com.cloudbanter.adssdk.ad.model.CbAtomicRegistration;
import com.cloudbanter.adssdk.ad.model.CbDevice;
import com.cloudbanter.adssdk.ad.model.CbEvent;
import com.cloudbanter.adssdk.ad.model.CbEventRequest;
import com.cloudbanter.adssdk.ad.model.CbEventResponse;
import com.cloudbanter.adssdk.ad.model.CbKeywords;
import com.cloudbanter.adssdk.ad.model.CbPreferenceData;
import com.cloudbanter.adssdk.ad.model.CbSchedule;
import com.cloudbanter.adssdk.ad.model.CbScheduleRequest;
import com.cloudbanter.adssdk.ad.model.CbScheduleRequestWrapper;
import com.cloudbanter.adssdk.ad.model.CbScheduleResponse;
import com.cloudbanter.adssdk.ad.model.CbUserInfo;
import com.cloudbanter.adssdk.ad.service.callbacks.CallbackException;
import com.cloudbanter.adssdk.ad.service.http.CbHttpService;
import com.cloudbanter.adssdk.ad.service.http.CbRequest;
import com.cloudbanter.adssdk.ad.service.http.CbResponse;
import com.cloudbanter.adssdk.ad.service.http.CbServerAddressResolver;
import com.cloudbanter.adssdk.ad.service.http.HttpMethod;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;

import java.util.List;

/**
 * Created by eric on 6/24/15.
 */
public class CbEndpoints {

  private static final String TAG = "CbEndpoints";
  private static final String STRING_COMA = ",";

  private static String serviceUrl;

  private static CbHttpService service;

  // since initialization calls out to network, wait to initialize until a call is made from the
  // service;
  static Object lock = new Object();

  public void init() throws CallbackException {
    synchronized (lock) {
      // probably should check availability on each call
      if (null == serviceUrl || "".equalsIgnoreCase(serviceUrl)) {
        serviceUrl = CbServerAddressResolver.getServerAddress();
        if (null == serviceUrl || "".equalsIgnoreCase(serviceUrl)) {
          throw new CallbackException("No server available");
        }
      }
      if (null == service) {
        service = new CbHttpService(serviceUrl);
      }
    }
  }


  public CbDevice registerDevice(CbDevice device) throws CallbackException {
    init();
    String json = device.toJson();
    CbRequest req = new CbRequest("devices/selfRegister", HttpMethod.POST, json);
    CbResponse res = service.makeRequest(req);
    try {
      return device.regSync(res.body);
    } catch (JsonSyntaxException je) {
      Log.d(TAG, String.format("RegisterDevice: \n%s\n%s", je.getMessage(), res.body));
      throw new CallbackException("Device Registration Error");
    }
  }

  public CbDevice doAtomicRegistration(CbDevice device, CbUserInfo userInfo,
                                       CbPreferenceData preferenceData) throws CallbackException {
    init();
    String json = new CbAtomicRegistration(device, userInfo, preferenceData).toJson();
    CbRequest req = new CbRequest("devices/selfRegister", HttpMethod.POST, json);
    CbResponse res = service.makeRequest(req);
    try {
      return device.regSync(res.body);
    } catch (JsonSyntaxException je) {
      Log.d(TAG, String.format("RegisterDevice: \n%s\n%s", je.getMessage(), res.body));
      throw new CallbackException("Device Registration Error");
    }
  }

  public CbEventResponse sendEvent(Authenticator auth, CbEvent event)
          throws JSONException, CallbackException {
    init();
    // CbRequest req = new CbRequest("/events", HttpMethod.POST, auth, event.toJson());
    // TODO fix events api
    // CbRequest req = new CbRequest("/events/" + auth.deviceId + "/newEvent", HttpMethod.POST,
    // auth.authToken, event.toJson());
    CbRequest req =
            new CbRequest("events/" + auth.deviceId + "/rawEvent", HttpMethod.POST, auth.authToken,
                    new CbEventRequest(event).toJson());
    CbResponse res = service.makeRequest(req);
    try {
      //We don't have an activity that handles this, because it's happening in the background
      //so well clear the maps from here
      EventAggregator.getInstance().resetCounters();
      return CbEventResponse.fromJson(res.body);
    } catch (JsonSyntaxException je) {
      Log.d(TAG, String.format("sendEvent: \n%s\n%s", je.getMessage(), res.body));
      throw new CallbackException("Send Event Error");
    }
  }

  public CbSchedule getSchedule(Authenticator auth, List<String> keywords)
          throws CallbackException {
    init();
    CbScheduleRequest request = buildRequest(keywords);
    CbRequest req =
            new CbRequest("devices/" + auth.deviceId + "/generateSchedule", HttpMethod.POST,
                    auth.authToken, request.toJson());
    CbResponse res = service.makeRequest(req);
    if (res != null && res.body != null) {
      Log.d(TAG, "Schedule response size: " + res.body.length());
    }
    Log.d(TAG, "schedule: " + res.body);
    try {
      CbScheduleResponse response = CbScheduleResponse.fromJson(res.body);
      return response == null ? null : response.getSchedule();
    } catch (JsonSyntaxException je) {
      Log.d(TAG, String.format("sendEvent: \n%s\n%s", je.getMessage(), res.body));
      throw new CallbackException("Send Event Error");
    }
  }

  private CbScheduleRequest buildRequest(List<String> keywords) {
    return new CbScheduleRequest().setGenerateSchedule(new CbScheduleRequestWrapper(keywords));
  }

  public CbKeywords getKeywords(Authenticator auth) throws CallbackException {
    init();
    CbRequest req = new CbRequest("devices/" + auth.deviceId + "/keywords", HttpMethod.GET,
            auth.authToken, null);
    CbResponse res = service.makeRequest(req);
    if (res != null && res.body != null) {
      Log.d(TAG, "Keywords response size: " + res.body.length());
      Log.d(TAG, "keywords: " + res.body);
    }
    try {
      return CbKeywords.fromJson(res.body);
    } catch (JsonSyntaxException je) {
      Log.d(TAG, String.format("getKeywords: \n%s\n%s", je.getMessage(), res.body));
      throw new CallbackException("Error getting keywords");
    }
  }

  public CbPreferenceData sendPreferences(Authenticator auth, CbPreferenceData prefdata)
          throws CallbackException {
    init();
    CbRequest req = new CbRequest("devices/" + auth.deviceId + "/updatePreferences", HttpMethod.PUT,
            auth.authToken, prefdata.toJson());
    CbResponse res = service.makeRequest(req);
    try {
      // check response ack.
      return null; // TODO sync/update...  prefdata.fromJson(res.body); // currently not used on
      // device.
    } catch (JsonSyntaxException je) {
      Log.d(TAG, String.format("sendEvent: \n%s\n%s", je.getMessage(), res.body));
      throw new CallbackException("Send Preferences Error");
    }
  }


  public CbPreferenceData sendUserInfo(Authenticator auth, CbUserInfo userData)
          throws CallbackException {
    Log.d(TAG, "Send user info called");
    init();
    CbRequest req = new CbRequest("devices/" + auth.deviceId + "/updateUserInfo", HttpMethod.PUT,
            auth.authToken, userData.toJson());
    CbResponse res = service.makeRequest(req);
    try {
      // check response ack.
      return null; // TODO sync/update...  userData.fromJson(res.body); // currently not used on
      // device.
    } catch (JsonSyntaxException je) {
      Log.d(TAG, String.format("sendEvent: \n%s\n%s", je.getMessage(), res.body));
      throw new CallbackException("Send Preferences Error");
    }
  }

  public Object getDefaultSchedule(Authenticator auth) throws CallbackException {
    init();
    CbRequest req = new CbRequest("devices/" + auth.deviceId + "/getDefaultAds", HttpMethod.GET,
            auth.authToken, null);
    CbResponse res = service.makeRequest(req);
    Log.d(TAG, "schedule: " + res.body);
    try {
      CbScheduleResponse response = CbScheduleResponse.fromJson(res.body);
      return response == null ? null : response.getSchedule();
    } catch (JsonSyntaxException je) {
      Log.d(TAG, String.format("sendEvent: \n%s\n%s", je.getMessage(), res.body));
      throw new CallbackException("Send Event Error");
    }

  }

  public AdMix getMixSettings(Authenticator auth) throws CallbackException {
    init();
    CbRequest req =
            new CbRequest("devices/" + auth.deviceId + "/mix", HttpMethod.GET, auth.authToken,
                    null);
    CbResponse res = service.makeRequest(req);
    Log.d(TAG, "Mix: " + res.body);
    try {
      return new Gson().fromJson(res.body, AdMix.class);
    } catch (JsonSyntaxException je) {
      Log.d(TAG, String.format("sendEvent: \n%s\n%s", je.getMessage(), res.body));
      throw new CallbackException("Send Event Error, json syntax exception");
    }
  }
}

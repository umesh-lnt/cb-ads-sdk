package com.cloudbanter.adssdk.ad.service.http;

import android.util.Log;

import com.cloudbanter.adssdk.ad.service.callbacks.CallbackException;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by eric on 6/24/15.
 */
public class CbHttpService {

  private static final String TAG = "CbHttpService";
  private final String serviceUrl;

  public CbHttpService(String serviceUrl) {
    this.serviceUrl = serviceUrl;
  }

  public CbResponse makeRequest(CbRequest req) throws CallbackException {
    Log.d(TAG, "Sending to: " + req.url);
    BufferedOutputStream os = null;
    InputStream is = null;
    HttpURLConnection conn = null;
    CbResponse res = null;
    try {
      Log.d(TAG, req.url);
      Log.d(TAG, req.toJson());
      // constants
      String methodUrl;
      if (null != req.auth) {
        methodUrl = String.format("%s%s?jwt=%s", serviceUrl, req.url, req.auth);
      } else {
        methodUrl = String.format("%s%s", serviceUrl, req.url);
      }
      Log.d(TAG, "Method url: " + methodUrl);
      Log.d(TAG, "Method: " + req.method.toString());
      URL url = new URL(methodUrl);

      String message = req.body;
//      Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.1.134", 8888));
      conn = (HttpURLConnection) url.openConnection();
      conn.setReadTimeout(10000 /* milliseconds */);
      conn.setConnectTimeout(12000 /* milliseconds */);
      conn.setDoInput(true);


      if (HttpMethod.POST == req.method) {
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setFixedLengthStreamingMode(null == message ? 0 : message.getBytes().length);
        conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
      } else if (HttpMethod.PUT == req.method) {
        conn.setDoOutput(true);
        conn.setRequestMethod("PUT");
        conn.setFixedLengthStreamingMode(null == message ? 0 : message.getBytes().length);
        conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
      } else if (HttpMethod.GET == req.method) {
        conn.setRequestMethod("GET");
        conn.setFixedLengthStreamingMode(0);
      } else {
        Log.d(TAG, "RTF Unsupported http method");
      }

      // make some HTTP header nicety
      conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

      // open
      conn.connect();
      Log.d(TAG, "Connect");
      Log.d(TAG, "Http host: " + System.getProperty("http.proxyHost", "NA"));
      Log.d(TAG, "Http port: " + System.getProperty("http.proxyPort", "NA"));
      Log.d(TAG, "Https host: " + System.getProperty("https.proxyHost", "NA"));
      Log.d(TAG, "Https port: " + System.getProperty("https.proxyPort", "NA"));
      Log.d(TAG, "Proxy? " + conn.usingProxy());

      // setup send
      if (HttpMethod.POST == req.method || HttpMethod.PUT == req.method) {
        os = new BufferedOutputStream(conn.getOutputStream());
        os.write((null == message ? "" : message).getBytes());
        // clean up
        os.flush();
      }

      // handle buffering errors & size
      String errMsg = String.format("http code/mesg: %d %s", conn.getResponseCode(),
              conn.getResponseMessage());
      Log.d(TAG, errMsg);
      if (400 <= conn.getResponseCode()) { // && conn.getResponseCode() <= 499) {
        if (conn.getResponseCode() == 403) {
          throw new CallbackException("Authorization fail.");
        }
        throw new CallbackException(errMsg);
      } else if (conn.getResponseMessage().contains("Unauthorized")) {
        throw new CallbackException("Authorization fail.");
      }

      is = conn.getInputStream();
      String receivedBody = new String(readBytes(is));

      Log.d(TAG, receivedBody);

      res = new CbResponse(
              conn.getResponseCode(),
              conn.getResponseMessage(),
              receivedBody
      );

      // TODO update error precision
      if (null != res.body && res.body.toLowerCase().contains("error")) {
        throw new CallbackException("ServerError: " + res.body);
      }

    } catch (IOException e) {
      // handle IOException
      Log.e(TAG, "", e);
      throw new CallbackException("Cannot connect with server.", e);
    } finally {
      // clean up
      try {
        if (null != os) {
          os.close();
        }
      } catch (IOException e) {
      }
      try {
        if (null != is) {
          is.close();
        }
      } catch (IOException e) {
      }
      if (null != conn) {
        conn.disconnect();
      }
    }
    return res;
  }

  public String readBytes(InputStream is) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int bytesRead = 0;
    byte[] input = new byte[32768];
    while (-1 != (bytesRead = is.read(input))) {
      buffer.write(input, 0, bytesRead);
    }
    input = buffer.toByteArray();
    return new String(input);
  }
}

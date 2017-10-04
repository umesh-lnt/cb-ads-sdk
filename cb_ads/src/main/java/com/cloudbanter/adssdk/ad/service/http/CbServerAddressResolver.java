package com.cloudbanter.adssdk.ad.service.http;

import android.util.Log;

import com.cloudbanter.adssdk.CbAdsSdk;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by eric on 5/5/15. endpoint resolver for server call from off main thread.
 */
public class CbServerAddressResolver {
  // server address
  // private static final String SERVER_ADDR = "http://192.168.1.38:8124"; // original node based
  // test server
  // private static final String SERVER_ADDR = "http://192.168.1.172:3000"; // cb office bbb
  // private static final String SERVER_ADDR = "http://104.197.46.255:3000"; // google apps
  // private static final String SERVER_ADDR = "http://192.168.1.38:3000"; // mac development
  // private static final String SERVER_ADDR = "http://192.168.43.222:3000"; // mac development
  
  private static final String TAG = CbServerAddressResolver.class.getSimpleName();
  
  private static final int CONNECTABLE_TIMEOUT = 5000; //  500 ms timeout...
  
  // local test server addresses...
  ServerAddress[] addresses = {
          new ServerAddress("192.168.1.38", "9000"),
          new ServerAddress("192.168.1.40", "9000"),
          new ServerAddress("192.168.42.207", "9000")
  };
  //  private static ServerAddress saLocal = new ServerAddress("192.168.1.38", "9000"); // wifi
  // devel from vbox
  private static ServerAddress saLocal = new ServerAddress("192.168.1.44", "9000");
  // wifi devel from vbox
  //  private static ServerAddress saLocal = new ServerAddress("192.168.1.40", "9000"); // devel
  // from vbox
  private static ServerAddress saDevice = new ServerAddress("192.168.42.207", "9000");
  // tethered device
  private static ServerAddress saEmulator = new ServerAddress("192.168.58.101", "9000");
  // tethered emulator
  private static ServerAddress saRemote1 = new ServerAddress("54.229.169.78", "8081");
  // old server EC2 small
  private static ServerAddress test = new ServerAddress("52.31.112.11", "8081"); // EC2 test
  private static ServerAddress development = new ServerAddress("52.48.58.216", "8081");
  // EC2 development
  
  // private static ServerData saLocal = new ServerData("127.0.0.1", "9000"); // devel
  // private static ServerData saLocal = new ServerData("192.168.1.38", "3000"); // devel
  // private static ServerData saGoogle = new ServerData("104.197.46.255", "3000"); // demo
  
  private static ServerAddress curServer = null;
  private static boolean DEBUG = false; // MmsConfig.DEBUG;
  
  public static String getServerAddress() {
    
    curServer = CbAdsSdk.CLOUD_SERVER_ADDRESS;
    if (null == curServer) {
      Log.d(TAG, "no server address");
      return "";
    } else {
      Log.d(TAG, curServer.getHttpURI());
      return curServer.getHttpURI();
    }
  }
  
  public static String getLocationServerAddress() {
    return "http://freegeoip.net/";
  }
  
  private static ServerAddress getServerData() {
    return curServer;
  }
  
  
  // socket based - ping
  public static boolean isConnectable() {
    return isConnectable(curServer);
  }
  
  public static boolean isConnectable(ServerAddress sa) {
    
    Socket socket = null;
    boolean reachable = false;
    try {
      // timeout? = CONNECTABLE_TIMEOUT
      socket = new Socket(sa.ip, Integer.parseInt(sa.port));
      reachable = true;
    } catch (UnknownHostException e) {
      ;
    } catch (IOException e) {
      ;
    } finally {
      if (socket != null) {
        try {
          socket.close();
        } catch (IOException e) {
        }
      }
    }
    return reachable;
  }
  
  // HttpUrlConnection - server responds.
  public static boolean isServerAvailable() {
    return isServerAvailable(curServer, CONNECTABLE_TIMEOUT);
  }
  
  public static boolean isServerAvailable(ServerAddress sa, int timeout) {
    HttpURLConnection connection = null;
    URL url = null;
    int responseCode = 0;
    boolean connectable = false;
    try {
      if (null == url) {
        url = new URL(sa.getHttpURI());
      }
      connection = (HttpURLConnection) url.openConnection();
      connection.setConnectTimeout(timeout);
      connection.setRequestMethod("HEAD");
      responseCode = connection.getResponseCode();
      connection.disconnect();  // release socket back to pool - doesn't necessarily close...
    } catch (SocketTimeoutException toE) {
      Log.d(TAG, "connection test - timeout exception", toE);
    } catch (IOException e) {
      Log.d(TAG, "connection test - io exception", e);
    }
    if (responseCode == 200) {
      connectable = true;
    }
    return connectable;
  }
}
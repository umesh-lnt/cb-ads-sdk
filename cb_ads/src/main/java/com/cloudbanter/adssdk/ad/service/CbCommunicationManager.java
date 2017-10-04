package com.cloudbanter.adssdk.ad.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

//import com.cloudbanter.mms.MmsConfig;
//import com.cloudbanter.mms.ad.service.cbprotocol.CbDirectProtocolController;
//import com.cloudbanter.mms.ad.service.cbprotocol.CbSmsResolver;
//import com.cloudbanter.mms.ad.service.cbprotocol.SmsEndpointAddress;

/**
 * Singleton manager class used for retrieving endpoint interface. Manager must be initialized
 * before
 * the instance is retrieved, otherwise a runtime exception is thrown. Except for providing
 * endpoints interface, manager is responsible for providing methods that will be called from
 * appropriate
 * parts of the SMS flow to offer a message for protocol data injection as well as offering a
 * message
 * for protocol data stripping
 */
public class CbCommunicationManager {

  public static final String TAG = CbCommunicationManager.class.getSimpleName();

  private static CbCommunicationManager sInstance = null;

  private Context mContext;
  private ConnectivityManager mConnectivityManager;
  //    private CbProxyEndpoints mCbProxyEndpoints;
//    private CbSmsResolver mCbSmsResolver;
//    private CbDirectProtocolController mCbDirectProtocolController;
  private BroadcastReceiver mConnectivityReceiver = new ConnectivityReceiver();

//    private boolean mIsDirectProtocolEnabled = MmsConfig.isCloudbanterDirectAllowed();
//    private boolean mIsCloudbanterProtocolAllowed = MmsConfig.isCloudbanterProtocolAllowed();


  private boolean mIsNetworkAvailable = false;
  private int mNetworkType;

  private List<NetworkListener> mNetworkListeners;

  public static synchronized CbCommunicationManager getInstance() {
    if (sInstance == null) {
      throw new RuntimeException("Cb Communication manager not initialized!");
    }
    return sInstance;
  }

  /**
   * Initialize the manager
   *
   * @param context
   *         application context
   */
  public static synchronized void init(Context context) {
    Log.d(TAG, "Initializing communication manager");
    if (sInstance == null) {
      sInstance = new CbCommunicationManager(context);
//            sInstance.mCbProxyEndpoints = new CbProxyEndpoints(context);
//            sInstance.mCbSmsResolver = CbSmsResolver.getInstance(context);
//            sInstance.mCbDirectProtocolController = CbDirectProtocolController.getInstance
// (context);
//            sInstance.setDirectProtocolEnabled(MmsConfig.isCloudbanterDirectAllowed());
//
//            sInstance.mCbSmsResolver.addSmsEndpoint(
//                    new SmsEndpointAddress("Initial", MmsConfig
// .getInitialCloudbanterProtocolNumber())
//            );

    }
  }

  private CbCommunicationManager(Context context) {
    mContext = context;
    mConnectivityManager =
            (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

    IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    mContext.registerReceiver(mConnectivityReceiver, intentFilter);
    mNetworkListeners = new LinkedList<>();
  }

  ;

  /**
   * Return an CbEnpoint instance that is used for all outgoing communication.
   * Underlying implementation will decide when to use SMS protocol and when to use
   * HTTP protocol
   * @return endpoint instance
   */
//    public CbEndpoints getEndpoints(){
//        return mCbProxyEndpoints;
//    }

  /**
   * Offer a message to be injected with cloudbanter protocol data
   * @param message message text
   * @return message with protocol data injected if there was any data to inject
   */
//    public String offerIncomingSmsToProtocol(String message){
//        if (mIsCloudbanterProtocolAllowed) {
//            return mCbProxyEndpoints.offerIncomingMessage(message);
//        } else {
//            return message;
//        }
//    }

//    public void requestFetchImageOverSms(CbSmsEndpoint.FetchImageRequest request){
//        mCbProxyEndpoints.getCbSmsEndpoint().requestFetchImageOverSms(request);
//    }

  /**
   * Offer a message with Cloudbanter SMS protocol data, if there is any, to strip and
   * return a user readable message
   * @param message message with protocol data
   * @return user readable message
   */
//    public String offerOutgoingSmsToProtocol(String message){
//        if (mIsCloudbanterProtocolAllowed) {
//            Log.d(TAG, "CLoudbanter protocol enabled");
////            return mCbProxyEndpoints.offerOutgoingMessage(message);
//        } else {
//            Log.d(TAG, "Cloudbanter protocol disabled");
//            return message;
//        }
//    }

  /**
   * Checks if the data network is available (Wifi, mobile or other)
   *
   * @return true if data network is available
   */
  public boolean isNetworkAvailable() {
    refreshNetworkState();
    return mIsNetworkAvailable;
  }

  /**
   * Provides current network type
   *
   * @return network type
   */
  public int getNetworkType() {
    refreshNetworkState();
    return mNetworkType;
  }

  private void refreshNetworkState() {
    NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
    if (activeNetwork != null) {
      Log.d(TAG, "Refreshing network state, active network " +
              activeNetwork.getDetailedState().name());
      Log.d(TAG, "Is connected or connecting? " + activeNetwork.isConnectedOrConnecting());
    } else {
      Log.d(TAG, "Active network was null when trying to refresh state");
    }
    mIsNetworkAvailable = activeNetwork != null &&
            activeNetwork.isConnectedOrConnecting();
    if (activeNetwork != null) {
      mNetworkType = activeNetwork.getType();
    } else {
      mNetworkType = -1;
    }
  }

  /**
   * Logging helper method
   *
   * @param type
   *         network type
   *
   * @return string describing network type
   */
  public static String networkTypeToName(int type) {
    switch (type) {
      case ConnectivityManager.TYPE_BLUETOOTH:
        return "Bluetooth";
      case ConnectivityManager.TYPE_ETHERNET:
        return "Ethernet";
      case ConnectivityManager.TYPE_MOBILE:
        return "Mobile";
      case ConnectivityManager.TYPE_MOBILE_DUN:
        return "Mobile DUN"; //Dial up network?
      case ConnectivityManager.TYPE_VPN:
        return "VPN";
      case ConnectivityManager.TYPE_WIFI:
        return "Wifi";
      case ConnectivityManager.TYPE_DUMMY:
        return "Dummy";
      case ConnectivityManager.TYPE_WIMAX:
        return "Wimax";
      default:
        return "Unknown type!";
    }
  }


  private class ConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
        Log.d(TAG, "Connectivity action!");
        Log.d(TAG, "Was available? " + mIsNetworkAvailable);
        Log.d(TAG, "Previous type: " + mNetworkType + " " + networkTypeToName(mNetworkType));
        refreshNetworkState();
        Log.d(TAG, "Is available now? " + mIsNetworkAvailable);
        Log.d(TAG, "Current type: " + mNetworkType + " " + networkTypeToName(mNetworkType));
        for (NetworkListener listener : mNetworkListeners) {
          listener.onNetworkStatusChanged(mIsNetworkAvailable, mNetworkType);
        }
      } else {
        Log.e(TAG, "Wrong action received! Action: " + intent.getAction());
      }
    }
  }

  /**
   * Listener that provides network status changes
   */
  public interface NetworkListener {
    public void onNetworkStatusChanged(boolean isNetworkAvailable, int networkType);
  }

  /**
   * Adds a listener for network status changes
   *
   * @param listener
   *         NetworkListener concrete implementation
   */
  public void registerNetworkListener(NetworkListener listener) {
    mNetworkListeners.add(listener);
  }

  /**
   * Remove a listener from manager
   *
   * @param listener
   *         listener to be removed
   */
  public void unregisterNetworkListener(NetworkListener listener) {
    mNetworkListeners.remove(listener);
  }


//    public boolean isDirectProtocolEnabled() {
//        return mIsDirectProtocolEnabled;
//    }
//
//    public void setDirectProtocolEnabled(boolean directProtocolEnabled) {
//        if (directProtocolEnabled){
//            mCbDirectProtocolController.startDirectController();
//        } else {
//            mCbDirectProtocolController.stopDirectController();
//        }
//        mIsDirectProtocolEnabled = directProtocolEnabled;
//    }

  /**
   * Used by direct protocol controller to trigger a direct message sending
   */
  public void triggerSendDirect() {
//        if (mIsDirectProtocolEnabled) {
//            mCbProxyEndpoints.sendDirectly();
//        } else {
//            Log.e(TAG, "Direct protocol disabled, direct controller should not be running!");
//        }
  }

  /**
   * Used only for testing
   *
   * @param sms
   * @param networkType
   */
  public void forceNetwork(boolean sms, int networkType) {
    mContext.unregisterReceiver(mConnectivityReceiver);
    mNetworkType = networkType;
    for (NetworkListener listener : mNetworkListeners) {
      listener.onNetworkStatusChanged(!sms, networkType);
    }
  }

  //TODO temporary
  public void debugSendDirect() {
//        mCbProxyEndpoints.addPayload("A" + new Random().nextInt(100), "TestDebug");
    final StringBuffer stringBuffer = new StringBuffer();
    for (int i = 0; i < 160; i++) {
      stringBuffer.append("A");
    }
    Log.e(TAG, "Test is not functional any more, keeping if needed in future");
//        Thread testThread = new Thread(){
//            @Override
//            public void run() {
//                mCbProxyEndpoints.testRequest(stringBuffer.toString());
//            }
//        };
//        testThread.start();


  }
}
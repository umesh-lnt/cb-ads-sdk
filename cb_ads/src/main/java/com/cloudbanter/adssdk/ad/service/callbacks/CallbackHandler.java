package com.cloudbanter.adssdk.ad.service.callbacks;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cloudbanter.adssdk.ad.model.CbAtomicRegistrationResult;
import com.cloudbanter.adssdk.ad.model.CbDevice;
import com.cloudbanter.adssdk.ad.model.CbEventResponse;
import com.cloudbanter.adssdk.ad.model.CbKeywords;
import com.cloudbanter.adssdk.ad.model.CbPreferenceData;
import com.cloudbanter.adssdk.ad.model.CbSchedule;
import com.cloudbanter.adssdk.ad.model.CbUserInfo;
import com.cloudbanter.adssdk.ad.service.CbRestService;

import java.lang.ref.WeakReference;


public class CallbackHandler<DataT> extends Handler {
  public static final int REGISTER_DEVICE_RESULT = 1;
  public static final int SEND_EVENT_RESULT = 2;
  public static final int SEND_PREFERENCE_RESULT = 3;
  public static final int GENERATE_SCHEDULE_RESULT = 4;
  public static final int SEND_USERDATA_RESULT = 5;
  public static final int GET_DEFAULT_SCHEDULE_RESULT = 6;
  public static final int ATOMIC_REGISTRATION_RESULT = 7;
  public static final int GET_KEYWORDS_RESULT = 8;

  private static final String TAG = CallbackHandler.class.getSimpleName();

  WeakReference<ICallback<DataT>> mCallback;

  public CallbackHandler(ICallback<DataT> callback) {
    mCallback = new WeakReference<ICallback<DataT>>(callback);
  }

  @Override
  public void handleMessage(Message msg) {
    ICallback<DataT> activity;
    if (null == mCallback || null == (activity = (ICallback) mCallback.get())) {
      Log.d(TAG, "Activity/Callback reference gone!");
      return; // activity is gone - nowhere to go...
    }
    if (CbRestService.CB_ERROR == msg.arg1) {
      activity.handleError((String) msg.obj);
    }
    if (CbRestService.CB_SUCCESS == msg.arg1) {
      try {
        switch (msg.what) {
          case REGISTER_DEVICE_RESULT: // IRegisterDevice
            ((IRegisterDeviceCallback) activity).onRegisterDeviceComplete((CbDevice) msg.obj);
            break;

          case ATOMIC_REGISTRATION_RESULT:
            CbAtomicRegistrationResult result = (CbAtomicRegistrationResult) msg.obj;
            ((IAtomicRegistrationCallback) activity).onAtomicRegistrationSuccess(result.getDevice(),
                    result.getUserInfo(), result.getPreferences());
            break;

          case SEND_EVENT_RESULT: // ISendEvent
            ((ISendEventCallback) activity).onSendEventComplete((CbEventResponse) msg.obj);
            break;

          case SEND_PREFERENCE_RESULT:
            ((ISendPreferenceCallback) activity).onSendPreferenceComplete(
                    (CbPreferenceData) msg.obj);
            break;

          case GENERATE_SCHEDULE_RESULT:
            ((IGenerateScheduleCallback) activity).onGenerateScheduleComplete((CbSchedule) msg.obj);
            break;

          case SEND_USERDATA_RESULT:
            ((ISendUserInfoCallback) activity).onSendUserInfoComplete((CbUserInfo) msg.obj);

          case GET_DEFAULT_SCHEDULE_RESULT:
            ((IGetDefaultScheduleCallback) activity).onGetDefaultScheduleComplete(
                    (CbSchedule) msg.obj);
            Log.d(TAG, "received default schedule");
            break;

          case GET_KEYWORDS_RESULT:
            ((IGetKeywordsCallback) activity).onKeywordsListSuccess((CbKeywords) msg.obj);
            break;
          default:
            activity.onSuccess((DataT) msg.obj);
        }
      } catch (ClassCastException cce) {
        // result going to wrong receiver. 
        Log.e(TAG, "Error processing the result", cce);
        cce.printStackTrace();
      }
    }
  }
}
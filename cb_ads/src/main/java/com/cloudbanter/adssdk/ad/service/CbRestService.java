package com.cloudbanter.adssdk.ad.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.ad.manager.AdvertManager;
import com.cloudbanter.adssdk.ad.manager.DefaultSchedule;
import com.cloudbanter.adssdk.ad.manager.ScheduleController;
import com.cloudbanter.adssdk.ad.model.CbDevice;
import com.cloudbanter.adssdk.ad.model.CbEvent;
import com.cloudbanter.adssdk.ad.model.CbEventResponse;
import com.cloudbanter.adssdk.ad.model.CbKeywords;
import com.cloudbanter.adssdk.ad.model.CbPreferenceData;
import com.cloudbanter.adssdk.ad.model.CbSchedule;
import com.cloudbanter.adssdk.ad.model.CbUserInfo;
import com.cloudbanter.adssdk.ad.repo.CbDatabase;
import com.cloudbanter.adssdk.ad.repo.DatabaseFactory;
import com.cloudbanter.adssdk.ad.service.callbacks.CallbackHandler;
import com.cloudbanter.adssdk.ad.util.CbSharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in a endpoint on a
 * separate handler thread.
 * <p/>
 */
public class CbRestService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS

    private static final String ACTION_REGISTER_DEVICE =
            "com.cloudbanter.mms.ad.service.action.REGISTER_DEVICE";
    private static final String ACTION_ATOMIC_REGISTRATION =
            "com.cloudbanter.mms.ad.service.action.ATOMIC_REGISTRATION";
    private static final String ACTION_SEND_EVENT =
            "com.cloudbanter.mms.ad.service.action.SEND_EVENT";
    private static final String ACTION_GET_SCHEDULE =
            "com.cloudbanter.mms.ad.service.action.GET_SCHEDULE";
    private static final String ACTION_UPDATE_PREFERENCES =
            "com.cloudbanter.mms.ad.service.action.SEND_PREFERENCES";
    private static final String ACTION_SEND_USERDATA =
            "com.cloudbanter.mms.ad.service.action.SEND_USERDATA";
    private static final String ACTION_GET_DEFAULT_SCHEDULE =
            "com.cloudbanter.mms.ad.service.action.GET_DEFAULT_SCHEDULE";
    private static final String ACTION_GET_KEYWORDS =
            "com.cloudbanter.mms.ad.service.action.GET_KEYWORDS";

    private static final String EXTRA_CALLBACK = "com.cloudbanter.mms.ad.service.extra.CALLBACK";
    private static final String EXTRA_EVENT = "com.cloudbanter.mms.ad.service.extra.EVENT";
    private static final String EXTRA_DEVICE = "com.cloudbanter.mms.ad.service.extra.DEVICE";
    private static final String EXTRA_PREFERENCE_DATA =
            "com.cloudbanter.mms.ad.service.extra.PREFERENCE";
    private static final String EXTRA_USER_INFO_DATA =
            "com.cloudbanter.mms.ad.service.extra.USER_INFO";
    private static final String EXTRA_KEYWORDS = "com.cloudbanter.mms.ad.service.extra.KEYWORDS";

    // prototypes...
    private static final String ACTION_BAZ = "com.cloudbanter.mms.ad.service.action.BAZ";
    private static final String EXTRA_PARAM1 = "com.cloudbanter.mms.ad.service.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.cloudbanter.mms.ad.service.extra.PARAM2";

    private static final String TAG = "CbRestService";

    public CbRestService() {
        super("CBRestService");
    }

    public CbEndpoints endpoint;
    private Authenticator auth;
    private Context mContext = this;

    /**
     * service for handling cloudbanter api requests connects service request with the appropriate
     * endpoint authentication handled here
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            // wait to initialize endpoints and athenticator until we have a valid call...
            if (null == endpoint) {
                endpoint = new CbEndpoints();
            }
            if (null == auth) {
                auth = Authenticator.getInstance(this);
            }

            final String action = intent.getAction();
            final Messenger callback = ( // ternary op to allow assignment to final
                    !intent.hasExtra(EXTRA_CALLBACK) ? null :  // no callback required
                            (Messenger) intent.getParcelableExtra(EXTRA_CALLBACK));
            if (ACTION_REGISTER_DEVICE.equals(action)) {
                final CbDevice device = (CbDevice) intent.getSerializableExtra(EXTRA_DEVICE);
                handleRegisterDevice(callback, device);
            } else if (CbAdsSdk.sIsInDemoMode) {
                handleDemoMode();
            } /*else if (ACTION_ATOMIC_REGISTRATION.equals(action)) {
                CbDevice device = (CbDevice) intent.getSerializableExtra(EXTRA_DEVICE);
                CbUserInfo userInfo = (CbUserInfo) intent.getSerializableExtra(EXTRA_USER_INFO_DATA);
                CbPreferenceData preferenceData =
                        (CbPreferenceData) intent.getSerializableExtra(EXTRA_PREFERENCE_DATA);
                handleAtomicRegistration(callback, device, userInfo, preferenceData);
            }*/

            // for apis requiring authentication...
            // if the device is in demo mode, and registration fails, it won't be authenticated with JWT
            else if (auth.isAuthenticated()) {
                if (ACTION_SEND_EVENT.equals(action)) {
                    final CbEvent event = (CbEvent) intent.getSerializableExtra(EXTRA_EVENT);
                    handleSendEvent(callback, auth, event);
                } else if (ACTION_GET_SCHEDULE.equals(action)) {
                    List<String> keywords = intent.getStringArrayListExtra(EXTRA_KEYWORDS);
                    handleGetSchedule(callback, auth, keywords);
                } else if (ACTION_UPDATE_PREFERENCES.equals(action)) {
                    final CbPreferenceData prefdata =
                            (CbPreferenceData) intent.getSerializableExtra(EXTRA_PREFERENCE_DATA);
                    handleSendPreference(callback, auth, prefdata);
                } else if (ACTION_SEND_USERDATA.equals(action)) {
                    final CbUserInfo userData =
                            (CbUserInfo) intent.getSerializableExtra(EXTRA_USER_INFO_DATA);
                    handleSendUserData(callback, auth, userData);
                } else if (ACTION_GET_DEFAULT_SCHEDULE.equals(action)) {
                    handleGetDefaultSchedule(callback, auth);
                } else if (ACTION_GET_KEYWORDS.equals(action)) {
                    handleGetKeywords(callback, auth);
                }

            } else {
                handleNotAuthenticated();
            }
            //TODO reorganize this
//      handleNoAction(action);
        }
    }

    /**
     * From Activity, starts the intent endpoint to perform action SendEvent Paired with
     * handleSendEvent in intent endpoint.
     *
     * @see IntentService
     */
    public static Intent getSendEventIntent(Context context, CallbackHandler<CbEvent> handler,
                                            CbEvent event) {
        Intent intent = new Intent(context, CbRestService.class);
        intent.setAction(ACTION_SEND_EVENT);
        if (null != handler) {
            intent.putExtra(EXTRA_CALLBACK, new Messenger(handler));
        }
        intent.putExtra(EXTRA_EVENT, event);
        return intent;
    }

    public static Intent getRegisterDeviceIntent(Context context, CallbackHandler<CbDevice> handler,
                                                 CbDevice device) {
        Intent intent = new Intent(context, CbRestService.class);
        intent.setAction(ACTION_REGISTER_DEVICE);
        if (null != handler) {
            intent.putExtra(EXTRA_CALLBACK, new Messenger(handler));
        }
        intent.putExtra(EXTRA_DEVICE, device);
        return intent;
    }

    public static Intent getKeywords(Context context, CallbackHandler<List<String>> handler) {
        Intent intent = new Intent(context, CbRestService.class);
        intent.setAction(ACTION_GET_KEYWORDS);
        if (null != handler) {
            intent.putExtra(EXTRA_CALLBACK, new Messenger(handler));
        }
        return intent;
    }

    public static Intent getAtomicRegistrationIntent(Context context,
                                                     CallbackHandler<?> handler, CbDevice device,
                                                     CbUserInfo userInfo,
                                                     CbPreferenceData preferenceData) {
        Intent intent = new Intent(context, CbRestService.class);
        intent.setAction(ACTION_ATOMIC_REGISTRATION);
        if (null != handler) {
            intent.putExtra(EXTRA_CALLBACK, new Messenger(handler));
        }
        intent.putExtra(EXTRA_DEVICE, device);
        intent.putExtra(EXTRA_USER_INFO_DATA, userInfo);
        intent.putExtra(EXTRA_PREFERENCE_DATA, preferenceData);
        return intent;
    }

    private void handleGetKeywords(Messenger callback, Authenticator auth) {
        try {
            CbKeywords result = endpoint.getKeywords(auth);

            // setup message for callback
            sendResult(callback, CallbackHandler.GET_KEYWORDS_RESULT, null, result);


        } catch (Exception e) {
            Log.e(TAG, "Error getting keywords", e);
            sendResult(callback, CallbackHandler.SEND_EVENT_RESULT, e.getMessage(), null);
        }
    }

    /**
     * Handle send event on background thread
     */
    private void handleSendEvent(final Messenger callback, Authenticator auth, CbEvent event) {
        // call http endpoint to send event
        try {
            String error = "CBRestService.error or post:sendEvent";

            // call httpService
            CbEventResponse result = endpoint.sendEvent(auth, event);

            // setup message for callback
            sendResult(callback, CallbackHandler.SEND_EVENT_RESULT, null, result);


        } catch (Exception e) {
            // TODO fix - never just catch generic exceptions
            sendResult(callback, CallbackHandler.SEND_EVENT_RESULT, e.getMessage(), null);
        }
    }

    private void handleRegisterDevice(final Messenger callback, CbDevice device) {
        try {
            String error = "service error: register device";
            Object result = "result placeholder";

            // connect with registration API
            result = endpoint.registerDevice(device);

            // persist device and set registered
            CbDevice regDevice = (CbDevice) result;
            regDevice.save(this);
            CbSharedPreferences.setCbDetailsMobileNumber(this, regDevice.phoneNumber);

            auth.setAuthToken(regDevice._id, regDevice.jwt);

            // no whitelist -> demo mode switch not used.
            // if device ok and not active - not whitelisted.
      /*
      if ( ! CbDevice.ACTIVE.equalsIgnoreCase(device.status)) {
        Authenticator.setDemoMode(true);
      }
      */

            // using preloaded schedule
            // regDevice.schedule = PreloadSchedule.getSchedule();
            // AdvertManager.initAdRotator(regDevice.schedule);

            CbDatabase database = DatabaseFactory.getCbDatabase(this);
            database.upsert(regDevice);

            // if no schedule yet - check preloads
            // preloads done in app init first time - needs file unpack
            // startup saves to database
            if (null == regDevice.schedule) {
                CbSchedule savedSchedule = database.getCbSchedule();
                if (null != savedSchedule) {
                    device.schedule = savedSchedule;
                }
            } else {
                // received schedule as part of registration
                database.upsert(regDevice.schedule);
            }

            // setup message for callback
            sendResult(callback, CallbackHandler.REGISTER_DEVICE_RESULT, null, result);
        } catch (Exception e) {
            // TODO deal with generic exceptions
            // TODO if registration fails - use demo mode
            // TODO fix - never just catch generic exceptions


            // TODO - whitelist fail - if whitelist - send auth fail to reg handler.
            if (e.getMessage().equals("Authorization fail.")) {
                Log.d(TAG, "Authorization failed, proceed in demo mode?");
                Authenticator.setDemoMode(true);
            }

            // if internet not available - ignore... for Demo client
            if (CbAdsSdk.sIsDemoClient || Authenticator.getDemoMode()) {
                sendResult(callback, CallbackHandler.REGISTER_DEVICE_RESULT, "DEMO_MODE", null);
            }

            sendResult(callback, CallbackHandler.REGISTER_DEVICE_RESULT, e.getMessage(), null);
        }
    }

    /**
     * Retrieve schedule from server
     *
     * @see IntentService
     */
    public static Intent getScheduleIntent(Context context, CallbackHandler handler,
                                           CbDevice device) {
        return getScheduleIntent(context, handler, device, null);
    }

    /**
     * Retrieve schedule from server
     *
     * @see IntentService
     */
    public static Intent getScheduleIntent(Context context, CallbackHandler handler, CbDevice device,
                                           List<String> keywords) {
        Intent intent = new Intent(context, CbRestService.class);
        intent.setAction(ACTION_GET_SCHEDULE);
        if (handler != null) {
            intent.putExtra(EXTRA_CALLBACK, new Messenger(handler));
        }
        if (keywords != null) {
            ArrayList<String> keywordsAl = keywords instanceof ArrayList ? (ArrayList<String>) keywords :
                    new ArrayList<>(keywords);
            intent.putStringArrayListExtra(EXTRA_KEYWORDS, keywordsAl);
        }
        intent.putExtra(EXTRA_DEVICE, device);
        return intent;
    }

    /**
     * handle schedule sync in the provided background thread with the provided parameters.
     */
    private void handleGetSchedule(final Messenger callback, Authenticator auth,
                                   List<String> keywords) {
        try {
            // if request not running...
            CbSchedule schedule = endpoint.getSchedule(auth, keywords);

            if (null == schedule.scheduleType) {
                schedule.scheduleType = "PREFS";
            }
            // updates advert manager with new adverts
            if (!ScheduleController.contains(schedule._id)) {
                AdvertManager.syncNewSchedule(schedule);
                ScheduleController.add(schedule._id);
            }

            // setup message for callback
            sendResult(callback, CallbackHandler.GENERATE_SCHEDULE_RESULT, null, schedule);
        } catch (Exception e) {
            // FIXME never just catch generic exceptions
            sendResult(callback, CallbackHandler.GENERATE_SCHEDULE_RESULT, e.getMessage(), null);
        }
    }

    public static Intent getUpdatePreferencesIntent(Context context, CallbackHandler<?> handler,
                                                    CbPreferenceData prefData) {
        Intent intent = new Intent(context, CbRestService.class);
        intent.setAction(ACTION_UPDATE_PREFERENCES);
        if (null != handler) {
            intent.putExtra(EXTRA_CALLBACK, new Messenger(handler));
        }
        intent.putExtra(EXTRA_PREFERENCE_DATA, prefData);
        return intent;
    }

    private void handleSendPreference(Messenger callback, Authenticator auth,
                                      CbPreferenceData prefdata) {
        String error = "service error: send preferences";
        Object result = "result placeholder";
        try {
            result = endpoint.sendPreferences(auth, prefdata);
            sendResult(callback, CallbackHandler.SEND_PREFERENCE_RESULT, null, result);
        } catch (Exception e) {
            if (e.getMessage().equals("Authorization fail.")) {
                Log.d(TAG, "Authorization failed, proceed in demo mode");
                Authenticator.setDemoMode(true);
            }
            sendResult(callback, CallbackHandler.SEND_PREFERENCE_RESULT, e.getMessage(), null);
        }
    }


    public static Intent getSendUserDataIntent(Context context, CallbackHandler<?> handler,
                                               CbUserInfo userData) {
        Intent intent = new Intent(context, CbRestService.class);
        intent.setAction(ACTION_SEND_USERDATA);
        if (null != handler) {
            intent.putExtra(EXTRA_CALLBACK, new Messenger(handler));
        }
        intent.putExtra(EXTRA_USER_INFO_DATA, userData);
        return intent;
    }

    private void handleSendUserData(Messenger callback, Authenticator auth, CbUserInfo userInfo) {
        Log.d(TAG, "Sending user data");
        String error = "service error: send userdata";
        Object result = "result placeholder";
        try {
            result = endpoint.sendUserInfo(auth, userInfo);
            sendResult(callback, CallbackHandler.SEND_USERDATA_RESULT, null, result);
        } catch (Exception e) {
            sendResult(callback, CallbackHandler.SEND_USERDATA_RESULT, e.getMessage(), null);
        }
    }

    /**
     * get default schedule
     *
     * @see IntentService
     */
    public static Intent getDefaultScheduleIntent(Context context, CallbackHandler<?> handler) {
        Intent intent = new Intent(context, CbRestService.class);
        intent.setAction(ACTION_GET_DEFAULT_SCHEDULE);
        if (null != handler) {
            intent.putExtra(EXTRA_CALLBACK, new Messenger(handler));
        }
        return intent;
    }

    /**
     * handle get default schedule
     */
    private void handleGetDefaultSchedule(final Messenger callback, Authenticator auth) {
        // TODO: Handle action getDefaults
        String error = "service error: get default schedule";
        Object result = "result placeholder";
        try {
            // if request not running...
            result = endpoint.getDefaultSchedule(auth);

            // TODO reset to download
            CbSchedule sched = (CbSchedule) result;
            if (null == sched.scheduleType) {
                sched.scheduleType = "DEFAULT";
            }

            // updates advert manager with new adverts
            sched = DefaultSchedule.setSchedule(sched);


            // setup message for callback
            sendResult(callback, CallbackHandler.GET_DEFAULT_SCHEDULE_RESULT, null, result);
        } catch (Exception e) {
            // TODO fix - never just catch generic exceptions
            sendResult(callback, CallbackHandler.GET_DEFAULT_SCHEDULE_RESULT, e.getMessage(), null);
        }

    }


    /**
     * Baz sample intent
     *
     * @see IntentService
     */
    public static Intent getActionBazIntent(Context context, CallbackHandler<?> handler,
                                            String param1, String param2) {
        Intent intent = new Intent(context, CbRestService.class);
        intent.setAction(ACTION_BAZ);
        if (null != handler) {
            intent.putExtra(EXTRA_CALLBACK, new Messenger(handler));
        }
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        return intent;
    }

    /**
     * Baz sample intent Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleNoAction(String action) {
        throw new UnsupportedOperationException("Cb Service ACTION not yet implemented " + action);
    }

    // for managing error returns through callback mechanism
    public static final int CB_ERROR = -1;
    public static final int CB_SUCCESS = 1;


    /**
     * internal call that sends result message back to callback handler
     *
     * @param cb
     * @param error
     * @param result
     */
    public void sendResult(Messenger cb, int handlerType, String error, Object result) {
        Message msg = Message.obtain();

        msg.what = handlerType; // result type - associated with callback interface... in
        // CallbackHandler...

        if (null == error) {
            msg.arg1 = CB_SUCCESS;
            msg.obj = result;
        } else {
            msg.arg1 = CB_ERROR;
            msg.obj = error;
        }

        try {
            if (null != cb) {
                cb.send(msg);
            }
        } catch (RemoteException | RuntimeException e) {
            Log.e(TAG, "Error sending the result", e);
        }
    }

    public void handleNotAuthenticated() {
        // TODO handle not authenticated
        Log.d(TAG, "authentication error: ");
    }


    private static void handleDemoMode() {
        Log.d(TAG, "handle demo mode");
    }

}

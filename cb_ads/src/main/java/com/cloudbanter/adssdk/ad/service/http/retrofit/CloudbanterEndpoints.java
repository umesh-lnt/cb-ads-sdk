package com.cloudbanter.adssdk.ad.service.http.retrofit;


import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.cloudbanter.adssdk.ad.model.AdMix;
import com.cloudbanter.adssdk.ad.model.CbDevice;
import com.cloudbanter.adssdk.ad.model.CbLocation;
import com.cloudbanter.adssdk.ad.service.Authenticator;
import com.cloudbanter.adssdk.ad.util.CbSharedPreferences;
import com.cloudbanter.adssdk.model.ad_blender.AdsConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Ugljesa Jovanovic (ionspinf@gmail.com) on 14-Oct-2016
 */
public class CloudbanterEndpoints {
  public static final String TAG = CloudbanterEndpoints.class.getSimpleName();
  
  private static CloudbanterEndpoints instance;
  
  private Retrofit retrofit;
  private Retrofit locationRetrofit;
  CloudbanterService cloudbanterService;
  LocationService locationService;
  private Authenticator authenticator;
  private CbDevice device;
  private Handler handler;
  private HandlerThread handlerThread;
  private Context context;
  
  public static synchronized void init(Context context, String baseUrl, String locationServerUrl) {
    Log.d(TAG, "BaseUrl: " + baseUrl);
    instance = new CloudbanterEndpoints(context, baseUrl, locationServerUrl);
  }
  
  public static synchronized CloudbanterEndpoints getInstance() {
    return instance;
  }
  
  private CloudbanterEndpoints(Context context, String baseUrl, String locationServerUrl) {
    this.context = context;
    
    handlerThread = new HandlerThread("CloudbanterEndpointsThread");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
    retrofit = createRetrofitClient(baseUrl);
    locationRetrofit = createRetrofitClient(locationServerUrl);
    
    cloudbanterService = retrofit.create(CloudbanterService.class);
    locationService = locationRetrofit.create(LocationService.class);
    
    if (CbSharedPreferences.isRegistered(context)) {
      device = CbDevice.restore(context);
      if (device != null) {
        Log.d(TAG, "Device was null, try again later");
      }
    } else {
      Log.d(TAG, "Device is not registered");
    }
    authenticator = Authenticator.getInstance(context);
    if (authenticator.isAuthenticated()) {
      Log.d(TAG, "User registered");
    }
  }
  
  private Retrofit createRetrofitClient(String serverUrl) {
    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(
                    new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build();
    return new Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
  }
  
  public void getAdMix(final EndpointOperationCallback<AdMix> callback) {
    if (device == null) {
      Log.d(TAG, "Device doesn't exist yet, try later");
      device = CbDevice.restore(context);
      if (device == null) {
        Log.d(TAG, "Device null even after restore.");
      }
      return;
    }
    Log.d(TAG, "Requesting ad mix: " + device._id + " auth : " + authenticator.authToken);
    
    handler.post(new Runnable() {
      @Override
      public void run() {
        try {
          Call<AdMix> call = cloudbanterService.getAdMix(device._id, authenticator.authToken);
          Log.d(TAG, "Calling: " + call.request().toString());
          Response<AdMix> response = call.execute();
          callback.onSuccess(response.body());
        } catch (Exception e) {
          callback.onFailure("Exception! " + e.getMessage(), e);
        }
      }
    });
  }
  
  
  public void getUserLocationByIp(final EndpointOperationCallback<CbLocation> callback) {
    locationService.getLocationByIp().enqueue(new Callback<CbLocation>() {
      @Override
      public void onResponse(Call<CbLocation> call, Response<CbLocation> response) {
        if (response.isSuccessful()) {
          callback.onSuccess(response.body());
        } else {
          callback.onFailure("Error requesting location",
                  new Exception("Error requesting location by IP"));
        }
      }
      
      @Override
      public void onFailure(Call<CbLocation> call, Throwable t) {
        callback.onFailure("Failure", new Exception(t));
      }
    });
  }
  
  public void getAdBlenderConfig(final EndpointOperationCallback<AdsConfig> callback) {
    if (!authenticator.isAuthenticated()) {
      authenticator = Authenticator.getInstance(context);
    }
    cloudbanterService.getAdBlenderConfig(authenticator.authToken)
            .enqueue(new Callback<AdsConfig>() {
              @Override
              public void onResponse(Call<AdsConfig> call, final Response<AdsConfig> response) {
                if (response.isSuccessful()) {
                  callback.onSuccess(response.body());
                } else {
                  callback.onFailure("Error getting AdBlender config",
                          new Exception("Error getting AdBlender config"));
                }
              }
              
              @Override
              public void onFailure(Call<AdsConfig> call, Throwable t) {
                callback.onFailure("Failure", new Exception(t));
              }
            });
  }
}

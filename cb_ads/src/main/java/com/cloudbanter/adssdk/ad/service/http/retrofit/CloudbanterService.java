package com.cloudbanter.adssdk.ad.service.http.retrofit;

import com.cloudbanter.adssdk.ad.model.AdMix;
import com.cloudbanter.adssdk.model.ad_blender.AdsConfig;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author Ugljesa Jovanovic (ionspinf@gmail.com) on 14-Oct-2016
 */
public interface CloudbanterService {
  
  @GET("devices/{deviceId}/mix")
  Call<AdMix> getAdMix(@Path("deviceId") String deviceId, @Query("jwt") String authToken);
  
  @GET("constants/device")
  Call<AdsConfig> getAdBlenderConfig(@Query("jwt") String authToken);
}

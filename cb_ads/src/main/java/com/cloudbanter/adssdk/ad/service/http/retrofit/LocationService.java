package com.cloudbanter.adssdk.ad.service.http.retrofit;


import com.cloudbanter.adssdk.ad.model.CbLocation;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 */
public interface LocationService {
  
  @GET("json")
  Call<CbLocation> getLocationByIp();
}

package com.cloudbanter.adssdk.ad.service.http.retrofit;

/**
 * @author Ugljesa Jovanovic (ionspinf@gmail.com) on 14-Oct-2016
 */
public abstract class EndpointOperationCallback<T> {
  public static final String TAG = EndpointOperationCallback.class.getSimpleName();

  public abstract void onSuccess(T result);

  public abstract void onFailure(String errorMessage, Exception exception);

}

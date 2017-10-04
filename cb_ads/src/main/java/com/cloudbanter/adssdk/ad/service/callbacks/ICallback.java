package com.cloudbanter.adssdk.ad.service.callbacks;


/**
 * Created by eric on 6/17/15.
 */
public interface ICallback<T> {
  void handleError(String s);

  void onSuccess(T data);
}

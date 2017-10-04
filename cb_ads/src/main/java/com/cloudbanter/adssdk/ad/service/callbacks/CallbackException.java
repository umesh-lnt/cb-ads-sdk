package com.cloudbanter.adssdk.ad.service.callbacks;

/**
 * Created by eric on 8/9/15.
 */
public class CallbackException extends Exception {
  public CallbackException() {
    super();
  }

  public CallbackException(String message) {
    super(message);
  }

  public CallbackException(String message, Throwable cause) {
    super(message, cause);
  }

  public CallbackException(Throwable cause) {
    super(cause);
  }
}

package com.cloudbanter.adssdk.ad_exchange.presentation.ad_view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

/**
 * Methods available for ads management
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 28/2/17
 */
public interface AdPresenter {

  /**
   * This method should be called the very first time to initialize the view. Preferably this
   * method should be invoked on {@link Activity#onCreate(Bundle)}
   *
   * @param context
   *         Application context
   */
  void init(Context context);

  /**
   * This method should be located on {@link Activity#onResume()}. Resumes the ads and starts the
   * cycle of showing ads from networks
   */
  void resumeAds();

  /**
   * This method should be located on {@link Activity#onResume()}. Pauses the ads cycle but don't
   * destroy the views
   */
  void pauseAds();

  /**
   * This method should be located on {@link Activity#onDestroy()}. Destroys the views to free
   * memory. {@link #init(Context)} should be invoked to start showing ads again
   */
  void destroy();

}

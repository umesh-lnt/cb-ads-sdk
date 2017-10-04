package com.cloudbanter.adssdk.ad_exchange.presentation.ad_view;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Expected view for AdView management
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 28/2/17
 */
public interface AdView {

  /**
   * Shows the given ad and replaces the existing ad
   *
   * @param adView
   *         Ad view to be shown
   */
  void showAd(@NonNull View adView);
}

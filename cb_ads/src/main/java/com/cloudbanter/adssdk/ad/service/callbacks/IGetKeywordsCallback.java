package com.cloudbanter.adssdk.ad.service.callbacks;

import android.support.annotation.NonNull;

import com.cloudbanter.adssdk.ad.model.CbKeywords;

/**
 * Callback to get the keywords request result
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 27/1/17
 */
public interface IGetKeywordsCallback {

  /**
   * Called if keywords list has retrieved successfully
   *
   * @param keywords
   *         Keywords
   */
  void onKeywordsListSuccess(@NonNull CbKeywords keywords);

}

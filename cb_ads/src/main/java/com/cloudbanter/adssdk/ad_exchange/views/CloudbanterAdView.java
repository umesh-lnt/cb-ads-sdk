package com.cloudbanter.adssdk.ad_exchange.views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.cloudbanter.adssdk.ad_exchange.presentation.ad_view.AdPresenter;
import com.cloudbanter.adssdk.ad_exchange.presentation.ad_view.AdView;
import com.cloudbanter.adssdk.ad_exchange.presentation.ad_view.AdsPresenterImpl;


/**
 * Cloudbanter Ad layout which is used to show all Ads from networks
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 28/2/17
 */
public class CloudbanterAdView extends FrameLayout implements AdView {

  /**
   * The Ad presenter instance. Note: Don't use directly this instance, use
   * {@link #getPresenter()}
   **/
  private AdPresenter mAdPresenter;

  /** Required constructor **/
  public CloudbanterAdView(Context context) {
    super(context);
  }

  /** Required constructor **/
  public CloudbanterAdView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  /** Required constructor **/
  public CloudbanterAdView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  /** Required constructor **/
  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public CloudbanterAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  /**
   * Initializes the ads to be shown. In order to start showing ads, {@link #onResume()} should be
   * called on {@link Activity#onResume()}
   */
  public void init() {
    getPresenter().init(getContext());
  }

  /**
   * This method resumes the ads cycle and should be called on {@link Activity#onResume()}
   */
  public void onResume() {
    getPresenter().resumeAds();
  }

  /**
   * This method pauses the ads cycle and should be called on {@link Activity#onPause()} ()}
   */
  public void onPause() {
    getPresenter().pauseAds();
  }

  /**
   * This method destroys the views and should be called on {@link Activity#onDestroy()} ()} ()}
   */
  public void onDestroy() {
    getPresenter().destroy();
  }

  /**
   * Instantiates correctly the presenter to be returned and returns a singleton of the configured
   * instance
   *
   * @return Singleton of the presenter instance
   */
  @NonNull
  private AdPresenter getPresenter() {
    if (mAdPresenter == null) {
      mAdPresenter = new AdsPresenterImpl(this);
    }
    return mAdPresenter;
  }

  @Override
  public void showAd(@NonNull View adView) {
    removeAllViews();
    removeViewParent(adView);
    addView(adView);
  }

  /**
   * Removes the view parent (if any)
   *
   * @param view
   *         View to remove parent
   */
  private void removeViewParent(View view) {
    ViewParent parent = view.getParent();
    if (parent != null) {
      ((ViewGroup) parent).removeView(view);
    }
  }
}

package com.cloudbanter.adssdk.util;

import android.os.CountDownTimer;
import android.support.annotation.NonNull;

/**
 * Infinite timer which reports to a listener every configured tick
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 28/2/17
 */
public class InfiniteTimer {

  /** Default sequences per interval **/
  private static final int DEFAULT_SEQUENCES = 10;

  /** Callback for tick notifications **/
  private final OnTickCallback mCallback;

  /** Indicates if the initial tick should be omitted **/
  private boolean mOmitInitialTick;

  /** Flag that indicates if the next tick should be omitted **/
  private boolean mOmitNext;

  /** Count down timer **/
  private CountDownTimer mCountDownTimer;

  /**
   * @param millisTickInterval
   *         Interval in which {@link OnTickCallback#onTick()} is called
   * @param omitInitialTick
   *         True if the initial tick should be omitted (tick when {@link CountDownTimer#start()})
   *         is called
   * @param callback
   *         Callback used to notify the tick
   */
  public InfiniteTimer(long millisTickInterval, boolean omitInitialTick,
                       @NonNull OnTickCallback callback) {
    this.mOmitInitialTick = omitInitialTick;
    this.mCallback = callback;
    mCountDownTimer =
            new CountDownTimer(millisTickInterval * DEFAULT_SEQUENCES, millisTickInterval) {
              @Override
              public void onTick(long millisUntilFinished) {
                if (!mOmitNext) {
                  mCallback.onTick();
                }
                mOmitNext = false;
              }

              @Override
              public void onFinish() {
                start();
              }
            };
  }

  /**
   * Starts the infinite timer. Every tick will be called using {@link OnTickCallback#onTick()}
   */
  public void start() {
    mOmitNext = mOmitInitialTick;
    mCountDownTimer.start();
  }

  /**
   * Cancels the infinite timer.
   */
  public void cancel() {
    mCountDownTimer.cancel();
  }

  /**
   * Used to notify when a tick has been done
   */
  public interface OnTickCallback {

    /**
     * Called when a tick is completed based on the
     */
    void onTick();
  }
}

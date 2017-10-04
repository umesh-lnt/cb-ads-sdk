package com.cloudbanter.adssdk.ad_exchange.domain.observers;

import android.os.Handler;
import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Subscription that notifies when some ads' states change
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio</a>
 * @since 5/18/17
 */
public final class AdsStateSubscription {

  /** Singleton instance **/
  private static AdsStateSubscription sInstance;

  /** Observers **/
  private final Set<AdsStateChangeObserver> mObservers = new HashSet<>();

  /**
   * Gets the singleton instance
   *
   * @return The singleton {@link AdsStateSubscription} instance
   */
  @NonNull
  public static AdsStateSubscription getInstance() {
    if (sInstance == null) {
      sInstance = new AdsStateSubscription();
    }
    return sInstance;
  }

  /** Private to avoid outside instances **/
  private AdsStateSubscription() {
  }

  /**
   * Registers an observer to the subscription
   *
   * @param observer
   *         Observer to be registered
   */
  public void register(@NonNull AdsStateChangeObserver observer) {
    mObservers.add(observer);
  }

  /**
   * Unregisters an observer from the subscription
   *
   * @param observer
   *         Observer to be unregistered
   */
  public void unregister(@NonNull AdsStateChangeObserver observer) {
    mObservers.remove(observer);
  }

  /**
   * Submits to the observers the received event
   *
   * @param event
   *         Event to be submitted
   */
  public void submitEvent(@NonNull final Event event) {
    submitEventAsynchronously(event);
  }

  private void submitEventAsynchronously(@NonNull final Event event) {
    new Handler().post(new Runnable() {
      @Override
      public void run() {
        for (AdsStateChangeObserver observer : mObservers) {
          submitEvent(observer, event);
        }
      }
    });
  }

  /**
   * Submits the given event to the given observer
   *
   * @param observer
   *         Observer to be notified
   * @param event
   *         Event to be sumbitted
   */
  private void submitEvent(@NonNull AdsStateChangeObserver observer, @NonNull Event event) {
    switch (event) {
      case WAKE_UP:
        observer.onWakeUp();
        break;
      case SLEEP:
        observer.onSleep();
        break;
      default:
        throw new IllegalArgumentException("No event supported: " + event);
    }
  }

  /** Available states for ads **/
  public enum Event {
    /** The service has started **/
    WAKE_UP,

    /** The service has stopped **/
    SLEEP
  }

  /**
   * Observer of the events registered by the subscription
   */
  public interface AdsStateChangeObserver {

    /** Called when the ads process has started **/
    void onWakeUp();

    /** Called when the ads process has stopped **/
    void onSleep();

  }

}

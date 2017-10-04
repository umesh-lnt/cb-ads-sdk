package com.cloudbanter.adssdk.ad_exchange.domain.ad_view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.cloudbanter.adssdk.ad.manager.BannerGrabber;
import com.cloudbanter.adssdk.ad.manager.EventAggregator;
import com.cloudbanter.adssdk.ad.model.AdMix;
import com.cloudbanter.adssdk.ad_exchange.ad_networks.AdNetwork;
import com.cloudbanter.adssdk.ad_exchange.domain.managers.AdBlenderManager;
import com.cloudbanter.adssdk.ad_exchange.domain.observers.AdsStateSubscription;
import com.cloudbanter.adssdk.ad_exchange.views.CustomEventBanner;
import com.cloudbanter.adssdk.model.ad_blender.AdNetworkConfig;
import com.cloudbanter.adssdk.model.ad_blender.AdsConfig;
import com.cloudbanter.adssdk.util.ClassUtils;
import com.cloudbanter.adssdk.util.MathUtils;
import com.cloudbanter.adssdk.util.NetworkUtils;
import com.mopub.mobileads.MoPubErrorCode;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static java.lang.Math.max;

/**
 * Implementation of the {@link AdsInteractor}
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 28/2/17
 */
class AdsInteractorImpl implements AdsInteractor,
        AdsStateSubscription.AdsStateChangeObserver, AdBlenderManager.AdsConfigObserver {
  
  /** Tag for logs **/
  private static final String TAG = AdsInteractorImpl.class.getSimpleName();
  
  /** Default buffer size **/
  private static final int DEFAULT_BUFFER_SIZE = 3;
  
  /** Next ad network limit. Used to avoid infinite loops while searching for a valid Ad **/
  private static final int NEXT_AD_NETWORK_LIMIT = 100;
  
  /** Maximum time for processing an ad **/
  private static final int MAX_PROCESSING_TIME = 10000;
  
  /** Param that indicates if no ads has been displayed **/
  private static final String WERE_ADS_DISPLAYED_PARAM = "WERE_ADS_DISPLAYED_PARAM";
  
  /** Data per AdNetwork **/
  private final Map<AdNetwork, Map<String, String>> mAdNetworkData = new HashMap<>();
  
  /** AdNetworks Queue **/
  private final Queue<AdNetwork> mProcessingQueue = new LinkedList<>();
  
  /** Cached views from networks **/
  private final Queue<CachedView> mCachedViews = new LinkedList<>();
  
  /** Application context **/
  private final Context mContext;
  
  /** Requested mix/blender **/
  private final AdMix mRequiredAdMix = new AdMix();
  
  /** Current mix/blender **/
  private final AdMix mCurrentAdMix = new AdMix();
  
  /** Ads shown since last loop **/
  private int mAdsShownCount = 0;
  
  /** System beginning flag. Used to mark the start of the system **/
  private boolean mIsWakingUp = true;
  
  /** Flag to know if an ad is being loaded **/
  private boolean mIsProcessingAds = false;
  
  /** Start process timestamp **/
  private long mProcessStartTimestamp = -1;
  
  /** Cached last preloaded banner. This is used to show a preloaded banner when system wakes up **/
  private CachedView mLastPreloadedBanner;
  
  /**
   * Constructor
   *
   * @param context
   *         Application context
   */
  AdsInteractorImpl(Context context) {
    mContext = context;
    readAdNetworksData();
    postponeNextAdNetwork();
    updateMix();
    AdsStateSubscription.getInstance().register(this);
    AdBlenderManager.getInstance().registerToAdsConfigChanges(this);
  }
  
  /**
   * Updates the required mix for Ads stored at server
   */
  private void updateMix() {
    processAdMix(AdBlenderManager.getInstance().getAdMix());
  }
  
  /**
   * Caches into the
   */
  private synchronized void cacheNextAdNetwork() {
    if (mCachedViews.size() >= DEFAULT_BUFFER_SIZE) {
      Log.i(TAG, "Reached maximum cached Ad views");
      return;
    }
    markProcessStarting();
    final AdNetwork nextAdNetwork = nextAdNetwork();
    Log.i(TAG, "Caching AdNetwork: " + nextAdNetwork.name());
    final CustomEventBanner banner = ClassUtils.newInstance(nextAdNetwork.getBannerClass());
    if (banner == null) {
      postponeNextAdNetwork();
      return;
    }
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        loadBanner(nextAdNetwork, banner);
      }
    });
  }
  
  /**
   * Marks the process starting
   */
  private void markProcessStarting() {
    mIsProcessingAds = true;
    mProcessStartTimestamp = Calendar.getInstance().getTimeInMillis();
  }
  
  /**
   * Marks the process ending
   */
  private void markProcessEnding() {
    mIsProcessingAds = false;
  }
  
  /**
   * Checks if there is a current processing ad. If some time has passed without answer, then it
   * allows processing
   *
   * @return mIsProcessingAds
   */
  public boolean isProcessingAds() {
    long curTime = Calendar.getInstance().getTimeInMillis();
    return mIsProcessingAds && (curTime - mProcessStartTimestamp) <= MAX_PROCESSING_TIME;
  }
  
  /**
   * Loads the banner given the {@link AdNetwork} and the target {@link CustomEventBanner}
   *
   * @param nextAdNetwork
   *         Target {@link AdNetwork}
   * @param banner
   *         Target banner
   */
  private void loadBanner(final AdNetwork nextAdNetwork, final CustomEventBanner banner) {
    banner.loadBanner(mContext, new SimpleCustomEventBannerListener() {
              /** Reference to the loaded banner view **/
              private View mBannerView;
              
              @Override
              public void onBannerLoaded(View bannerView) {
                Log.d(TAG, "Banner successfully loaded for " + nextAdNetwork);
                markProcessEnding();
                mBannerView = bannerView;
                enqueueLoadedBanner(bannerView, nextAdNetwork);
                banner.addViewEvent(mBannerView);
                postponeNextAdNetwork();
              }
              
              @Override
              public void onBannerFailed(MoPubErrorCode errorCode) {
                Log.e(TAG, "Error loading banner for " + nextAdNetwork + ": " + errorCode);
                markProcessEnding();
                processBannerFailed(nextAdNetwork, errorCode);
                postponeNextAdNetwork();
              }
              
              @Override
              public void onBannerClicked() {
                super.onBannerClicked();
                Log.d(TAG, "Banner clicked: " + nextAdNetwork);
                addClickEvent(mBannerView);
              }
              
            }, buildLocalExtras(nextAdNetwork),
            mAdNetworkData.get(nextAdNetwork));
  }
  
  /**
   * Adds a click event for a given banner
   *
   * @param bannerView
   *         Target banner
   */
  private void addClickEvent(View bannerView) {
    if (bannerView instanceof ViewGroup) {
      String source = BannerGrabber.getInstance().handleFavourites((ViewGroup) bannerView);
      EventAggregator ea = EventAggregator.getInstance();
      if (ea != null) {
        ea.addClick(source);
      }
    }
  }
  
  /**
   * Processes the failed {@link AdNetwork} and performs actions in case is needed
   *
   * @param failedAdNetwork
   *         FailedAdNetwork
   * @param errorCode
   *         Error coded
   */
  private void processBannerFailed(AdNetwork failedAdNetwork, MoPubErrorCode errorCode) {
    if (failedAdNetwork == AdNetwork.PRELOADED_ADS) {
      mAdsShownCount = 0;
    }
    if (failedAdNetwork == AdNetwork.CLOUDBANTER && errorCode == MoPubErrorCode.NO_FILL) {
      resetCounter();
    }
  }
  
  /**
   * Enqueues a loaded banner with its corresponding {@link AdNetwork}
   *
   * @param bannerView
   *         Loaded banner
   * @param nextAdNetwork
   *         Parent {@link AdNetwork}
   */
  private void enqueueLoadedBanner(View bannerView, AdNetwork nextAdNetwork) {
    CachedView cachedView = new CachedView(nextAdNetwork, bannerView);
    if (nextAdNetwork == AdNetwork.PRELOADED_ADS) {
      mLastPreloadedBanner = cachedView;
    }
    mCachedViews.add(cachedView);
  }
  
  /**
   * Marks the loaded banner if it's not {@link AdNetwork#PRELOADED_ADS} ad
   *
   * @param adNetwork
   *         Ad network to be marked
   */
  private void markLoadedBanner(AdNetwork adNetwork) {
    mAdsShownCount = adNetwork != AdNetwork.PRELOADED_ADS ? mAdsShownCount + 1 : 0;
  }
  
  /**
   * Generates local extras for the given {@link AdNetwork}. Mostly of the cases, they will be
   * empty
   * extras, but for {@link AdNetwork#PRELOADED_ADS} it may indicate when no other ads have been
   * displayed by its own {@link AdNetwork}
   *
   * @param adNetwork
   *         Ad network to process
   *
   * @return Map of local extras
   */
  @NonNull
  private Map<String, Object> buildLocalExtras(AdNetwork adNetwork) {
    if (adNetwork != AdNetwork.PRELOADED_ADS) {
      return new HashMap<>(0);
    }
    Map<String, Object> localExtras = new HashMap<>();
    localExtras.put(WERE_ADS_DISPLAYED_PARAM, mAdsShownCount != 0);
    return localExtras;
  }
  
  /**
   * Retrieves the next AdNetwork to be processed. The resulting AdNetwork is sent to the end of
   * the queue for future processing
   *
   * @return The next AdNetwork
   */
  private AdNetwork nextAdNetwork() {
    AdNetwork nextAdNetwork;
    int counter = 0;
    do {
      nextAdNetwork = mProcessingQueue.poll();
      mProcessingQueue.add(nextAdNetwork);
      counter++;
    } while (!isValidAdNetwork(nextAdNetwork) && counter < NEXT_AD_NETWORK_LIMIT);
    if (counter >= NEXT_AD_NETWORK_LIMIT) {
      nextAdNetwork = AdNetwork.PRELOADED_ADS;
    }
    return nextAdNetwork;
  }
  
  /**
   * {@link #isValidAdNetwork(AdNetwork, boolean)} is called with no validation on preloaded ads by
   * default
   */
  private boolean isValidAdNetwork(AdNetwork adNetwork) {
    return isValidAdNetwork(adNetwork, false);
  }
  
  /**
   * Check if the given network is valid to be processed. A valid {@link AdNetwork} fulfill one of
   * the following premises:
   * <p>
   * - Is null (means no available network, which is valid)
   * - Is preloaded ad
   * - If not null and has not reached the limit of ads
   * - For Cloudbanter ad network, it will only be displayed if:
   * <t/>- Has not reached the ads limit and the other AdNetworks have completed their limit
   * - The limit of the ads is < 0 (edge case, shouldn't ever happen)
   *
   * @param adNetwork
   *         AdNetwork to be validated
   * @param validatePreloadedAds
   *         Validate preloaded ads. For this case, preloaded ads is valid if no ads have been
   *         loaded
   *
   * @return True if is a valid {@link AdNetwork}
   */
  private boolean isValidAdNetwork(AdNetwork adNetwork, boolean validatePreloadedAds) {
    if (validatePreloadedAds && adNetwork == AdNetwork.PRELOADED_ADS) {
      boolean isWakingUp = this.mIsWakingUp;
      this.mIsWakingUp = false;
      return isWakingUp || mAdsShownCount == 0 && !NetworkUtils.isOnline(mContext);
    }
    if (adNetwork == null || adNetwork == AdNetwork.PRELOADED_ADS ||
            mRequiredAdMix.getConsole() <= 0 && mRequiredAdMix.getAdExchange() <= 0) {
      return true;
    }
    resetCounterIfNeeded();
    
    if (adNetwork == AdNetwork.CLOUDBANTER) {
      return mCurrentAdMix.getConsole() < mRequiredAdMix.getConsole() &&
              mCurrentAdMix.getAdExchange() >= mRequiredAdMix.getAdExchange();
    } else {
      return mCurrentAdMix.getAdExchange() < mRequiredAdMix.getAdExchange();
    }
  }
  
  /**
   * Resets the ads counter if fulfills one of the following premises:
   * <p>
   * - There's no internet connection (while no internet connection, rotation is needed)
   * - The limits of the ads console and AdNetworks have been reached
   */
  private void resetCounterIfNeeded() {
    if (mCurrentAdMix.getConsole() >= mRequiredAdMix.getConsole() &&
            mCurrentAdMix.getAdExchange() >= mRequiredAdMix.getAdExchange()) {
      resetCounter();
    }
  }
  
  /**
   * Resets the mix counter
   */
  private void resetCounter() {
    mCurrentAdMix.setConsole(0);
    mCurrentAdMix.setAdExchange(0);
  }
  
  /**
   * Reads the AdNetworks data
   */
  private void readAdNetworksData() {
    mAdNetworkData.clear();
    mProcessingQueue.clear();
    List<AdNetworkConfig> adNetworkConfigs = AdBlenderManager.getInstance().getAdNetworkConfigs();
    for (AdNetworkConfig config : adNetworkConfigs) {
      addAdNetwork(config);
    }
  }
  
  /**
   * Add AdNetwork given the AdNetworkConfig
   *
   * @param config
   *         AdNetwork config to be applied
   */
  private void addAdNetwork(AdNetworkConfig config) {
    if (config.getAdNetwork() != null) {
      mAdNetworkData.put(config.getAdNetwork(), config.getData());
      mProcessingQueue.add(config.getAdNetwork());
    }
  }
  
  @Override
  @Nullable
  public View pollCachedAd() {
    Log.i(TAG, "Polled cached Ad");
    CachedView cachedView = mCachedViews.poll();
    Log.d(TAG, "Polled cached Ad: " + cachedView);
    if (cachedView != null && !isValidAdNetwork(cachedView.mAdNetwork, true)) {
      return null;
    }
    if (cachedView != null) {
      incrementMix(cachedView.mAdNetwork);
      markLoadedBanner(cachedView.mAdNetwork);
    }
    if (!isProcessingAds()) {
      postponeNextAdNetwork();
    }
    return cachedView == null ? null : cachedView.mCachedView;
  }
  
  /**
   * Increments the mix counters given the polled cached view
   *
   * @param adNetwork
   *         Cached view
   */
  private void incrementMix(AdNetwork adNetwork) {
    incrementMix(adNetwork, 1);
  }
  
  /**
   * Increments the mix counters given the polled cached view
   *
   * @param adNetwork
   *         Cached view
   * @param increment
   *         The value to be incremented
   */
  private void incrementMix(AdNetwork adNetwork, int increment) {
    if (adNetwork == AdNetwork.PRELOADED_ADS) {
      // Preloaded ads shouldn't be counted as a valid ad as they don't generate revenue
      return;
    }
    if (adNetwork == AdNetwork.CLOUDBANTER) {
      mCurrentAdMix.setConsole(max(mCurrentAdMix.getConsole() + increment, 0));
    } else {
      mCurrentAdMix.setAdExchange(max(mCurrentAdMix.getAdExchange() + increment, 0));
    }
    Log.d(TAG, "Current state of mix/blend ads: " + mCurrentAdMix);
  }
  
  /**
   * Postpones the next network to be cached a few milliseconds. This is done to avoid extra
   * processing in some steps
   */
  private void postponeNextAdNetwork() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        cacheNextAdNetwork();
      }
    }).start();
  }
  
  /**
   * Processes the new ad mix. It converts percentage into the amount of ads that each ad network
   * should show
   *
   * @param newAdMix
   *         New ad mix to be set
   */
  private void processAdMix(AdMix newAdMix) {
    int gcd = MathUtils.gcd(newAdMix.getAdExchange(), newAdMix.getConsole());
    mRequiredAdMix.setAdExchange(newAdMix.getAdExchange() / gcd);
    mRequiredAdMix.setConsole(newAdMix.getConsole() / gcd);
    Log.d(TAG, "New mix/blend for ads " + mRequiredAdMix);
  }
  
  @Override
  public void onWakeUp() {
    AdBlenderManager.getInstance().registerToAdsConfigChanges(this);
    mIsWakingUp = true;
  }
  
  @Override
  public void onSleep() {
    AdBlenderManager.getInstance().unregisterToAdsConfigChanges(this);
    if (mLastPreloadedBanner == null && !mCachedViews.isEmpty()) {
      return;
    }
    mCachedViews.add(mLastPreloadedBanner);
  }
  
  @Override
  public void onAdsConfigChanged(AdsConfig adsConfig) {
    readAdNetworksData();
    updateMix();
  }
  
  /**
   * Model to store the cached views
   */
  private class CachedView {
    
    /** Cached ad network **/
    AdNetwork mAdNetwork;
    
    /** Cached view **/
    View mCachedView;
    
    /**
     * Constructor
     *
     * @param adNetwork
     *         Cached ad network
     * @param cachedView
     *         Cached view
     */
    CachedView(AdNetwork adNetwork, View cachedView) {
      this.mAdNetwork = adNetwork;
      this.mCachedView = cachedView;
    }
    
    @Override
    public String toString() {
      return mAdNetwork.name();
    }
  }
  
}

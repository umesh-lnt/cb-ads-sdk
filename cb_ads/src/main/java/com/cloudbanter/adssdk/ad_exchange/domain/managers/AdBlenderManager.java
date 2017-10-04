package com.cloudbanter.adssdk.ad_exchange.domain.managers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.cloudbanter.adssdk.ad.model.AdMix;
import com.cloudbanter.adssdk.ad.service.http.retrofit.CloudbanterEndpoints;
import com.cloudbanter.adssdk.ad.service.http.retrofit.EndpointOperationCallback;
import com.cloudbanter.adssdk.ad.util.CbSharedPreferences;
import com.cloudbanter.adssdk.ad_exchange.ad_networks.AdNetwork;
import com.cloudbanter.adssdk.ad_exchange.domain.observers.AdsStateSubscription;
import com.cloudbanter.adssdk.model.ad_blender.AdNetworkConfig;
import com.cloudbanter.adssdk.model.ad_blender.AdsConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is part of the new ads system. This component is responsible to get all data related to Ads,
 * AdNetworks, rotation and others
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio</a>
 * @since 8/16/17
 */
public class AdBlenderManager extends EndpointOperationCallback<AdsConfig>
        implements AdsStateSubscription.AdsStateChangeObserver {
  
  /** Tag for logs **/
  private static final String TAG = AdBlenderManager.class.getSimpleName();
  
  /** Gson instance **/
  private static final Gson GSON = new GsonBuilder().create();
  
  /** Default mix count for console **/
  private static final int DEFAULT_MIX_CONSOLE_ADS = 1;
  
  /** Default mix count for Ad exchange ads **/
  private static final int DEFAULT_MIX_AD_EXCHANGE_ADS = 9;
  
  /** Default rotation time in secs **/
  private static final int DEFAULT_ROTATION_TIME = 5;
  
  /** Default delay for profile and preferences in days **/
  private static final int DEFAULT_PROFILE_PREFERENCES_DELAY = 2 * 24 * 60 * 60;
  
  /** Comparator between AdNetworkConfigs **/
  private static final Comparator<? super AdNetworkConfig> AD_NETWORK_CONFIG_COMPARATOR =
          new Comparator<AdNetworkConfig>() {
            @Override
            public int compare(AdNetworkConfig o1, AdNetworkConfig o2) {
              return Integer.compare(o1.getOrder(), o2.getOrder());
            }
          };
  
  /** Singleton instance **/
  private static AdBlenderManager sInstance;
  
  /** Application context **/
  private final WeakReference<Context> mContext;
  
  /** Default AdsConfig **/
  private final AdsConfig mDefaultAdsConfig;
  
  /** Ads config observers **/
  private final Set<AdsConfigObserver> mAdsConfigObservers = new HashSet<>();
  
  /** Is updated flag **/
  private boolean isUpdated;
  
  /** Is processing **/
  private boolean isProcessing;
  
  /**
   * Initializes the AdBlender manager
   *
   * @param context
   *         Application context
   */
  public static synchronized void init(Context context) {
    if (sInstance == null) {
      sInstance = new AdBlenderManager(context);
      sInstance.start();
    } else if (sInstance.needUpdate()) {
      sInstance.start();
    } else {
      Log.w(TAG, "AdBlenderManager already initialized");
    }
  }
  
  /**
   * Needs to be updated if and only if hasn't been already updated and no request is being
   * processed
   *
   * @return True if needs to be updated
   */
  private boolean needUpdate() {
    return !isUpdated && !isProcessing;
  }
  
  /**
   * Gets the existing instance
   *
   * @return AdBlenderManager instance or null if hasn't been initialized
   */
  public static AdBlenderManager getInstance() {
    return sInstance;
  }
  
  /**
   * Constructor
   *
   * @param context
   *         Application context
   */
  private AdBlenderManager(Context context) {
    this.mContext = new WeakReference<>(context);
    this.mDefaultAdsConfig = buildDefaultAdsConfig(context);
    AdsStateSubscription.getInstance().register(this);
  }
  
  /**
   * Builds an instance of the default ads config
   *
   * @param context
   *         Application context
   *
   * @return Default Ads Config instance
   */
  @NonNull
  private AdsConfig buildDefaultAdsConfig(Context context) {
    AdsConfig adsConfig = new AdsConfig();
    adsConfig.setRotationInSecs(DEFAULT_ROTATION_TIME);
    adsConfig.setProfilePreferencesDelayInSecs(DEFAULT_PROFILE_PREFERENCES_DELAY);
    adsConfig.setAdMix(new AdMix(DEFAULT_MIX_CONSOLE_ADS, DEFAULT_MIX_AD_EXCHANGE_ADS));
    adsConfig.setAdNetworkConfigs(readAdNetworksData(context));
    return adsConfig;
  }
  
  /**
   * Reads the AdNetworks data
   *
   * @param context
   *         Application context
   */
  private List<AdNetworkConfig> readAdNetworksData(Context context) {
    AdNetwork[] adNetworks = AdNetwork.values();
    List<AdNetworkConfig> adNetworkConfigs = new ArrayList<>(adNetworks.length);
    int i = 0;
    for (AdNetwork adNetwork : adNetworks) {
      adNetworkConfigs.add(new AdNetworkConfig(adNetwork, i++,
              buildMap(context.getString(adNetwork.getDataRes()))));
    }
    return adNetworkConfigs;
  }
  
  /**
   * Build map from jsonData
   *
   * @param jsonData
   *         Json to be transformed
   *
   * @return Map of parsed data
   */
  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  private Map<String, String> buildMap(String jsonData) {
    Map<String, String> map = new HashMap<>();
    return GSON.fromJson(jsonData, map.getClass());
  }
  
  /**
   * Initializes the complete system
   */
  private void start() {
    updateDataFromServer();
  }
  
  /**
   * Retrieves and updates the data from server
   */
  private void updateDataFromServer() {
    isProcessing = true;
    CloudbanterEndpoints.getInstance().getAdBlenderConfig(this);
  }
  
  /**
   * Registers a given observer to get ads config changes notifications
   *
   * @param observer
   *         Observer to be registered
   */
  public void registerToAdsConfigChanges(AdsConfigObserver observer) {
    if (observer != null) {
      mAdsConfigObservers.add(observer);
    }
  }
  
  /**
   * Unregister an observer from ads config changes
   *
   * @param observer
   *         Observer to be unregistered
   */
  public void unregisterToAdsConfigChanges(AdsConfigObserver observer) {
    mAdsConfigObservers.remove(observer);
  }
  
  /**
   * Gets the rotation time in millis
   *
   * @return Rotation time in millis
   */
  public int getRotationTimeInMillis() {
    return getAdsConfig().getRotationInSecs() * 1000;
  }
  
  /**
   * Gets the profile and preferences delay in millis
   *
   * @return Profile and preferences delay in millis
   */
  public int getProfilePreferencesDelayInMillis() {
    return getAdsConfig().getProfilePreferencesDelayInSecs() * 1000;
  }
  
  /**
   * Gets the {@link AdMix}
   *
   * @return AdMix instance
   */
  public AdMix getAdMix() {
    return getAdsConfig().getAdMix();
  }
  
  /**
   * Gets the correct ads config. If no AdsConfig has been downloaded from server, then the default
   * AdsConfig is returned. Otherwise the stored AdsConfig will be returned
   *
   * @return Current AdsConfig instance
   */
  @NonNull
  private AdsConfig getAdsConfig() {
    Context context = mContext.get();
    return context == null ? mDefaultAdsConfig :
            CbSharedPreferences.getAdsConfig(context, mDefaultAdsConfig);
  }
  
  @Override
  public void onSuccess(AdsConfig result) {
    Log.i(TAG, "Ads config successfully retrieved from server");
    isProcessing = false;
    if (mContext.get() != null) {
      CbSharedPreferences.setAdsConfig(mContext.get(), processAdsConfig(result));
      isUpdated = true;
      notifyChange();
    }
  }
  
  /**
   * Notifies all subscribed observers about the current AdsConfig
   */
  private synchronized void notifyChange() {
    AdsConfig adsConfig = getAdsConfig();
    for (AdsConfigObserver observer : mAdsConfigObservers) {
      observer.onAdsConfigChanged(adsConfig);
    }
  }
  
  @Override
  public void onFailure(String errorMessage, Exception exception) {
    Log.i(TAG, "Failure while getting Ads Config");
    isProcessing = false;
  }
  
  /**
   * Processes the given ads config to make it fit to the correct structure. For instance, order the
   * ad networks
   *
   * @param adsConfig
   *         Ads config to be processed
   *
   * @return Returns the processed AdsConfig, ordered and cleaned
   */
  private AdsConfig processAdsConfig(AdsConfig adsConfig) {
    if (adsConfig.getAdNetworkConfigs() == null) {
      return adsConfig;
    }
    Collections.sort(adsConfig.getAdNetworkConfigs(), AD_NETWORK_CONFIG_COMPARATOR);
    return adsConfig;
  }
  
  public List<AdNetworkConfig> getAdNetworkConfigs() {
    return getAdsConfig().getAdNetworkConfigs();
  }
  
  @Override
  public void onWakeUp() {
    if (!isProcessing) {
      start();
    }
  }
  
  @Override
  public void onSleep() {
    
  }
  
  /**
   * Interface used to notify observers when an AdsConfig change has been made
   */
  public interface AdsConfigObserver {
    
    /**
     * Called when AdsConfig has changed
     *
     * @param adsConfig
     *         The new AdsConfig
     */
    void onAdsConfigChanged(AdsConfig adsConfig);
  }
}

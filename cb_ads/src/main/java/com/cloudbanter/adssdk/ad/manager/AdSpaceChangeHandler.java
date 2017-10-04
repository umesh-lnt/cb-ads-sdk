package com.cloudbanter.adssdk.ad.manager;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;

import java.lang.ref.WeakReference;

public class AdSpaceChangeHandler extends Handler {

  /*
   * take care with hanging on to callbacks - especially ui activities on main thread - prone to
   * memory leakage...
   */
  public interface AdSpaceUpdater {
    public void updateAdSpace(CbScheduleEntry entry);
  }

  private final WeakReference<AdSpaceUpdater> mActivity;

  public AdSpaceChangeHandler(Activity activity) {
    try {
      mActivity = new WeakReference<AdSpaceUpdater>((AdSpaceUpdater) activity);
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
              + " must implement AdvertManager.AdSpaceUpdater");
    }
  }

  @Override
  public void handleMessage(Message msg) {
    AdSpaceUpdater updater = (AdSpaceUpdater) mActivity.get();
    CbScheduleEntry entry = (CbScheduleEntry) msg.obj;

    if (null != updater) {
      updater.updateAdSpace(entry);
    }
  }
}

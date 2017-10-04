package com.cloudbanter.adssdk.ad.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CbAutoRunService extends Service {

  public int onStartCommand(Intent intent, int flags, int startId) {
    mContext = this;

    Log.d("com.cloudbanter.mms", "auto run service start");
    new TimeRunnerThread().start();
    return START_STICKY;
  }

  static Context mContext;

  static void startCbDownloadService() {
    Intent intent = new Intent(mContext, CbDownloadService.class);
    mContext.startService(intent);
  }

  @Override
  public IBinder onBind(Intent arg0) {
    return null;
  }

  private static Object monitor = new Object();
  private static boolean broadbandConnected = false;
  private static boolean running = false;

  public static class TimeRunnerThread extends Thread {
    public void run() {
      while (running) {
        // calculate time of day 
        boolean lowUsageTime = true;
        if (broadbandConnected && lowUsageTime) {
          startCbDownloadService();
        }
        // Thread sleep time
        long sleepTime = 60 * 60 * 1000; // milliseconds
        try {
          Thread.sleep(sleepTime);
        } catch (InterruptedException e1) {

        }
      }
      System.out.println("Cloudbanter Time Based Download Manager halted...");
    }
  }

}

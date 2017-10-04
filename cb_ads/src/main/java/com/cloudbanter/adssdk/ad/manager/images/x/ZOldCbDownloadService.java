package com.cloudbanter.adssdk.ad.manager.images.x;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import com.cloudbanter.adssdk.R;
import com.cloudbanter.adssdk.ad.manager.images.ImageRef;
import com.cloudbanter.adssdk.ad.model.CbSchedule;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

// TODO triggers for download ( startup, new schedule, time based ) 
// Called when app started or
// boot received
// ---  beWellBehaved() ie: when wifi.isavailable && low impact time of day , 
//      --- and use CbProtocol
// --- start and stop yourself

public class ZOldCbDownloadService extends IntentService {
  
  protected static final String TAG = ZOldCbDownloadService.class.getSimpleName();
  
  public static final String EXTRA_SCHEDULE = "schedule_extra";
  public static final String EXTRA_SYNC_IMAGES = "sync_images_extra";
  
  private static final Queue<ImageRef> que = new LinkedList<ImageRef>();
  private static final Object lock = new Object();
  
  Thread download = null;                      // worker
  // thread
  boolean debug;
  boolean forceDownloads;
  String bucketUrl;
  String splitImageFilePrefix;
  
  Context mContext = this;
  CbSchedule mSchedule;
  
  // CbImageManager.mSyncImages -- list of images that need to be downloaded
  ArrayList<ImageRef> mSyncImages = new ArrayList<ImageRef>();
  
  public ZOldCbDownloadService() {
    super("CbDownloadService");
  }
  
  public IBinder onBind(Intent intent) {
    return null;
  }
  
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (download != null) {
      download.interrupt(); // Request immediate action by downloader
    }
    Log.d(TAG, "onStart sync:");
    for (ImageRef r : mSyncImages) {
      Log.d(TAG, "queueing img: " + r.mImageId);
      que.add(r);
    }
    
    // start threaded process
    run(startId);
    return START_STICKY;
  }
  
  public void onCreate() {
    // TODO move to configuration options...
    
    // This goes into a Constants file...
    debug = getResources().getBoolean(R.bool.debugThis);
    
    // Ok here.
    forceDownloads = getResources().getBoolean(R.bool.forceDownloads);
    
    // TODO move bucketURL to configuration
    bucketUrl = getResources().getString(R.string.imagesBucketUrl);
    
    // Goes to Image ref / mgr
    splitImageFilePrefix = getResources().getString(R.string.splitImageFilePrefix);
    
  }
  
  public void onDestroy() {
    super.onDestroy();
  }
  
  @Override
  protected void onHandleIntent(Intent intent) {
    Log.d(TAG, "handleintent - image sync");
    for (ImageRef r : mSyncImages) {
      que.add(r);
    }
    run(0);
  }
  
  // TODO fix threading model ( QueueThread / FetchThread sync on que )
  private void run(final int startId) {
    download = new Thread() {
      public void run() {
        // TODO manage run time...
        // long startTime =
        // cyclical
        // between hours of midnight and 6am local
        // if ( not now ) wait safely;
        // if (! isOnline() ) wait safely || check with user...
        
        final int sequentialReference = startId;
        new Thread() {
          public void run() {
            Log.d(TAG, "download attempt" + String.valueOf(sequentialReference));
            download(sequentialReference);
            forceDownloads = false;
            // TODO download complete callback...
          }
        }.start();
      }
    };
    download.start();
  }
  
  static boolean running = false;
  
  private void download(int sequentialReference) {
    ImageRef ref = null;
    // if (serious bandwidth - parallelize...?
    while (null != (ref = que.peek())) {
      if (isOnline() && null != ref && que.contains(ref) && fetchImage(ref)) {
        que.remove(ref); // when complete...
        waitToRestart(false);
      } else {
        waitToRestart(true);
      }
    }
  }
  
  static final long INITIAL_WAIT_TIME = 500;
  static final long WAIT_TIME_MULTIPLIER = 2;
  static final long MAX_WAIT_TIME = 3600000;
  static long waitTime = INITIAL_WAIT_TIME;
  Thread waitThread = null;
  
  // waits exponentially longer ...
  private void waitToRestart(boolean wait) {
    Log.d(TAG, "waiting to retry download");
    if (!wait) {
      waitTime = INITIAL_WAIT_TIME;
      if (null != waitThread) {
        waitThread.interrupt();
      }
      return;
    }
    try {
      if (waitTime < MAX_WAIT_TIME) {
        waitTime *= WAIT_TIME_MULTIPLIER;
      }
      if (null == waitThread) {
        waitThread = new Thread();
      }
      waitThread.sleep(waitTime);
    } catch (InterruptedException e) {
      waitTime = INITIAL_WAIT_TIME;
    }
  }
  
  private boolean fetchImage(ImageRef img) {
    if (null != img.mUrl && !ImageRef.isPresent(img)) {
      
      String fileName = ImageRef.getFileName(img);
      
      // TODO update to https...
      byte[] ba = httpGetFile(img.mUrl.replace("https", "http"));
      if (null != ba && ba.length > 0) {
        writeLocalFile(fileName, ba);
        img.mSavedFileName = fileName;
        return true;
      }
    }
    return false;
  }
  
  // TODO if offline, use CbProtocol
  private byte[] httpGetFile(String url) {
    if (null == url) {
      return null;
    }
    final ByteArrayOutputStream r = new ByteArrayOutputStream();
    try {
      final HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
      c.connect();
      Log.d(TAG, "Download bytecount: " + c.getContentLength());
      final InputStream i = c.getInputStream();
      final byte[] b = new byte[8192];
      // TODO fix 1 Mb limit ??
      for (int j = 0; j < 1000; ++j) {
        int l = i.read(b, 0, b.length);
        if (l < 0) {
          break;
        }
        r.write(b, 0, l);
      }
      c.disconnect();
      Log.d(TAG, "Read " + r.size() + " bytes from " + url);
      return r.toByteArray();
    } catch (UnknownHostException ue) { // UnknownHostException ... reset emulator ?
      Log.d(TAG,
              "Cannot download url: " + url + " because: " + ue.getMessage() + " reset emulator?");
    } catch (Exception e) { // TODO manage connection errors: unqualified Exception is REALLY BAD
      // FORM
      Log.d(TAG, "Cannot download url: " + url + " because: " + e);
    }
    return null;
  }
  
  private void writeLocalFile(String file, byte[] data) {
    try {
      FileOutputStream o = openFileOutput(file, Context.MODE_PRIVATE);
      o.write(data);
      o.close();
    } catch (Exception e) {
      Log.d(TAG, "Unable to write to local file" + file + " because " + e);
    }
  }
  
  private void findMe() {
    File file = new File(this.getFilesDir(), "find_me");
    try {
      file.createNewFile();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public boolean isOnline() {
    ConnectivityManager cm =
            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    return netInfo != null && netInfo.isConnectedOrConnecting();
  }
  
}

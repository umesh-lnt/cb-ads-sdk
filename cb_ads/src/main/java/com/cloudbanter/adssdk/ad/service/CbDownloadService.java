package com.cloudbanter.adssdk.ad.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cloudbanter.adssdk.ad.manager.images.ImageRef;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class CbDownloadService extends IntentService {

  public static final String TAG = CbDownloadService.class.getSimpleName();

  public static final String ACTION_ADD_TO_QUEUE = "ActionAddToQueue";
  public static final String ACTION_PROCESS_ONE = "ProcessImage";

  public static final String EXTRA_IMAGE_COLLECTION = "ExtraImageCollection";
  public static final String EXTRA_IMAGE = "ExtraTheImage";


  public String PATH_PREFIX;

  public CbDownloadService() {
    super("CbDownloadService");
  }

  @Override
  public void onCreate() {
    super.onCreate();
    PATH_PREFIX = getFilesDir().getAbsolutePath() + File.separator;
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent != null) {
      final String action = intent.getAction();
      if (ACTION_ADD_TO_QUEUE.equals(action)) {
        List<ImageRef> images = intent.getParcelableArrayListExtra(EXTRA_IMAGE_COLLECTION);
        queueImages(images);
      } else if (ACTION_PROCESS_ONE.equals(action)) {
        ImageRef ref = intent.getParcelableExtra(EXTRA_IMAGE);
        processImage(ref);
      }
    }
  }

  private void processImage(ImageRef ref) {  // TODO untangle bad defaul
    try {
      if (!ImageRef.isPresent(ref)) {
        // if bytes downloaded...
        Log.d(TAG, "Url: " + ref.mUrl);
        if (ref.mUrl.contains(".null")) {
          if (0 < fetchImage(ref.mUrl.replace(".null", ".png"), ref.mSavedFileName)) {
            if (0 < fetchImage(ref.mUrl.replace(".null", ".gif"), ref.mSavedFileName)) {
              Log.d(TAG, "processImage: .null replace failed...");
            }
          }
        } else if (0 < fetchImage(ref.mUrl, ref.mSavedFileName)) {
          curteousRestartWait(false);
        } else {
          Log.e(TAG, "download issue with zero length file? ");
        }
      }
    } catch (MalformedURLException e) {
      Log.e(TAG, "Bad URL FOR image download " + ref.mUrl);
    } catch (IOException e) {
      // error for now until additional catches handled...
      Log.e(TAG, String.format("Image not downloaded %s %s", e.getMessage(), ref.mUrl));
      Log.e(TAG, "", e);
      // catch (NoInternet) {
      // catch (ServerUnavailable) {
      // catch (ImageNotFound) {
      // TODO back off download pressure
      // curteousRestartWait(true);
    }
  }

  // images cheat from static...
// queue should be persisted
  public static void addToDownloadQueue(Context context, ArrayList<ImageRef> images) {
    Intent intent = new Intent(context, CbDownloadService.class);
    intent.setAction(ACTION_ADD_TO_QUEUE);
    intent.putParcelableArrayListExtra(EXTRA_IMAGE_COLLECTION, images);
    context.startService(intent);
  }

  private void queueImages(List<ImageRef> images) {
    for (ImageRef ref : images) {
      downloadImage(this, ref);
    }
  }

  public static void downloadImage(Context context, ImageRef ref) {
    Intent intent = new Intent(context, CbDownloadService.class);
    intent.setAction(ACTION_PROCESS_ONE);
    intent.putExtra(EXTRA_IMAGE, ref);
    context.startService(intent);
  }

  private long xFetchImage(String remoteFile, String localFile) throws IOException {
    Log.d(TAG, String.format("Downloading: %s %s", PATH_PREFIX + localFile, remoteFile));
    URL remoteFileUrl = new URL(remoteFile);
    int fileSize = remoteFileUrl.openConnection().getContentLength();
    ReadableByteChannel rbc = Channels.newChannel(remoteFileUrl.openStream());
    ByteBuffer buffer = ByteBuffer.allocate(8192);
    buffer.mark();
    FileOutputStream fos = new FileOutputStream(PATH_PREFIX + localFile);
    int readBytes = 0;
    long sumBytesRead = 0;
    while ((readBytes = rbc.read(buffer)) > 0) {
      Log.d(TAG, "Read: " + readBytes);
      sumBytesRead += readBytes;
      fos.write(buffer.array(), 0, buffer.position());
      buffer.reset();
    }
    if (fileSize != sumBytesRead) {
      Log.e(TAG, "Number of downloaded bytes doesn't match returned content length");
    }

    Log.d(TAG, "Done reading, flushing and closing file stream");
    fos.flush();
    fos.close();
    return sumBytesRead;
  }

  // TODO move to Cloudbanter config!
  static final long INITIAL_WAIT_TIME = 512;
  static final long WAIT_TIME_MULTIPLIER = 2; // 1.5 ?
  static final long MAX_WAIT_TIME = 3600000; // 1 hour
  static long waitTime = INITIAL_WAIT_TIME;
  Thread waitThread = null;

  // waits exponentially longer ...
  void curteousRestartWait(boolean wait) {
    // if ! wait = all good, reset to normal download
    if (!wait) {
      waitTime = INITIAL_WAIT_TIME;
      if (null != waitThread) {
        waitThread.interrupt();
      }
      return;
    }
    Log.d(TAG, "waiting to retry download - exponential back off to ");
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

  private long fetchImage(String remoteFile, String localFile) throws IOException {
    Log.d(TAG, String.format("Downloading: %s %s", PATH_PREFIX + localFile, remoteFile));
    URL remoteFileUrl = new URL(remoteFile);
    int fileSize = remoteFileUrl.openConnection().getContentLength();
    ReadableByteChannel rbc = Channels.newChannel(remoteFileUrl.openStream());
    ByteBuffer buffer = ByteBuffer.allocate(8192);
    File pathFile = new File(PATH_PREFIX);
    if (!pathFile.exists()) {
      Log.d(TAG, "Path did not exist, creating");
      pathFile.mkdirs();
    }
    FileOutputStream fos = new FileOutputStream(PATH_PREFIX + localFile);
    int readBytes = 0;
    long sumBytesRead = 0;
    while ((readBytes = rbc.read(buffer)) > 0) {
      Log.d(TAG, "Read: " + readBytes);
      sumBytesRead += readBytes;
      fos.write(buffer.array(), 0, buffer.position());
      buffer.clear();
    }
    if (fileSize != sumBytesRead) {
      Log.e(TAG, "Number of downloaded bytes doesn't match returned content length");
    }

    Log.d(TAG, "Done reading, flushing and closing file stream");
    fos.flush();
    fos.close();
    return sumBytesRead;
  }

/*

private void downloadSplitImages(URL remote, String local, String doneCondition) {
  // files = getFileList(getManifest());
  // for (file: files) {
  //   download(file);
  // }
  //
}

// online check -- make calls to cbHttpService (factory)
private boolean isOnline()
{
  Context mContext = getApplicationContext();
  try {
    ConnectivityManager cm = (ConnectivityManager) mContext
            .getSystemService(Context.CONNECTIVITY_SERVICE);
    return cm.getActiveNetworkInfo().isConnectedOrConnecting();
  } catch (Exception e) {
    return false;
  }
}

// LOCAL IMAGE STORE
public static class ImageQueue  {

  public static boolean imagesOnQueue() {
    return ! images.isEmpty();
  }

  public static ImageRef getNextImage() {
    return images.getFirst();
  }

  public static void imageHandled(ImageRef r) {
    if (isDownloaded(r)) {
      images.remove(r);
    } else {
      images.addLast(r);
    }
  }

  public static void checkQueue() {
    for(Iterator<ImageRef> it = images.iterator(); it.hasNext(); ) {
      ImageRef r = it.next();
      if(isDownloaded(r)) {
        it.remove();
      }
    }
  }

  // use only for removing images that can't be downloaded
  public static void remove(ImageRef ref) {
    images.remove(ref);
  }

  public static boolean addImages(Collection<ImageRef> moreImages) {
    // add to end of queue
    return images.addAll(images.size(), moreImages);
  }

  private static LinkedList<ImageRef> images = new LinkedList<ImageRef>();
}


//  for activity/fragment/service - callback impl
@SuppressLint("ParcelCreator")
public static class DownloadImageReceiver extends ResultReceiver {
  private Receiver mReceiver;

  public DownloadImageReceiver(Handler handler) {
    super(handler);
  }
  public void setReceiver(Receiver receiver) {
    mReceiver = receiver;
  }
  public interface Receiver {
    public void onReceiveResults(int resultCode, Bundle resultData);
  }
  @Override
  protected void onReceiveResult(int resultCode, Bundle resultData) {
    if(mReceiver != null) {
      mReceiver.onReceiveResults(resultCode, resultData);
    }
  }
}

//  for activity/fragment - callback impl
//  example receiver code
@Override
public void onReceiveResults(int resultCode, Bundle resultData) {
  switch (resultCode) {
    case CbDownloadService.STATUS_RUNNING:
      // set progress bar .. visibility
      break;
    case CbDownloadService.STATUS_FINISHED:
      // hide progress bar
      break;
    case CbDownloadService.STATUS_ERROR:
      break;
  }
}
*/

}

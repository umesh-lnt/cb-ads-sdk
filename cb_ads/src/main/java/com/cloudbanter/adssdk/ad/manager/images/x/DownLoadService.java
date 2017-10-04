/*------------------------------------------------------------------------------
Image Manager - Download service
Philip R Brenan, philip at toptal dot com, © CloudBanter, 2015/07/30 16:38:45
------------------------------------------------------------------------------*/
package com.cloudbanter.adssdk.ad.manager.images.x;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.cloudbanter.adssdk.R;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Stack;

public class DownLoadService extends Service {
  final String imageFilePrefix = DisplayManagerActivity.imageFilePrefix;
          // Prefix to identify image files
  final String splitImageFilePrefix = DisplayManagerActivity.splitImageFilePrefix;
          // Prefix to identify split image files
  final String splitImageDetailsPrefix = DisplayManagerActivity.splitImageDetailsPrefix;
          // Prefix to identify split image files manifest
  String imagesBucketUrl = null;
          // Bucket containing unsplit images

  boolean testing = true;
          // Set true for demonstrations so that downloads start immediately, otherwise false
  boolean forceDownloads = false;
          // Whether downloads should be forced on the first set of downloads
  Speech speech = null;                                                         // Speech generator
  Thread download = null;                                                       // Worker thread

  public IBinder onBind(Intent intent) {
    return null;
  }

  public int onStartCommand(Intent intent, int flags,
                            int startId)             // Called when app started or boot received
  {
    if (download != null) {
      download.interrupt();                                 // Request immediate action by
    }
    // downloader
    say("Download manager: onStartCommand");
    return START_STICKY;
  }

  public void onCreate() {
    super.onCreate();
    testing = getResources().getBoolean(
            R.bool.debugThis);                // Set true for demonstrations so that downloads
    // start immediately
    forceDownloads = getResources().getBoolean(
            R.bool.forceDownloads);         // Set true for demonstrations so that downloads are
    // immediately refreshed
    imagesBucketUrl = getResources().getString(
            R.string.imagesBucketUrl);       // Url to bucket containing images
    say("Download manager: onCreate");
    speech = new Speech();
    run();
  }

  void run()                                                                    // Start a
  // download on a new thread if now is a good time to do so
  {
    download = new Thread() {
      public void run() {
        final long start = t();
        for (int I = 0; ; ++I) {
          final int i = I;
          if (testing) {
            minutes(1 + i / 60);                                         // Quickly if testing
          } else {
            hours(1);                                                        // Slowly if production
          }

          final GregorianCalendar now = new GregorianCalendar();
          if (!testing && now.get(GregorianCalendar.HOUR_OF_DAY) > 6) {
            continue; // Only Midnight to 6.00am local time unless in test
          }

          new Thread()                                                          // Do the
                  // downloads on a separate thread so that if there are any problems this thread
                  // is preserved, this technique also reduces the number of sleep states from 2
                  // to 1 in each thread allowing interrupt to be used.
          {
            public void run() {
              download(i);
              forceDownloads =
                      false;                                           // Only be forceful on the
              // first set of downloads
            }
          }.start();
        }
      }
    };
    download.start();
  }

  public void onDestroy() {
    super.onDestroy();
  }

  /*------------------------------------------------------------------------------
  Download all new files
  ------------------------------------------------------------------------------*/
  void download(int request) {
    final DisplayManagerActivity im =
            DisplayManagerActivity.displayManager;                    // Address display manager

    say("DownloadManager: start downloads");                                    // Download manifest

    final String manifest = downloadString(imagesBucketUrl + "manifest.txt");     // Manifest
    if (manifest ==
            null)                                                       // Complain if no manifest
    {
      err("No image manifest, please create one");
      return;
    }

    say("Manifest\n" + manifest);
    final Stack<Stack<String>> parse = im.splitTwice(manifest);                 // Parse manifest

    say("Parse\n" + parse);
    final Stack<String> skipped = new Stack<String>();                          // Skipped elements

    for (Stack<String> line : parse)                                              // Each parsed
    // line
    {
      if (line.size() < 2) {
        continue;                                            // Ignore blank lines
      }
      final String cmd = line.elementAt(0);                                    // Command
      final String name = line.elementAt(1);                                    // Image name

      if (!forceDownloads && im.images.images.containsKey(
              name))                // Skipped elements already downloaded
      {
        skipped.push(name);
        continue;
      }

      if (cmd.equalsIgnoreCase("image"))                                        // Unsplit images
      {
        final String url =
                imagesBucketUrl + name;                                // File holding image

        say("Download image: " + request + " cmd=" + cmd + " name=" + name + " url=" + url);
        final String f = imageFilePrefix + name;                                  // Local file name
        final byte[] i = execUrl(url);
        if (i != null &&
                i.length > 0)                                          // Avoid writing empty files
        {
          writeLocalFile(f, i);
          im.images.newDetail(
                  name);                                            // Record details of new
          // unsplit image
        }
      } else if (cmd.equalsIgnoreCase("split_image"))                             // Split images
      {
        say("Download split image: " + request + " cmd=" + cmd + " name=" + name);
        downloadSplitImage(name);
      } else                                                                      // Unknown command
      {
        err("Ignoring unknown command", cmd);
      }
    }
    if (skipped.size() >
            0)                                                     // Report skipped elements
    {
      say("Skipped the download of " + skipped.size() + " images.");
    }
    say("Image cache contains " + im.images.images.size() +
            " images.");            // Report number of images in cache
  }

  /*------------------------------------------------------------------------------
  Download a split image
  ------------------------------------------------------------------------------*/
  void downloadSplitImage(final String name) {
    final String folder = imagesBucketUrl + name +
            "/";                            // Folder name containing splits
    final byte[] bytes = execUrl(folder +
            "details.txt");                       // Read image description url as bytes
    final String Details = new String(bytes);                                   // Get image details
    final DisplayManagerActivity im =
            DisplayManagerActivity.displayManager;                    // Address display manager

    final DisplayManagerActivity.Images.SplitDetail d =
            // Parse details of split image
            im.images.newSplitDetail(name, Details);

    say("Download split image " + name);                                          // Progress

    for (int y = 1; y <= d.Height;
         ++y)                                        // Download each block in the image
    {
      for (int x = 1; x <= d.Width; ++x) {
        final String pos =
                "" + y + "_" + x;                                         // Position of block
        final String url = folder + pos;                                         // Url
        final String file = splitImageFilePrefix + name + "_part_" + pos;            // Block name
        if (forceDownloads || !im.files.contains(
                file))                         // Download url if file not already present on
        // file system or we are being forceful
        {
          final byte[] i = execUrl(url);                                       // Download url
          if (i != null && i.length > 0) {
            writeLocalFile(file, i);                                            // Save block
            im.files.add(
                    file);                                                 // Show file as present
            d.present(x,
                    y);                                                     // Mark block as loaded
          }
        }
      }
    }

    writeLocalFile("splitImage_details_" + name,
            bytes);                          // Save a copy of the image details last so that the
    // prescence of this small file indicates that the image has been downloaded
  }

  /*------------------------------------------------------------------------------
  Get the result from executing a url
  ------------------------------------------------------------------------------*/
  byte[] execUrl(String u) {
    final ByteArrayOutputStream r = new ByteArrayOutputStream();
    try {
      final HttpURLConnection
              c = (HttpURLConnection) new URL(u).openConnection();
      c.connect();
      final InputStream i = c.getInputStream();
      final byte[] b = new byte[1024];
      for (int j = 0; j < 1000; ++j) {
        int l = i.read(b, 0, b.length);
        if (l < 0) {
          break;
        }
        r.write(b, 0, l);
      }
      c.disconnect();
      say("Read " + r.size() + " bytes from " + u);
      return r.toByteArray();
    } catch (Exception e) {
      err("Cannot download url", "" + u + " because: " + e);
    }
    return null;
  }

  /*------------------------------------------------------------------------------
  Download a string
  ------------------------------------------------------------------------------*/
  String downloadString(final String url) {
    final byte[] s =
            execUrl(url);                                              // Read content of url as
    // bytes
    if (s == null) {
      return null;                                                 // Return null if nothing
    }
    // received
    return new String(
            s);                                                       // Return string from bytes
  }

  /*------------------------------------------------------------------------------
  Write to a local file
  ------------------------------------------------------------------------------*/
  void writeLocalFile(String file, byte[] data) {
    try {
      FileOutputStream o = openFileOutput(file, Context.MODE_PRIVATE);
      o.write(data);
      o.close();
      say("Wrote " + data.length + " bytes to file " + file);
    } catch (Exception e) {
      err("Unable to write to local file", "" + file + " because " + e);
    }
  }

  //------------------------------------------------------------------------------
// Say something - more convenient than searching the log
//------------------------------------------------------------------------------
  class Speech implements TextToSpeech.OnInitListener {
    final TextToSpeech tts;
    boolean ready = false;

    Speech() {
      tts = new TextToSpeech(DownLoadService.this, this);
    }

    void say(String... S) {
      for (String s : S) {
        if (tts != null) {
          tts.speak(s, TextToSpeech.QUEUE_ADD, null, null);
        }
      }
    }

    public void onInit(int status) {
      if (status == TextToSpeech.SUCCESS) {
        ready = true;
      }
    }

    void language(String s) {
      int result = tts.setLanguage(Locale.US);
      tts.setSpeechRate(0.5f);
      tts.setPitch(0.5f);
    }

    void destroy() {
      if (tts != null) {
        tts.stop();
        tts.shutdown();
      }
    }
  }

  /*------------------------------------------------------------------------------
  Utility functions
  ------------------------------------------------------------------------------*/
  long t() {
    return System.currentTimeMillis();
  }

  float sinceSeconds(long t) {
    return (float) ((t() - t) / 1000l);
  }

  void seconds(int s) {
    try {
      Thread.sleep(s * 1000);
    } catch (Exception e) {
    }
  }       // Sleep a number of seconds

  void minutes(int m) {
    seconds(60 * m);
  }                                          // Sleep a number of minutes

  void hours(int h) {
    minutes(60 * h);
  }                                          // Sleep a number of hours

  final String LogTag = "IMDownload";
          // Write a message to the log

  void say(String s) {
    Log.w(LogTag, s);
  }

  void err(String s) {
    Log.e(LogTag, s);
    speech.say(s);
  }

  void err(String s, String S) {
    Log.e(LogTag, s + S);
    // speech.say(s);
  }
}

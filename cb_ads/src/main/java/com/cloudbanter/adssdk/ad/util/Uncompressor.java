package com.cloudbanter.adssdk.ad.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;


import com.cloudbanter.adssdk.CbAdsSdk;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Uncompressor {
  public static final String TAG = "Uncompressor";

  private String mSrcFileName;
  private String mTargetDirectory;

  public Uncompressor(String src, String tgt) {
    mSrcFileName = src;
    mTargetDirectory = tgt;
  }

  public void unzip() {
    try {
      Context app = CbAdsSdk.getApplication();
      InputStream in = null;
      ZipInputStream zin = null;
      ZipEntry ze = null;
      try {
        AssetManager assetManager = app.getAssets();
        // debug:
        String[] assets1 = assetManager.list("");
        // debug:
        String[] assets2 = assetManager.list("/");
        in = assetManager.open(mSrcFileName);
        zin = new ZipInputStream(in);
        ze = null;

        String dir = "";
        while (null != (ze = zin.getNextEntry())) {

          // log entry
          if (ze.isDirectory()) {
            dir = (mTargetDirectory + "_" + ze.getName()).replace("/", "_").replace("__", "_");
            Log.d(TAG, "dir: " + dir);
            zin.closeEntry();
            continue;
          }

          // hack to remove mac extra stuff
          if (ze.getName().contains("MACOSX")) {
            zin.closeEntry();
            continue;
          }

          String targetFileName = new StringBuffer()
                  .append(dir)
                  // .append(imagesPrefix)
                  .append(ze.getName())
                  .toString();

          Log.d(TAG, "file: " + targetFileName);

          FileOutputStream fos = app.openFileOutput(targetFileName, Context.MODE_PRIVATE);
          byte[] buffer = new byte[8192];
          int len;
          while ((len = zin.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
          }

          zin.closeEntry();
          fos.close();
        }
      } finally {
        // closes?
      }
    } catch (IOException e) {
      // TODO unzip error.
      Log.d("UncompressImages", "IO Exception - " + e.getMessage());
    } catch (Exception e1) {
      Log.d(TAG, "Uncaught Exception" + e1.getMessage());
      e1.printStackTrace();
    }
  }
}

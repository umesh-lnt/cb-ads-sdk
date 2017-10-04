package com.cloudbanter.adssdk.ad.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by eric on 4/5/16.
 */
public class AssetFile {
  public static String getString(Context context, String filename) {
    byte[] ba = null;
    try {
      AssetManager manager = context.getAssets();
      InputStream file = manager.open(filename);
      ba = new byte[file.available()];
      file.read(ba);
      file.close();
    } catch (IOException e) {
      // nothing to get
    }
    return null != ba ? new String(ba) : "";
  }
}

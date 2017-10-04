package com.cloudbanter.adssdk.ad.manager;

import android.content.Context;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.ad.manager.images.ImageRef;
import com.cloudbanter.adssdk.ad.manager.images.Images;
import com.cloudbanter.adssdk.ad.model.CbDevice;
import com.cloudbanter.adssdk.ad.model.CbSchedule;
import com.cloudbanter.adssdk.ad.repo.DatabaseFactory;
import com.cloudbanter.adssdk.ad.util.CbSharedPreferences;
import com.cloudbanter.adssdk.ad.util.Uncompressor;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PreloadContent {
  private static final String TAG = PreloadContent.class.getSimpleName();
  
  private static final Set<String> PREXISTING_FILES =
          new HashSet<>(Collections.singletonList(".Fabric"));
  
  public void preloadImages() {
    installImages();
    initializeSchedule();
    notifyAdvertManager();
  }
  
  public void installImages() {
    
  }
  
  public void initializeSchedule() {
    
  }
  
  public static String getFileNames() {
    // TODO publish preload image asset filename
    
    String preLoadedImageAssetFileName = CbAdsSdk.PRELOAD_IMAGES_ZIPFILE;
    return preLoadedImageAssetFileName;
  }
  
  public static String getTargetDirectory() {
    // TODO get target directory for image assets
    String targetDirectory = "images_";
    return targetDirectory;
  }
  
  public void unzipResourceToFiles() {
    
  }
  
  public void notifyAdvertManager() {
    
  }
  
  public static void init(Context context) {
    // if not already installed...
    CbSchedule manifestSchedule = null;
    if (!arePreloadedUncompressed(context)) {
      Uncompressor u = new Uncompressor(getFileNames(), getTargetDirectory());
      u.unzip();
      
      // TODO load preload schedule
      
      // scan preload files
      File[] files = context.getFilesDir().listFiles();
      ImageRef image = null;
      for (File f : files) {
        String fn = f.getName();
        // decodes filename
        if ("PreloadSchedule.json".equalsIgnoreCase(fn)) {
          ; // read manifest file
        } else {
          image = ImageRef.newDetailFromFileName(fn);
          if (null != image) {
            Images.put(image.mSavedFileName, image);
          }
        }
      }
    }
    
    
    // PROLLY NOT NECESSARY
    // First load if DB.getSchedule = null { preload Schedule }
    if (null == DatabaseFactory.getCbDatabase(context).getCbSchedule()) {
      CbDevice device = null;
      CbSchedule updateSchedule = null;
      if (null != manifestSchedule) {
        updateSchedule = manifestSchedule;
      } else {
        // TODO fix when manifestSched
        updateSchedule = PreloadSchedule.getSchedule();
      }
      
      if (null != updateSchedule) {
        if (CbSharedPreferences.isRegistered(context)) {
          device = CbDevice.fromJson(CbSharedPreferences.getCbDevice(context));
          device.schedule = updateSchedule;
          CbSharedPreferences.setCbDevice(context, device.toJson().toString());
        }
        DatabaseFactory.getCbDatabase(context).upsert(updateSchedule);
      }
    }
  }
  
  private static boolean arePreloadedUncompressed(Context context) {
    for (String file : context.fileList()) {
      if (!PREXISTING_FILES.contains(file)) {
        return true;
      }
    }
    return false;
  }
}

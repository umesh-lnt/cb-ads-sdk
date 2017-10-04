package com.cloudbanter.adssdk.ad.manager.images;

import android.content.Context;
import android.content.ContextWrapper;
import android.widget.ImageView;

import com.cloudbanter.adssdk.ad.model.CbSchedule;
import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;
import com.cloudbanter.adssdk.ad.repo.DatabaseFactory;

import java.io.File;

// TODO S T A T I C implementation
// TODO triggers for scan files[update local state] ( startup, download complete, new schedule )
// TODO service ?

public class CbImageManager extends ContextWrapper {
  protected static final String TAG = CbImageManager.class.getSimpleName();

  CbImageManager(Context context) {
    super(context);
    mImageManager = this;
  }

  public static CbImageManager mImageManager;

  static Context mContext;

  public static ImageView getImageView(String bannerImageUri) {
    // TODO Auto-generated method stub
    return null;
  }

  // startup from MmsApp
  public static void init(Context context) {
    mImageManager = new CbImageManager(context);
    mContext = context;

    scanFiles(context);
    syncSchedule(context);
  }

  // sync / download images to match current schedule...
  public static void syncSchedule(Context context) {

    CbSchedule schedule = DatabaseFactory.getCbDatabase(context).getCbSchedule();
    if (null != schedule) {
      for (CbScheduleEntry entry : schedule.entries) {
        if ((null != entry) && (null != entry.advert)) {
          // TODO fix when client is in sync with the server. AltImageRef for alt download address
          ImageRef.newImageDetailDownload(context, entry.advert);
        }
      }
    }
  }

  // updates known images from list of stored files
  public static void scanFiles(Context context) {

    File[] files = context.getFilesDir().listFiles();
    ImageRef ref = null;
    for (File f : files) {
      String fn = f.getName();
      // decodes filename
      ref = ImageRef.newDetailFromFileName(fn);
      if (null != ref) {
        Images.put(ref.mSavedFileName, ref);
      }
    }
  }

}

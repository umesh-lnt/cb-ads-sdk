package com.cloudbanter.adssdk.ad.manager.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by eric on 3/26/16.
 */
public class CbBitmap {
  public static Bitmap
  getBitmap(CbScheduleEntry entry, int imageType, Context context) {
    if (null == entry || null == entry.advert || null == entry.advert._id) {
      return null;
    }
//    return getCloudbanterImage(entry.advert.bannerImageFileName, Width, Height, defaultImage);

    // get image by
    ImageRef ref = Images.get(entry, imageType);
    if (null == ref) {
      return null;
    }
    Bitmap b = getBitmap(context, ref);
    if (null == b) { // funky default...
      b = getResourcesBitmap(context);
    }
    return b;
  }

  static Bitmap getResourcesBitmap(Context context) {
    int id = context.getResources()
            .getIdentifier("cloudbanter_background_6_0", "drawable", context.getPackageName());
    return BitmapFactory.decodeResource(context.getResources(), id);
  }

  static Bitmap getBitmap(Context context, ImageRef ref) {
    if (null == ref || null == ref.mSavedFileName) {
      return null; // not in the file cache
    }
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    BufferedInputStream is = null;
    Bitmap b = null;
    try {
      if (ImageRef.isPresent(ref)) {
        is = new BufferedInputStream(context.openFileInput(ref.getLocalFileName()));
        b = BitmapFactory.decodeStream(is);
        if (null == b) {
          ImageRef.bitmapDecodeFailed(ref);
        }
      }
      return b;
    } catch (FileNotFoundException e) {
      Log.e(ImageRef.TAG,
              String.format("missiing bitmap: %s %s", ref.getLocalFileName(), e.getMessage()));
      ImageRef.bitmapDecodeFailed(ref);
    } /* catch (IOException e1) {
    Log.d(ImageRef.TAG, "IO Exception " + e1.getMessage());// bitmap not available
    ImageRef.bitmapDecodeFailed(ref);
  } */ catch (Exception e2) {
      Log.d(ImageRef.TAG, "Unknown Exception " + e2.getMessage());
    } finally {
      if (null != is) {
        try {
          is.close();
        } catch (IOException e) {
          // do nothing
        }
      }
    }
    return null;
  }
}

package com.cloudbanter.adssdk.ad.manager.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.R;
import com.cloudbanter.adssdk.ad.model.CbAdvert;
import com.cloudbanter.adssdk.ad.service.CbDownloadService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageRef implements Parcelable {
  public static final String TAG = "ImageRef";

  public static final int IMAGE_TYPE_FULL = 1;
  public static final int IMAGE_TYPE_BANNER = 2;

  // TODO should be in configuration
  public static final String BANNER_FILENAME =
          CbAdsSdk.getApplication().getResources().getString(R.string.banner_image_name);
  public static final String FULL_FILENAME =
          CbAdsSdk.getApplication().getResources().getString(R.string.full_image_name);

  public String mAdvertiser;
  public String mImageId;
  public String mBaseFileName;             // FullImage || BannerImage
  public String mFileExt;                  // filetype extension
  public int mImageType = 0;
  public String mUrl;
  public String mSavedFileName = null;
  public boolean haveFile = false;

  public ImageRef() {
  }


  @Override
  public void writeToParcel(Parcel p, int arg1) {
    p.writeString(mAdvertiser);
    p.writeString(mImageId);
    p.writeString(mBaseFileName);
    p.writeString(mUrl);
    p.writeString(mSavedFileName);
    p.writeInt(mImageType);
  }

  private ImageRef(Parcel in) {
    mAdvertiser = in.readString();
    mImageId = in.readString();
    mBaseFileName = in.readString();
    mUrl = in.readString();
    mSavedFileName = in.readString();
    mImageType = in.readInt();
  }

  public static final Creator<ImageRef>
          CREATOR = new Creator<ImageRef>() {
    public ImageRef createFromParcel(Parcel in) {
      return new ImageRef(in);
    }

    public ImageRef[] newArray(int size) {
      return new ImageRef[size];
    }
  };

  public static boolean isPresent(ImageRef ref) {
    if (null == ref || TextUtils.isEmpty(ref.mSavedFileName)) {
      return false;
    }
    String f = ref.mSavedFileName;
    // Log.d(TAG, String.format("exists with %b without %b", new File(f).exists(), new File
    // (PATH_PREFIX+f).exists()));
    if (!ref.haveFile) {
      ref.haveFile = ((new File(f).exists()) || (new File(CbAdsSdk.FILE_PATH + f).exists()));
    }
    return (ref.haveFile);
  }

  public static String getImageUri(String advertiser, String imageId, String imageTypeName,
                                   String fnExt) {
    String fn = String.format(
            CbAdsSdk.getApplication().getResources().getString(R.string.imageRefTemplate),
            CbAdsSdk.SERVER_NAME, advertiser, imageId, imageTypeName, fnExt);
    return fn;
  }

  public static String getFileName(ImageRef img) {
    return String.format("%s_%s_%s", img.mAdvertiser, img.mImageId, img.mBaseFileName);
  }

  public static String getImageFileName(String advertiser, String imageId, String imageTypeName,
                                        String fullExt) {
    if (null == fullExt) {
      fullExt = "png";
    }
    Log.d(TAG, "Image finle name: " +
            String.format("%s_%s_%s.%s", advertiser, imageId, imageTypeName, fullExt));
    return String.format("%s_%s_%s.%s", advertiser, imageId, imageTypeName, fullExt);
  }

  // PATH + FILENAME
  public String getLocalFileName() {
    return mSavedFileName;
  }

  public ImageRef(String advertiser, String imageId, String name, String fnExt) {
    this.mAdvertiser = advertiser;
    this.mImageId = imageId;
    this.mBaseFileName = name;
    this.mSavedFileName = getFileName(this);
    this.mImageType = getImageType(name);
    this.mFileExt = fnExt;
  }

  public static int getImageType(String s) {
    int imageType = 0;
    if (s.contains(FULL_FILENAME)) {
      imageType = IMAGE_TYPE_FULL;
    } else if (s.contains(BANNER_FILENAME)) {
      imageType = IMAGE_TYPE_BANNER;
    }
    return imageType;
  }

  public static String getImageTypeName(int imageType) {
    switch (imageType) {
      case IMAGE_TYPE_FULL:
        return FULL_FILENAME;
      case IMAGE_TYPE_BANNER:
        return BANNER_FILENAME;
    }
    return null;
  }

  // TODO fix scaling?
  Bitmap getImageBitmap(int reqWidth, int reqHeight) {
    // First decode with inJustDecodeBounds=true to check dimensions
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    BitmapFactory.decodeFile(mSavedFileName, options);
    // Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false;
    try {
      return BitmapFactory.decodeStream(
              new BufferedInputStream(new FileInputStream(mSavedFileName)),
              new Rect(0, 0, reqWidth, reqHeight), options);
    } catch (IOException e1) {
      Log.e(TAG, String.format("IO or FileNotFound bitmap: %s %s", getLocalFileName(),
              e1.getMessage()));
    } catch (Exception e2) {
      Log.d(TAG, "Unknown Exception " + e2.getMessage());
    }
    return null;
  }

  // Given the bitmap size and View size calculate a bsampling size (powers of 2)
  static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
    int inSampleSize = 1; // Default subsampling size
    // See if image raw height and width is bigger than that of required view
    if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
      // bigger
      final int halfHeight = options.outHeight / 2;
      final int halfWidth = options.outWidth / 2;
      // Calculate the largest inSampleSize value that is a power of 2 and keeps both
      // height and width larger than the requested height and width.
      while ((halfHeight / inSampleSize) > reqHeight
              && (halfWidth / inSampleSize) > reqWidth) {
        inSampleSize *= 2;
      }
    }
    return inSampleSize;
  }

  @Override
  public int describeContents() {
    return 0;
  }


  public static CbAdvert newImageDetailDownload(Context context, CbAdvert advert) {
    Log.d(TAG, String.format("newImagesDetail: %s", advert.toJson()));
    ImageRef banner = newDetail(advert.advertiser, advert._id, "bannerImage", advert.bannerExt);
    if (!ImageRef.isPresent(banner)) {
      CbDownloadService.downloadImage(context, banner);
    }
    ImageRef full = newDetail(advert.advertiser, advert._id, "fullImage", advert.fullExt);
    if (!ImageRef.isPresent(full)) {
      CbDownloadService.downloadImage(context, full);
    }
    return advert;
  }

  // created from local file.
// new detail from components
  public static ImageRef newDetail(String advertiser, String imageId, String name, String fnExt) {
    final ImageRef d = new ImageRef(advertiser, imageId, name, fnExt);
    d.mUrl = ImageRef.getImageUri(advertiser, imageId, name, fnExt);
    d.mSavedFileName = ImageRef.getImageFileName(advertiser, imageId, name, fnExt);
    d.mImageType = ImageRef.getImageType(name);  // { banner, full }
    Images.put(d.mSavedFileName, d);
    return d;
  }

  @Deprecated
  public static ImageRef newDetail(String advertiser, String imageId, String name) {
    return newDetail(advertiser, imageId, name, "png");
  }

  // TODO fix path name conventions fragile...
  public static ImageRef newDetailFromFileName(String fn) {
    Log.d(TAG, "Filename: " + fn);
    {
      String ext = "png";
      if (fn.contains("gif")) {
        ext = "gif";
      }
      if (fn.contains("jpg")) {
        ext = "jpg";
      }

      //   "/data/data...  android absolute path not needed.  if (pathComp.length < 5) return null;
      String[] pathComp = fn.split("/");
      String[] comp = pathComp[0].split(
              "_"); // pos 4 of /data/data/com.cloudbanter
      // .mms/files/advertiser_advert-nnnnnnn-B/Bannerimage.jpg

      int len = comp.length;
      String[] imagetype = comp[len - 1].split("\\.");
      ImageRef ref = null;
      if (len >= 3) {
        ref = newDetail(comp[len - 3], comp[len - 2], imagetype[0], ext);
      }

      return ref;
    }
  }

  public static void bitmapDecodeFailed(ImageRef ref) {
    // rm file
    // File file = new File(PathPrefix+FileName);
    // boolean deleted = file.delete();

    ref.haveFile = false;
    // redownload the file
    // CbDownloadService.downloadImage(context, ref);
  }

}


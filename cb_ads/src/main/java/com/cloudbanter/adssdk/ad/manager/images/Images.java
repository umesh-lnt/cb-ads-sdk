package com.cloudbanter.adssdk.ad.manager.images;


import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;

import java.util.TreeMap;

// TODO most methods should be static. 
public class Images {

  private static final String TAG = "ImagesCollection";

  private static TreeMap<String, ImageRef> images = new TreeMap<String, ImageRef>();

  final static String SPLIT_IMAGE_PREFIX = "splitImage_";
  final static String SPLIT_IMAGE_DETAILS_PREFIX = "splitImage_details_";

  public static boolean contains(String key) {
    return images.containsKey(key);
  }

  public static ImageRef get(String fileName) {
    return images.get(fileName);
  }

  public static void put(String key, ImageRef ref) {
    images.put(key, ref);
  }

  public static ImageRef get(CbScheduleEntry entry, int type) {
    String fn = ImageRef.getImageFileName(entry.advert.advertiser, entry.advert._id,
            ImageRef.getImageTypeName(type),
            type == ImageRef.IMAGE_TYPE_BANNER ? entry.advert.bannerExt : entry.advert.fullExt);
    return images.get(fn);
  }

  public static boolean isPresent(String fn) {
    return ImageRef.isPresent(get(fn));
  }
}

package com.cloudbanter.adssdk.ad.manager.images;

import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by eric on 3/26/16.
 */
// TODO subclass of ImageRef... f
// for tiling
public class SplitDetail {

  public final int width, Width, height, Height, size;
  final TreeSet<String> present = new TreeSet<String>();


  SplitDetail(String advertiser, String imageId, String pname,
              int pwidth, int pWidth, int pheight, int pHeight, int pSize) {
    //  TODO super(advertiser, imageId, pname);
    width = pwidth;
    Width = pWidth;
    height = pheight;
    Height = pHeight;
    size = pSize;
  }

  public void present(int x, int y) {
    present.add("" + x + "_" + y);
  }

  // TODO @Override
  public boolean isPresent() {
    // TODO if (super.isPresent()) {
    final int required = Width * Height;
    final int found = present.size();
    if (found == required) {
      return true;
    }
    // TODO }
    return false;
  }

  public static SplitDetail newSplitDetail(String url, String details) {
    final String[] components = url.split("/");
    int cLen = components.length;
    if (cLen > 4) {
      return newSplitDetail(
              components[cLen - 3],
              components[cLen - 2],
              components[cLen - 1],
              details);
    }
    return null;
  }

  // parse details of a split image
  static SplitDetail newSplitDetail(String advertiser, String imageId, String name,
                                    String Details) {

    final Stack<Stack<String>> details = splitTwice(Details); // Parse image details
    final TreeMap<String, String> keys = stackToTree(details); // Convert image details into a
    // tree
    final int width = Integer.parseInt(keys.get("width")); // Pixel width
    final int Width = Integer.parseInt(keys.get("Width")); // Block width
    final int height = Integer.parseInt(keys.get("height")); // Pixel height
    final int Height = Integer.parseInt(keys.get("Height")); // Block height
    final int size = Integer.parseInt(keys.get("size")); // Block height

    return new SplitDetail(advertiser, imageId, name, width, Width, height, Height, size);
  }

  static Stack<Stack<String>> splitTwice(String s) {
    final Stack<Stack<String>> S = new Stack<Stack<String>>();

    for (String l : s.split("\\s*\\n+\\s*")) // Split into lines
    {
      final Stack<String> W = new Stack<String>();
      for (String w : l.split(" +")) // Split on spaces
      {
        if (!w.trim().equals("")) {
          W.push(w);
        }
      }
      if (W.size() > 0) {
        S.push(W); // Ignore blank lines
      }
    }
    return S;
  }

  static TreeMap<String, String> stackToTree(Stack<Stack<String>> ssS) {
    final TreeMap<String, String> t = new TreeMap<String, String>();

    for (Stack<String> s : ssS) // Each parsed line
    {
      if (s.size() != 2) {
        continue; // Ignore anything except key=>value pairs
      }
      final String k = s.elementAt(0); // Key
      final String v = s.elementAt(1); // Value
      t.put(k, v); // Add to tree
    }
    return t;
  }
  /*
  // created from local file...
  public ImageRef.SplitDetail newSplitDetail(String url, String details) {
    final ImageRef.SplitDetail sd = ImageRef.newSplitDetail(url, details);
    sd.mUrl = null; // String.format(format, args)
    images.put(sd.mImageId, sd);
    return sd;
  }

  public ImageRef.SplitDetail newSplitDetail(String advertiser, String imageId, String name,
  String details) {
    final ImageRef.SplitDetail sd = ImageRef.newSplitDetail(advertiser, imageId, name, details);
    images.put(imageId, sd);
    return sd;
  }

  private void manageSplitDetail() {

  // TODO manage/assemble split files...
  // if (=="details.txt") ...
  if (IMAGE_PREFIX.equalsIgnoreCase(comp[0])) {
    return image = newDetail(fn.substring(IMAGE_PREFIX.length()));
  } else if (SPLIT_IMAGE_PREFIX.equalsIgnoreCase(comp[0])) {
    return image = newSplitDetail(fn.substring(SPLIT_IMAGE_PREFIX.length()),
        "COMBINE_comp_for_details");
  } else if (SPLIT_IMAGE_DETAILS_PREFIX.equalsIgnoreCase(comp[0])) {
    // DetailsRef details = parseDetailsFile(fn);
    // add to details struct
  }
  */
/*
  // TODO combine splits
  // from downloadService
  void downloadSplitImage(String name) {
    final String folder = bucketUrl + name + "/"; // Folder name containing splits
    final byte[] bytes = httpGetFile(folder + "details.txt"); // Read image description url as
                                                              // bytes
    final String Details = new String(bytes); // Get image details

    final CbImageManager im = CbImageManager.mImageManager; // Address display manager
    final ImageRef.SplitDetail d = // Parse details of split image
    // ImageRef.newSplitDetail(name, Details);

    Log.d(TAG, "Download split image " + name); // Progress

    for (int y = 1; y <= d.Height; ++y) { // Download each block in the image
      for (int x = 1; x <= d.Width; ++x) {
        final String pos = "" + y + "_" + x; // Position of block
        final String url = folder + pos; // Url
        final String file = splitImageFilePrefix + name + "_part_" + pos; // Block name
        if (forceDownloads || !im.mImages.contains(file)) { // Download url if file not already
          // present on file system or we are being
          // forceful
          final byte[] i = httpGetFile(url); // Download url
          if (i != null && i.length > 0) {
            writeLocalFile(file, i); // Save block
            im.mImages.files.add(file); // Show file as present
            d.present(x, y); // Mark block as loaded
          }
        }
      }
    }

    writeLocalFile("splitImage_details_" + name, bytes);
  }
*/

}

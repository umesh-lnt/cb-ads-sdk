/*------------------------------------------------------------------------------
Display Manager - display images downloaded from S3 to the user
Philip R Brenan, philip at toptal dot com, © CloudBanter, 2015/07/30 16:38:244
------------------------------------------------------------------------------*/
package com.cloudbanter.adssdk.ad.manager.images.x;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.cloudbanter.adssdk.R;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

/*------------------------------------------------------------------------------
Image Manager
------------------------------------------------------------------------------*/
public class DisplayManagerActivity extends Activity {
  final static String nl = "\n";
  final static String imageFilePrefix = "image_";
          // Prefix to identify image files
  final static String splitImageFilePrefix = "splitImage_";
          // Prefix to identify split image files
  final static String splitImageDetailsPrefix = "splitImage_details_";
          // Prefix to identify split image files details file
  final TreeSet<String> files = new TreeSet<String>();
          // Set of local files
  final Images images = new Images();
          // Currently available images
  static DisplayManagerActivity displayManager = null;
          // Makes us visible to DownloadService
  String welcomeMessage = "";
          // Welcome message in the absence of any images to display
  int lastShownImage = 0;
          // Index of last image shown

  /*------------------------------------------------------------------------------
  Create App
  ------------------------------------------------------------------------------*/
  public void onCreate(Bundle save) {
    super.onCreate(save);
    welcomeMessage = getResources().getString(
            R.string.app_label);         // Welcome message in the absence of any images to display
    displayManager = this;                                                      // Addressability
    parseFiles();                                                               // Parse the
    // files already downloaded to see what images we already have on the device
    startDownLoadService();                                                     // Download
    // service polls for new files
    setContentView(
            new Display());                                              // Show images to prove
    // we have downloaded then successfully
  }

  public void onStart() {
    super.onStart();
  }

  public void onResume() {
    super.onResume();
  }

  public void onPause() {
    super.onPause();
  }

  public void onStop() {
    super.onStop();
  }

  public void onDestroy() {
    super.onDestroy();
  }

  /*------------------------------------------------------------------------------
  Start download service
  ------------------------------------------------------------------------------*/
  void startDownLoadService() {
    final Intent intent = new Intent(this, DownLoadService.class);
    try {
      startService(intent);
      say("Download service start requested");
    } catch (Exception e) {
      say("Cannot start the download service, exception " + e);
    }
  }

  /*------------------------------------------------------------------------------
  Display the images
  ------------------------------------------------------------------------------*/
  class Display extends View {
    final Paint paint = new Paint();
            // Paint for non text

    Display()                                                                   // Create display
    {
      super(DisplayManagerActivity.this);
    }

    public void onDraw(Canvas c)                                                // Draw image
    {
      final Images.Detail d =
              images.chooseImage();                             // Choose next image to draw
      if (d == null) {
        drawNoFiles(c);
      } else {
        d.draw(this, c);                      // Draw welcome or image
      }
    }

    public void drawNoFiles(Canvas c)                                           // Draw image
    {
      final Paint p = paint;                                                    // Clear screen

      p.setColor(Color.BLACK);
      p.setStyle(Paint.Style.FILL);
      c.drawRect(0f, 0f, c.getWidth(), c.getHeight(), p);                       // Clear

      p.setColor(Color.RED);
      p.setStyle(Paint.Style.FILL);
      p.setTextSize(50);
      p.setShadowLayer(1, 10, 10, 0xff000080);

      c.drawText(welcomeMessage, 0, 100,
              p);                                    // Display a message in absence of images to
      // show
      postInvalidateDelayed(
              2000);                                              // Keep the redraw cycle going
      // quickly hoping for an update
    }

    void shortWait() {
      postInvalidateDelayed(1000);
    }                            // Keep the redraw cycle going quickly hoping for an update

    void longWait() {
      postInvalidateDelayed(10000);
    }                            // Keep the redraw cycle going slowly enough to see what we have
  } // Display

  /*------------------------------------------------------------------------------
  Details of the images available
  ------------------------------------------------------------------------------*/
  class Images                                                                  // All images
  {
    final TreeMap<String, Detail> images = new TreeMap<String, Detail>();
    final Matrix matrix = new Matrix();
    final Paint paint = new Paint();

    int countAvailableImages()                                                  // Number of
    // images available
    {
      int n = 0;
      for (String k : images.keySet()) {
        final Detail d = images.get(k);
        if (d.present()) {
          ++n;
        }
      }
      return n;
    }

    Detail chooseImage()                                                 // Choose an image from
    // those available
    {
      final int I = countAvailableImages();
      if (I == 0) {
        return null;
      }
      final int i = ++lastShownImage % I;                                       // Greater than zero
      int n = 0;
      for (String k : images.keySet())                                            // Load current
      // image details
      {
        final Detail d = images.get(k);
        if (d.present() && n++ == i) {
          return images.get(k);                      // Choose this image
        }
      }
      return null;                                                              // Should not happen
    }

    class Detail                                                                // Unsplit image
            // detail
    {
      final String name;

      Detail(String Name)                                                       // Construct
      {
        name = Name;
      }

      void draw(Display display, Canvas canvas)                                 // Draw image
      {
        final String imageFile = imageFilePrefix + name;
        say("Draw image: " + name);
        try {
          final FileInputStream imageStream = openFileInput(imageFile);         // Input stream
          final Bitmap bmp = BitmapFactory.decodeStream(imageStream, null, null); // Decode stream
          if (bmp != null) {
            canvas.drawBitmap(bmp, null,                                        // Show image
                    new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), new Paint());
            display.longWait();
          } else {
            err("Null bitmap read from file " + imageFile);                       // Bitmap error
            display.shortWait();
          }
        } catch (FileNotFoundException e) {
          err("Cannot load image in file " + imageFile + " because " + e);
          display.shortWait();
        }
      }

      boolean present() {
        return true;
      }                                          // Always present as just one component
    } // Detail

    Detail newDetail(
            String name)                                               // Non split image detail
    {
      final Detail d = new Detail(name);
      images.put(name, d);
      return d;
    }

    class SplitDetail
            extends Detail                                            // Split image detail
    {
      final int width, Width, height, Height, size;
              // Pixel width, block width pixel height, block height, size of blocks
      final TreeSet<String> present = new TreeSet<String>();
              // Shows whether a block is present

      SplitDetail(String pname,                                                 // Constructor
                  int pwidth, int pWidth, int pheight, int pHeight, int pSize) {
        super(pname);
        width = pwidth;
        Width = pWidth;
        height = pheight;
        Height = pHeight;
        size = pSize;
      }

      void present(int x,
                   int y)                                                // Mark a block as
      // downloaded and present
      {
        present.add("" + x + "_" + y);
      }

      boolean present()                                                         // Check image is
      // fully present ior explain why not
      {
        final int required = Width * Height, found =
                present.size();              // Number of blocks required, number present
        if (found == required) {
          return true;                                     // Only present if all components
        }
        // downloaded
        say("Missing blocks for image " + name + " required=" + required + " found=" + found +
                " present= " + present);
        return false;                                                           // Some parts
        // still missing
      }

      void draw(Display display, Canvas canvas)                                 // Draw split image
      {
        say("Draw split image: " + name);
        try {
          for (int y = 1; y <= Height;
               ++y)                                    // Draw each block in the image
          {
            for (int x = 1; x <= Width; ++x) {
              draw(display, canvas, y, x);
            }
          }
        } catch (Exception e) {
          display.shortWait();
          return;
        }
        display.longWait();
      }

      void draw(Display display, Canvas canvas, int Y,
                int X)                   // Draw split image block by block
              throws
              Exception                                                        // Failed for some
              // unspecified reason
      {
        final String pos =
                "" + Y + "_" + X;                                          // Position of block
        final String imageFile = splitImageFilePrefix + name + "_part_" + pos;        // Image file
        final int x = size * (X - 1), y =
                size * (Y - 1);                          // Display position
        try {
          final FileInputStream imageStream = openFileInput(imageFile);         // Input stream
          final Bitmap bmp = BitmapFactory.decodeStream(imageStream, null, null); // Decode stream
          if (bmp != null) {
            draw(display, canvas, y, x, bmp);                    // Draw bitmap
          } else {
            err("Null bitmap read from file " + imageFile);                      // Bitmap error
            throw new Exception();
          }
        } catch (FileNotFoundException e) {
          err("Cannot load image in file " + imageFile + " because " + e);
          throw new Exception();
        }
      }

      void draw(Display display, Canvas canvas, int y, int x,
                Bitmap bmp)       // Draw split image block
      {
        final float                                                             // Scaling factors
                sx = f(canvas.getWidth()) / f(width),
                sy = f(canvas.getHeight()) / f(height);
        matrix.reset();
        matrix.preScale(sx, sy);                                                // Scale
        matrix.preTranslate(x,
                y);                                              // Translate before scale
        canvas.drawBitmap(bmp, matrix,
                paint);                                  // Draw bitmap for block
      }
    } // SplitDetails

    SplitDetail newSplitDetail(String name,
                               String Details)                     // Parse details of a split inage
    {
      final Stack<Stack<String>> details =
              splitTwice(Details);                 // Parse image details
      final TreeMap<String, String> keys =
              stackToTree(details);                // Convert image details into a tree
      final int width = Integer.parseInt(keys.get("width"));                   // Pixel width
      final int Width = Integer.parseInt(keys.get("Width"));                   // Block width
      final int height = Integer.parseInt(keys.get("height"));                  // Pixel height
      final int Height = Integer.parseInt(keys.get("Height"));                  // Block height
      final int size = Integer.parseInt(keys.get("size"));                    // Block height

      final SplitDetail d = new SplitDetail(name, width, Width, height, Height, size);
      images.put(name, d);
      return d;
    }
  }

  /*------------------------------------------------------------------------------
  Parse the local file system to obtain image details
  ------------------------------------------------------------------------------*/
  void parseFiles() {
    final String u = imageFilePrefix, s = splitImageDetailsPrefix;
    for (String f : fileList())                                                    // Each local
    // file
    {
      if (f.startsWith(u))                                                  // Unsplit  image file
      {
        images.newDetail(f.substring(u.length()));
      } else if (f.startsWith(
              s))                                                  // Details of a split image
      {
        images.newSplitDetail(f.substring(s.length()), readLocalFile(f));
      }
      files.add(
              f);                                                              // Set of local files
    }
  }

  /*------------------------------------------------------------------------------
  Read a local file
  ------------------------------------------------------------------------------*/
  String readLocalFile(String file) {
    final ByteArrayOutputStream r = new ByteArrayOutputStream();
    try {
      final InputStream i = openFileInput(file);
      final byte[] b = new byte[1024];
      for (int j = 0; j < 1000; ++j) {
        int l = i.read(b, 0, b.length);
        if (l < 0) {
          break;
        }
        r.write(b, 0, l);
      }
      final String s = new String(r.toByteArray());
      say("Read " + r.size() + " bytes from file: " + file + "\n" + s);
      return s;
    } catch (Exception e) {
      err("Cannot read file " + file + " because: " + e);
    }
    return "";
  }

  /*------------------------------------------------------------------------------
  Split a string on new lines and then spaces
  ------------------------------------------------------------------------------*/
  Stack<Stack<String>> splitTwice(String s) {
    final Stack<Stack<String>> S = new Stack<Stack<String>>();

    for (String l : s.split(
            "\\s*\\n+\\s*"))                                      // Split into lines
    {
      final Stack<String> W = new Stack<String>();
      for (String w : l.split(" +"))                                              // Split on spaces
      {
        if (!w.trim().equals("")) {
          W.push(w);
        }
      }
      if (W.size() > 0) {
        S.push(W);                                              // Ignore blank lines
      }
    }
    return S;
  }

  /*------------------------------------------------------------------------------
  Convert a stack of stack of strings to a treeMap of string to string
  ------------------------------------------------------------------------------*/
  TreeMap<String, String> stackToTree(Stack<Stack<String>> S) {
    final TreeMap<String, String> t = new TreeMap<String, String>();

    for (Stack<String> s : S)                                                     // Each parsed
    // line
    {
      if (s.size() != 2) {
        continue;                                              // Ignore anything except
      }
      // key=>value pairs
      final String k = s.elementAt(0);                                          // Key
      final String v = s.elementAt(1);                                          // Value
      t.put(k, v);                                                              // Add to tree
    }
    return t;
  }

  /*------------------------------------------------------------------------------
  Utility functions
  ------------------------------------------------------------------------------*/
  long t() {
    return System.currentTimeMillis();
  }

  float f(int i) {
    return (float) i;
  }                                         // Convert to float

  void sleep() {
    sleep(1000);
  }                                             // Default sleep

  void sleep(long n) {
    try {
      Thread.sleep(n);
    } catch (InterruptedException e) {
    }
  }  // Specified sleep

  final String LogTag = "IMDisplay";
          // Write a message to the log

  void say(String s) {
    Log.w(LogTag, s);
  }

  void err(String s) {
    Log.e(LogTag, s);
  }
}

package com.cloudbanter.adssdk.ad.manager.images;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudbanter.adssdk.R;
import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;


public class ImageDisplayActivity extends Activity {

  public static final String ARG_ENTRY_ITEM = "arg_entry_item";

  ImageView imageView;
  CbScheduleEntry mEntry;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image_display);

    if (null != getIntent().getExtras() && getIntent().getExtras().containsKey(ARG_ENTRY_ITEM)) {
      mEntry = (CbScheduleEntry) getIntent().getExtras().getSerializable(ARG_ENTRY_ITEM);
      Log.d("com.cloudbanter.mms.ad.image", "Loading Image: " + mEntry._id);
    }

    imageView = (ImageView) findViewById(R.id.display_image_view);
  }

  // TODO resize
  // CbImageView... imageView.viewHeight, imageView.viewWidth,
  // default = "advert-5602e2c8e0600ff4137d70f6"));  @Override
  public void onResume() {
    super.onResume();
    Bitmap b;
    String eid = "no advert yet";
    if (null != mEntry) {
      eid = mEntry._id;
      b = CbBitmap.getBitmap(mEntry, ImageRef.IMAGE_TYPE_FULL, this);
      if (null != b) {
        eid = mEntry.advert._id;
      }
      imageView.setImageBitmap(b);
    }
    TextView tv = (TextView) findViewById(R.id.text_image_name);
    tv.setText(eid);
  }
}

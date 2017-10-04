package com.cloudbanter.adssdk.ad.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.PermissionsManager;
import com.cloudbanter.adssdk.R;
import com.cloudbanter.adssdk.ad.manager.CloudbanterCentral;
import com.cloudbanter.adssdk.ad.manager.EventAggregator;
import com.cloudbanter.adssdk.ad.manager.images.CbBitmap;
import com.cloudbanter.adssdk.ad.manager.images.ImageRef;
import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * A fragment representing a single CloudbanterAdvert detail screen. This fragment is either
 * contained in a {@link CloudbanterCentralActivity} in two-pane mode (on tablets) or a
 * {@link CloudbanterAdvertDetailActivity} on handsets.
 */
public class CloudbanterAdvertDetailFragment extends Fragment
        implements View.OnClickListener, PermissionsManager.OnRequestPermissionsCallback {
    public static final String TAG = "CbCDetailFrag";
    /**
     * The fragment argument representing the item ID that this fragment represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String EXTRA_CB_ENTRY = "extra_cb_entry";
    private static final String[] SHARE_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int SHARE_PERMISSIONS_CODE = 1;

    /**
     * The dummy content this fragment is presenting.
     */
    private CbScheduleEntry mItem;

    private ImageView imageView;
    private RelativeLayout textView;
    private GifImageView gifView;

    private ShareActionProvider mShareActionProvider;

    private IAdDetailHandlers mActivity;

    private Tracker mTracker;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
     * screen orientation changes).
     */
    public CloudbanterAdvertDetailFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivity = (IAdDetailHandlers) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IAdDetailHandlers");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        mTracker = CbAdsSdk.getDefaultTracker();

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment arguments.
            mItem = CloudbanterCentral.getItem(getArguments().getString(ARG_ITEM_ID));
        }
        if (getArguments().containsKey(EXTRA_CB_ENTRY)) {
            mItem = (CbScheduleEntry) getArguments().getParcelable(EXTRA_CB_ENTRY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cloudbanter_advert_detail, container, false);

        imageView = (ImageView) rootView.findViewById(R.id.cloudbanter_advert_detail_image);
        imageView.setOnClickListener(this);

        gifView = (GifImageView) rootView.findViewById(R.id.advert_detail_gif_image);
        gifView.setOnClickListener(this);


        textView = (RelativeLayout) rootView.findViewById(R.id.cloudbanter_advert_detail_text);

        // Show the dummy content as text in a TextView.
        if (null != mItem) {
            Bitmap b = null;
            GifDrawable gif = null;
            if (isGif(mItem) && null != (gif = getGifDrawable(mItem))) {
                Log.d(TAG, "Gif view!");
                gifView.setImageDrawable(gif);
                imageView.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                gifView.setVisibility(View.VISIBLE);
            } else if (null != (b = CbBitmap.getBitmap(mItem, ImageRef.IMAGE_TYPE_FULL, getActivity()))) {
                imageView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                gifView.setVisibility(View.GONE);
                imageView.setImageBitmap(b);
            } else {
                imageView.setVisibility(View.GONE);
                gifView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                ((TextView) rootView.findViewById(R.id.cloudbanter_advert_detail_header))
                        .setText(mItem.advert.name);
                ((TextView) rootView.findViewById(R.id.cloudbanter_advert_detail_body))
                        .setText(mItem.advert.advertText);
            }
        }
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_full_advert, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        if (item != null) {
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            if (mShareActionProvider != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "Thought you might like this offer - " + mItem.advert.url);
                mShareActionProvider.setShareIntent(shareIntent);
            } else {
                Log.e(TAG, "Null share action provider!");
            }
        } else {
            Log.e(TAG, "Null menu item");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Log.d(TAG, String.format("id: %x  del: %x", id, R.id.menu_delete_ad));
    /*switch (id) {
      case R.id.menu_delete_ad:
        mActivity.handleDeleteAd(mItem._id);
        return true;

//    case R.id.menu_remove_offer:
//      mActivity.handleRemoveOffer(mItem._id);
//      return true;
      case R.id.menu_more_ads:
      case R.id.menu_less_ads:
      case R.id.menu_more_biz:
        return true;

      case R.id.menu_item_share:
        share();
        return true;


      default:
        return super.onOptionsItemSelected(item);
    }*/
        if (id == R.id.menu_delete_ad) {
            mActivity.handleDeleteAd(mItem._id);
            return true;
        } else if (id == R.id.menu_more_ads || id == R.id.menu_less_ads ||
                id == R.id.menu_more_biz) {
            return true;
        } else if (id == R.id.menu_item_share) {
            share();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void share() {
        Log.d(TAG, "Share. Item: " + mItem.advert.url);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        if (mItem.advert.url != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, mItem.advert.url);
            getActivity().startActivity(
                    Intent.createChooser(shareIntent, getResources().getText(R.string.share_button)));
        } else {
            PermissionsManager.getInstance()
                    .requestPermissions(this, SHARE_PERMISSIONS, SHARE_PERMISSIONS_CODE, this);
        }
    }

    private void sharePhysicalFile() {
        Log.d(TAG, "No url to share, sharing image instead");
//        shareIntent.putExtra(Intent.EXTRA_TEXT, "- No url -");
        Log.d(TAG, "Full image path: " + mItem.advert.fullImageFileName);
        final String dir = Environment.getExternalStorageDirectory() + "/" +
                Environment.DIRECTORY_DOWNLOADS + "/";
        copyFile(
                new File(getActivity().getFilesDir().getAbsolutePath() + File.separator +
                        mItem.advert.fullImageFileName),
                new File(dir + mItem.advert.fullImageFileName)
        );
        MediaScannerConnection.scanFile(
                getActivity(),
                new String[]{dir + mItem.advert.fullImageFileName},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.d(TAG, "Adding as extra stream: " + uri.getPath());
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("image/*");

                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(shareIntent,
                                getResources().getText(R.string.share_button)));
                    }
                }
        );
    }

    public void copyFile(File src, File dst) {
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "", e);
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }

    }

    @Override
    public void onClick(View v) {
        if (null != mItem.advert && mItem.hasUrl()) {
            mItem.onFullAdClick();
            Intent intent = new Intent(getActivity(), CbWebBrowserActivity.class);
            intent.putExtra(CbWebBrowserActivity.EXTRA_URL, mItem.advert.url); // mItem.advert.url);
            getActivity().startActivity(intent);
            EventAggregator.getInstance().addWebClick(mItem._id);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsManager.getInstance()
                .onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        if (requestCode == SHARE_PERMISSIONS_CODE) {
            sharePhysicalFile();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode) {
        if (requestCode == SHARE_PERMISSIONS_CODE) {
            Snackbar.make(imageView, R.string.external_storage_permissions_needed, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    public interface IAdDetailHandlers {
        public void handleDeleteAd(String id);

        public void handleRemoveOffer(String id);

        public void sendEvent(String id, String eventType);
    }

    protected boolean isGif(CbScheduleEntry entry) {
        if (null != entry.advert
                && null != entry.advert
                && ((null != entry.advert.fullExt
                && entry.advert.fullExt.equalsIgnoreCase("gif"))
                || entry.advert.fullImage.contains(".gif"))
                ) {
            return true;
        }
        return false;
    }

    protected GifDrawable getGifDrawable(CbScheduleEntry entry) {
        try {
            return new GifDrawable(getActivity().getFilesDir().getAbsolutePath() + File.separator +
                    mItem.advert.fullImageFileName);
        } catch (IOException e) {
            ; // null return...
        }
        return null;
    }

}

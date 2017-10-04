package com.cloudbanter.adssdk.ad.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cloudbanter.adssdk.R;
import com.cloudbanter.adssdk.ad.manager.CloudbanterCentral;
import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;
import com.google.android.gms.analytics.HitBuilders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An activity representing a list of CloudbanterAdverts. This activity has different presentations
 * for handset and tablet-size devices. On handsets, the activity presents a list of items, which
 * when touched, lead to a {@link CloudbanterAdvertDetailActivity} representing item details. On
 * tablets, the activity presents the list of items and item details side-by-side using two
 * vertical
 * panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link CloudbanterCentralListFragment} and the item details (if present) is a
 * {@link CloudbanterAdvertDetailFragment}.
 * <p/>
 * This activity also implements the required {@link CloudbanterCentralListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class CloudbanterCentralActivity<CbEvent>
        extends ACbListActivity<CbEvent> // ActionBarListActivity
        implements CloudbanterCentralListFragment.Callbacks {

  private static final String TAG = CloudbanterCentralActivity.class.getSimpleName();

  public static final String EXTRA_ITEM_ID = "extra_item_id";
  public static final String ARG_ENTRY_ITEM = "arg_entry_item";

  public static final int REMOVE_OFFER_FROM_CLOUDBANTER = 1097;
  public static final int REMOVE_ADVERT_FROM_ROTATION = 1098;
  /**
   * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
   */
  private boolean mTwoPane;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cloudbanter_advert_list);
    // Show the Up button in the action bar.
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // if is empty - check for saved cbcentral
    Log.d(TAG, "Checking if cb central is empty");
    if (CloudbanterCentral.isEmpty()) {
      Log.d(TAG, "CbCentral was empty, restoring");
      CloudbanterCentral.restoreCbCentral();
    } else {
      Log.d(TAG, "CbCentral was not empty");
    }

    if (findViewById(R.id.cloudbanteradvert_detail_container) != null) {
      // The detail container view will be present only in the
      // large-screen layouts (res/values-large and
      // res/values-sw600dp). If this view is present, then the
      // activity should be in two-pane mode.
      mTwoPane = true;

      // In two-pane mode, list items should be given the
      // 'activated' state when touched.
      ((CloudbanterCentralListFragment) getSupportFragmentManager()
              .findFragmentById(R.id.cloudbanteradvert_list))
              .setActivateOnItemClick(true);
    }


    // if there is an item that is selected, go to the detail...
    if (null != getIntent().getExtras() && getIntent().getExtras().containsKey(EXTRA_ITEM_ID)) {
      String id = getIntent().getExtras().getString(EXTRA_ITEM_ID);
      Intent intentWithoutId = getIntent();
      intentWithoutId.removeExtra(EXTRA_ITEM_ID);
      setIntent(intentWithoutId);
      onItemSelected(id);
    }

    // if entered with new entry
    if (null != getIntent().getExtras() && getIntent().getExtras().containsKey(ARG_ENTRY_ITEM)) {
      CbScheduleEntry mEntry =
              (CbScheduleEntry) getIntent().getExtras().getSerializable(ARG_ENTRY_ITEM);
      Log.d(TAG, "CloudbanterCentral Selected Entry: " + mEntry._id);
      onItemSelected(mEntry._id);
    }


  }


  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    //Do nothing
    Log.d(TAG, "Configuration change");
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    MenuInflater inflater = this.getMenuInflater();
    menu.clear();

    inflater.inflate(R.menu.menu_cloudbanter_advert_list, menu);

    super.onPrepareOptionsMenu(menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    /*switch (id) {
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;

      case R.id.menu_item_preferences:
        handlePreferences();
        return true;

      case R.id.menu_item_about:
        handleAbout();
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }*/
    if(id == android.R.id.home){
      NavUtils.navigateUpFromSameTask(this);
      return true;
    }
    else if(id == R.id.menu_item_preferences){
      handlePreferences();
      return true;
    }
    else if(id == R.id.menu_item_about){
      handleAbout();
      return true;
    }
    else{
      return super.onOptionsItemSelected(item);
    }
  }

  /**
   * Callback method from {@link CloudbanterCentralListFragment.Callbacks} indicating that the
   * item
   * with the given ID was selected.
   */
  @Override
  public void onItemSelected(String id) {
    if (mTwoPane) {
      // In two-pane mode, show the detail view in this activity by
      // adding or replacing the detail fragment using a
      // fragment transaction.
      Bundle arguments = new Bundle();
      arguments.putString(CloudbanterAdvertDetailFragment.ARG_ITEM_ID, id);
      CloudbanterAdvertDetailFragment fragment = new CloudbanterAdvertDetailFragment();
      fragment.setArguments(arguments);
      getSupportFragmentManager().beginTransaction()
              .replace(R.id.cloudbanteradvert_detail_container, fragment)
              .commit();

    } else {
      // In single-pane mode, simply start the detail activity
      // for the selected item ID.
      Intent detailIntent = new Intent(this, CloudbanterAdvertDetailActivity.class);
      detailIntent.putExtra(CloudbanterAdvertDetailFragment.ARG_ITEM_ID, id);
      startActivityForResult(detailIntent, VIEW_DETAILS_REQUEST);
    }
  }

  public static final int VIEW_DETAILS_REQUEST = 3001;

  private void handlePreferences() {
//    Intent intent = new Intent(this, CloudbanterPreferencesWizardActivity.class);
//    startActivity(intent);
    Intent intent = new Intent("cb.adslibrary.open_preferences_wizard_activity");
    sendBroadcast(intent);
  }

  private void handleSettings() {
//    Intent intent = new Intent(this, CloudbanterSettingsActivity.class);
//    startActivity(intent);
    Intent intent = new Intent("cb.adslibrary.open_settings_activity");
    sendBroadcast(intent);
  }

  private void handleAbout() {
//    Intent intent = new Intent(this, CloudbanterAboutActivity.class);
//    startActivity(intent);
    Intent intent = new Intent("cb.adslibrary.open_about_activity");
    sendBroadcast(intent);
  }

  @Override
  public void onListItemClick(ListView listView, View view, int position, long id) {
    CloudbanterCentralListFragment.EntryHolder entryHolder =
            (CloudbanterCentralListFragment.EntryHolder) view.getTag();
    if (entryHolder.adUrl == null) {
      onItemSelected(CloudbanterCentral.getItem(position)._id);
    } else {
      Log.d(TAG, "Starting web view for external ad");
      Intent intent = new Intent(this, CbWebBrowserActivity.class);
      intent.putExtra(CbWebBrowserActivity.EXTRA_URL, entryHolder.adUrl);
      startActivity(intent);
    }
  }

  @Override
  protected boolean onListItemLongClick(AdapterView lv, View v, int position, long id) {
    final CloudbanterCentralListFragment.EntryHolder entryHolder =
            (CloudbanterCentralListFragment.EntryHolder) v.getTag();
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
    dialogBuilder.setTitle("");
    String[] options = {
            getResources().getString(R.string.share_button),
            getResources().getString(R.string.delete)
    };
    dialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Clicked: " + which);
                switch (which) {
                  case 0:
                    Log.d(TAG, "Sharing.");
                    if (entryHolder.adUrl == null) {
                      //TODO handle cb ad without url
                      Log.d(TAG, "No url to share, sharing image instead");
                      final String dir = Environment.getExternalStorageDirectory() + "/" +
                              Environment.DIRECTORY_DOWNLOADS + "/";
                      entryHolder.adImageView.buildDrawingCache();
                      saveBitmapToFile(
                              entryHolder.adImageView.getDrawingCache(),
                              new File(dir + "shareItem.png")
                      );
                      MediaScannerConnection.scanFile(
                              CloudbanterCentralActivity.this,
                              new String[]{dir + "shareItem.png"},
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
                    } else {

                      Intent shareIntent = new Intent(Intent.ACTION_SEND);
                      shareIntent.setType("text/plain");
                      if (entryHolder.adUrl.contains("market://")) {
                        shareIntent.putExtra(Intent.EXTRA_TEXT, entryHolder.adUrl.replace
                                ("market://",
                                        "https://play.google.com/store/apps/"));
                      } else {
                        shareIntent.putExtra(Intent.EXTRA_TEXT, entryHolder.adUrl);
                      }
                      startActivity(Intent.createChooser(shareIntent,
                              getResources().getText(R.string.share_button)));
                    }
                    break;
                  case 1:
                    Log.d(TAG, "Deleting");
                    ((CloudbanterCentralListFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.cloudbanteradvert_list)).remove(entryHolder.id);
                    break;
                  default:
                    Log.d(TAG, "Unknown selection");
                    break;
                }
              }
            }
    );
    dialogBuilder.create().show();

    return true;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d(TAG, "Result: " + resultCode);
    if (requestCode == CloudbanterCentralActivity.VIEW_DETAILS_REQUEST) {
      switch (resultCode) {
        case RESULT_OK:
        case REMOVE_OFFER_FROM_CLOUDBANTER:
        case REMOVE_ADVERT_FROM_ROTATION:
          ((CloudbanterCentralListFragment) getSupportFragmentManager()
                  .findFragmentById(R.id.cloudbanteradvert_list))
                  .updateCloudbanterCentralList();
          break;
        case RESULT_CANCELED:
          finish();
          break;
      }
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.i(TAG, "Setting screen name: " + TAG + " and sending a hit");

    mTracker.setScreenName(TAG);
    mTracker.send(new HitBuilders.ScreenViewBuilder().build());
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

  private File saveBitmapToFile(Bitmap bitmap, File destination) {
    if (bitmap != null) {
      File outputFile = destination;
      if (outputFile.exists()) {
        outputFile.delete();
      }
      FileOutputStream fos = null;
      try {
        fos = new FileOutputStream(outputFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        return outputFile;
      } catch (FileNotFoundException e) {
        Log.e(TAG, "", e);
      } catch (IOException e) {
        Log.e(TAG, "", e);
      }
    }
    return null;
  }
}

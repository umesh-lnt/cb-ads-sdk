package com.cloudbanter.adssdk.ad.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cloudbanter.adssdk.R;
import com.cloudbanter.adssdk.ad.manager.AdvertManager;
import com.cloudbanter.adssdk.ad.manager.CloudbanterCentral;
import com.google.android.gms.analytics.HitBuilders;

/**
 * An activity representing a single CloudbanterAdvert detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link CloudbanterCentralActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link CloudbanterAdvertDetailFragment}.
 */
public class CloudbanterAdvertDetailActivity extends ACbActivity
        implements CloudbanterAdvertDetailFragment.IAdDetailHandlers {

  private static final String TAG = CloudbanterAdvertDetailActivity.class.getSimpleName();

  private String entryId = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cloudbanter_advert_detail);

    // Show the Up button in the action bar.
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // savedInstanceState is non-null when there is fragment state
    // saved from previous configurations of this activity
    // (e.g. when rotating the screen from portrait to landscape).
    // In this case, the fragment will automatically be re-added
    // to its container so we don't need to manually add it.
    // For more information, see the Fragments API guide at:
    //
    // http://developer.android.com/guide/components/fragments.html
    //
    if (savedInstanceState == null) {
      // Create the detail fragment and add it to the activity
      // using a fragment transaction.
      entryId = getIntent().getStringExtra(CloudbanterAdvertDetailFragment.ARG_ITEM_ID);
      Bundle arguments = new Bundle();
      arguments.putString(CloudbanterAdvertDetailFragment.ARG_ITEM_ID, entryId);
      CloudbanterAdvertDetailFragment fragment = new CloudbanterAdvertDetailFragment();
      fragment.setArguments(arguments);
      getSupportFragmentManager().beginTransaction()
              .add(R.id.cloudbanteradvert_detail_container, fragment)
              .commit();
    }
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    MenuInflater inflater = this.getMenuInflater();
    menu.clear();
    inflater.inflate(R.menu.menu_full_advert, menu);
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.i(TAG, "Setting screen name: " + TAG + " and sending a hit");

    mTracker.setScreenName(TAG);
    mTracker.send(new HitBuilders.ScreenViewBuilder().build());
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    // For more details, see the Navigation pattern on Android Design:
    // http://developer.android.com/design/patterns/navigation.html#up-vs-back
    // NavUtils.navigateUpTo(this, new Intent(this, CloudbanterCentralActivity.class));
    /*switch (id) {
      case android.R.id.home:
        Intent result = getIntent();
        setResult(RESULT_OK, result);
        finish();
        return true;

      case R.id.menu_delete_ad:
        handleDeleteAd(entryId);
        return true;

//      case R.id.menu_remove_offer:
//        handleRemoveOffer(entryId);
//        return true;

      case R.id.menu_more_ads:
      case R.id.menu_less_ads:
      case R.id.menu_more_biz:
        return true;

      default:
    }*/
    if( id== android.R.id.home){
      Intent result = getIntent();
      setResult(RESULT_OK, result);
      finish();
      return true;
    }
    else if(id == R.id.menu_delete_ad){
      handleDeleteAd(entryId);
      return true;
    }
    else if(id == R.id.menu_more_ads || id == R.id.menu_less_ads || id == R.id.menu_more_biz){
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public void handleDeleteAd(String id) {
    if (null != id) {
      CloudbanterCentral.removeItem(id);
      AdvertManager.removeItem(id);
    }
    Intent result = getIntent();
    setResult(CloudbanterCentralActivity.REMOVE_ADVERT_FROM_ROTATION, result);
    finish();
  }

  public void handleRemoveOffer(String id) {
    if (null != id) {
      CloudbanterCentral.removeItem(id);
    }
    Intent result = getIntent();
    setResult(CloudbanterCentralActivity.REMOVE_OFFER_FROM_CLOUDBANTER, result);
    finish();
  }

  public void sendEvent(String id, String eventType) {
//      String deviceId = null;
//      Intent intent = CbRestService.getSendEventIntent(this,
//          (CallbackHandler<CbEvent>) mHandler,
//          new CbEvent(deviceId, eventType, null, id));
//      startService(intent);
  }
}

package com.cloudbanter.adssdk.ad.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cloudbanter.adssdk.R;
import com.cloudbanter.adssdk.ad.manager.AdvertManager;
import com.cloudbanter.adssdk.ad.manager.BannerGrabber;
import com.cloudbanter.adssdk.ad.manager.CloudbanterCentral;
import com.cloudbanter.adssdk.ad.manager.ExternalBannerGrab;
import com.cloudbanter.adssdk.ad.manager.images.CbBitmap;
import com.cloudbanter.adssdk.ad.manager.images.ImageRef;
import com.cloudbanter.adssdk.ad.model.CbScheduleEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * A list fragment representing a list of CloudbanterAdverts. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link CloudbanterAdvertDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class CloudbanterCentralListFragment extends ListFragment {
  public static final String TAG = CloudbanterCentralListFragment.class.getSimpleName();

  private EntryAdapter mAdapter;
  /**
   * The serialization (saved instance state) Bundle key representing the
   * activated item position. Only used on tablets.
   */
  private static final String STATE_ACTIVATED_POSITION = "activated_position";

  /**
   * The fragment's current callback object, which is notified of list item
   * clicks.
   */
  private Callbacks mCallbacks = sDummyCallbacks;

  /**
   * The current activated item position. Only used on tablets.
   */
  private int mActivatedPosition = ListView.INVALID_POSITION;

  /**
   * A callback interface that all activities containing this fragment must
   * implement. This mechanism allows activities to be notified of item
   * selections.
   */
  public interface Callbacks {
    /**
     * Callback for when an item has been selected.
     */
    public void onItemSelected(String id);
  }

  /**
   * A dummy implementation of the {@link Callbacks} interface that does
   * nothing. Used only when this fragment is not attached to an activity.
   */
  private static Callbacks sDummyCallbacks = new Callbacks() {
    @Override
    public void onItemSelected(String id) {
    }
  };

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public CloudbanterCentralListFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // TODO: replace with a real list adapter.
    mAdapter = new EntryAdapter(
            getActivity(),
            R.layout.cloudbanter_advert_list_item,
            R.id.advert_list_image_view,
            CloudbanterCentral.getItems(),
            BannerGrabber.getInstance().getGrabbedEntries()
    );
    setListAdapter(mAdapter);
  }

  @Override
  public void onStart() {
    super.onStart();
    View listView = getListView();
    registerForContextMenu(listView);
    getListView().setEmptyView(emptyCbcView());
  }

  @Override
  public void onResume() {
    super.onResume();
    // mAdapter.updateItems(CloudbanterCentral.getItems());
    updateCloudbanterCentralList();
  }

  class EntryAdapter extends BaseAdapter {

    Context context;
    int layoutResourceId;

    private List<CbScheduleEntry> scheduleEntryList;
    private List<ExternalBannerGrab> grabbedBannerList;


    public EntryAdapter(
            Context context,
            int resource,
            int textViewResourceId,
            ArrayList<CbScheduleEntry> entries,
            List<ExternalBannerGrab> externalEntries) {
      scheduleEntryList = entries;
      grabbedBannerList = externalEntries;
      this.context = context;
      this.layoutResourceId = resource;
    }

    @Override
    public int getCount() {
      return scheduleEntryList.size() + grabbedBannerList.size();
    }

    @Override
    public Object getItem(int position) {
      if (position < scheduleEntryList.size()) {
        return scheduleEntryList.get(position);
      } else {
        return grabbedBannerList.get(position - scheduleEntryList.size());
      }
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      EntryHolder holder = null;
      if (convertView == null) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(layoutResourceId, parent, false);

        holder = new EntryHolder();
        // holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
        holder.adImageView = (ImageView) convertView.findViewById(R.id.advert_list_image_view);

        convertView.setTag(holder);
      } else {
        holder = (EntryHolder) convertView.getTag();
      }
      if (position < scheduleEntryList.size()) {
        CbScheduleEntry entry = (CbScheduleEntry) getItem(position);
        holder.adImageView.setImageBitmap(
                CbBitmap.getBitmap(entry, ImageRef.IMAGE_TYPE_BANNER, getActivity()));

      } else {
        ExternalBannerGrab externalBannerGrab = (ExternalBannerGrab) getItem(position);
        holder.adImageView.setImageBitmap(BitmapFactory.decodeFile(
                externalBannerGrab.getBannerImageFile().getAbsolutePath()));
        holder.adUrl = externalBannerGrab.getBannerUrl();

      }
      holder.id = position;
      return convertView;
    }

    public void updateItems(ArrayList<CbScheduleEntry> entries) {
      Log.d(TAG, "Updating adapter, number of entries: " + entries.size());
      scheduleEntryList = entries;
      mAdapter.notifyDataSetChanged();

    }

    public void deleteItem(long id) {
      Log.d(TAG, "Delete requested: " + id);

      if (id < scheduleEntryList.size()) {
        CbScheduleEntry entry = ((CbScheduleEntry) getItem((int) id));
        if (entry._id != null) {
          if (scheduleEntryList.remove((int) id) != null) {
            Log.d(TAG, "Removed entry at position: " + id);
          }
          CloudbanterCentral.removeItem(entry._id);
          AdvertManager.removeItem(entry._id);
        } else {
          Log.w(TAG, "Null id when trying to remove");
        }
      } else {
        ExternalBannerGrab bannerGrab = ((ExternalBannerGrab) getItem((int) id));
        Log.d(TAG, "Banner url: " + bannerGrab.getBannerUrl());
        grabbedBannerList.remove(bannerGrab);
        BannerGrabber.getInstance().removeEntry(bannerGrab);
      }
      notifyDataSetChanged();
    }

    public void removeItem(long id) {
      Log.d(TAG, "Remove requested: " + id);
      if (id < scheduleEntryList.size()) {
        CbScheduleEntry entry = ((CbScheduleEntry) getItem((int) id));
        if (entry._id != null) {
          scheduleEntryList.remove((int) id);
          CloudbanterCentral.removeItem(entry._id);
        } else {
          Log.w(TAG, "Null id when trying to remove");
        }
      } else {
        ExternalBannerGrab bannerGrab = ((ExternalBannerGrab) getItem((int) id));
        Log.d(TAG, "Banner url: " + bannerGrab.getBannerUrl());
        grabbedBannerList.remove(bannerGrab);
        BannerGrabber.getInstance().removeEntry(bannerGrab);
      }
      notifyDataSetChanged();
    }
  }

  public static class EntryHolder {
    //ImageView imgIcon;
//        TextView txtTitle;
    ImageView adImageView;
    String adUrl;
    String fullScreenFilePath;
    Integer id;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // Restore the previously serialized activated item position.
    if (savedInstanceState != null
            && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
      setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
    }
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    // Activities containing this fragment must implement its callbacks.
    if (!(activity instanceof Callbacks)) {
      throw new IllegalStateException("Activity must implement fragment's callbacks.");
    }

    mCallbacks = (Callbacks) activity;
  }

  @Override
  public void onDetach() {
    super.onDetach();

    // Reset the active callbacks interface to the dummy implementation.
    mCallbacks = sDummyCallbacks;
  }

  @Override
  public void onListItemClick(ListView listView, View view, int position, long id) {
    super.onListItemClick(listView, view, position, id);
    EntryHolder entryHolder = (EntryHolder) view.getTag();
    if (entryHolder.adUrl == null) {
      // Notify the active callbacks interface (the activity, if the
      // fragment is attached to one) that an item has been selected.
      mCallbacks.onItemSelected(CloudbanterCentral.getItem(position)._id);
    } else {
      Log.d(TAG, "Starting web view for external ad");
      Intent intent = new Intent(getActivity(), CbWebBrowserActivity.class);
      intent.putExtra(CbWebBrowserActivity.EXTRA_URL, entryHolder.adUrl);
      startActivity(intent);
    }
  }


  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (mActivatedPosition != ListView.INVALID_POSITION) {
      // Serialize and persist the activated item position.
      outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
    }
  }

  boolean hasEntryId(CbScheduleEntry e) {
    ArrayAdapter<CbScheduleEntry> a = (ArrayAdapter<CbScheduleEntry>) getListAdapter();
    return a.getPosition(e) >= 0;
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View view,
                                  ContextMenu.ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, view, menuInfo);
    Log.d(TAG, "Creating context menu");
    Log.d(TAG, "View Id: " + view.getResources().getResourceName(view.getId()));
    MenuInflater inflater = getActivity().getMenuInflater();
    inflater.inflate(R.menu.menu_central, menu);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterView.AdapterContextMenuInfo info =
            (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    Log.d(TAG, "Item: " + getResources().getResourceName(item.getItemId()));
    Log.d(TAG, "Item id: " + info.id + " position: " + info.position);
    /*switch (item.getItemId()) {


      case R.id.menu_central_delete_ad:
        mAdapter.removeItem(info.id);
        break;
//            case R.id.menu_central_remove_ad:
//                mAdapter.deleteItem(info.id);
      default:
        Log.w(TAG, "Invalid action");
        Log.d(TAG, "Item: " + getResources().getResourceName(item.getItemId()));
    }*/
    if(item.getItemId() == R.id.menu_central_delete_ad){
      mAdapter.removeItem(info.id);
    }
    else{
      Log.w(TAG, "Invalid action");
      Log.d(TAG, "Item: " + getResources().getResourceName(item.getItemId()));
    }
    return super.onContextItemSelected(item);
  }

  public void handleDeleteAd(String id) {

  }

  public void handleRemoveOffer(String id) {
    if (null != id) {
      CloudbanterCentral.removeItem(id);
    }
  }

  public void remove(int id) {
    if (mAdapter != null) {
      Log.d(TAG, "Removing: " + id);
      mAdapter.removeItem(id);
    }
  }


  /**
   * Turns on activate-on-click mode. When this mode is on, list items will be
   * given the 'activated' state when touched.
   */
  public void setActivateOnItemClick(boolean activateOnItemClick) {
    // When setting CHOICE_MODE_SINGLE, ListView will automatically
    // give items the 'activated' state when touched.
    getListView().setChoiceMode(activateOnItemClick
            ? ListView.CHOICE_MODE_SINGLE
            : ListView.CHOICE_MODE_NONE);
  }

  private void setActivatedPosition(int position) {
    if (position == ListView.INVALID_POSITION) {
      getListView().setItemChecked(mActivatedPosition, false);
    } else {
      getListView().setItemChecked(position, true);
    }

    mActivatedPosition = position;
  }

  private TextView emptyCbcView() {
    TextView emptyView = new TextView(getActivity());
    LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT);
    emptyView.setLayoutParams(params);
    emptyView.setTextColor(getResources().getColor(R.color.gray13));
    emptyView.setText(getResources().getString(R.string.text_cbc_empty_list));
    emptyView.setTextSize(12);
    emptyView.setVisibility(View.GONE);
    emptyView.setGravity(Gravity.CENTER_VERTICAL
            | Gravity.CENTER_HORIZONTAL);

    ((ViewGroup) getListView().getParent()).addView(emptyView);

    return emptyView;
  }

  // TODO remove ?
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == CloudbanterCentralActivity.VIEW_DETAILS_REQUEST &&
            resultCode == Activity.RESULT_OK) {
      Log.d(TAG, "Activity result: " + resultCode);
      mAdapter.updateItems(CloudbanterCentral.getItems());
    }
  }

  public void updateCloudbanterCentralList() {
    Log.d(TAG, "Updating adapter with items from cb central, Number of items: " +
            CloudbanterCentral.getItems().size());
    mAdapter.updateItems(CloudbanterCentral.getItems());
  }
}

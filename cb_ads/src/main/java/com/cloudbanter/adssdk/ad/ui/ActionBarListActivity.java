package com.cloudbanter.adssdk.ad.ui;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public abstract class ActionBarListActivity extends AppCompatActivity {

  private ListView mListView;
  private Handler mHandler = new Handler();
  private boolean mFinishedStart = false;
  private Runnable mRequestFocus = new Runnable() {
    public void run() {
      mListView.focusableViewAvailable(mListView);
    }
  };

  protected ListView getListView() {
    if (mListView == null) {
      mListView = (ListView) findViewById(android.R.id.list);
    }
    return mListView;
  }

  protected void setListAdapter(ListAdapter adapter) {
    getListView().setAdapter(adapter);
  }

  protected ListAdapter getListAdapter() {
    ListAdapter adapter = getListView().getAdapter();
    if (adapter instanceof HeaderViewListAdapter) {
      return ((HeaderViewListAdapter) adapter).getWrappedAdapter();
    } else {
      return adapter;
    }
  }

  // subclasses should override...
  protected void onListItemClick(ListView lv, View v, int position, long id) {
  }

  protected boolean onListItemLongClick(AdapterView lv, View v, int position, long id) {
    return false;
  }

  ;

  @Override
  public void onContentChanged() {
    super.onContentChanged();
    View emptyView = findViewById(android.R.id.empty);
    mListView = (ListView) findViewById(android.R.id.list);
    if (mListView == null) {
      throw new RuntimeException(
              "Your content must have a ListView whose id attribute is " +
                      "'android.R.id.list'");
    }
    if (emptyView != null) {
      mListView.setEmptyView(emptyView);
    }
    mListView.setOnItemClickListener(mOnClickListener);
    mListView.setOnItemLongClickListener(mOnLongClickListener);
    if (mFinishedStart) {
      setListAdapter(getListAdapter());
    }
    mHandler.post(mRequestFocus);
    mFinishedStart = true;
  }

  private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
      onListItemClick((ListView) parent, v, position, id);
    }
  };

  private AdapterView.OnItemLongClickListener mOnLongClickListener =
          new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                           long id) {
              return onListItemLongClick(parent, view, position, id);
            }
          };

  @Override
  protected void onDestroy() {
    mHandler.removeCallbacks(mRequestFocus);
    super.onDestroy();
  }
}

package com.cloudbanter.myapplication;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.cloudbanter.adssdk.CbAdsSdk;
import com.cloudbanter.adssdk.PermissionsManager;
import com.cloudbanter.adssdk.ad.ui.ACbActivity;
import com.cloudbanter.adssdk.ad_exchange.views.CloudbanterAdView;

public class MainActivity extends ACbActivity
        implements View.OnClickListener, TextView.OnEditorActionListener,
        PermissionsManager.OnRequestPermissionsCallback {

    private CloudbanterAdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CbAdsSdk.initialize(getApplication());

        setupCloudbanterView();

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    @Override
    public void onPermissionsDenied(int requestCode) {

    }

    private void setupCloudbanterView() {
        mAdView = (CloudbanterAdView) findViewById(R.id.compose_activity_adview);
        mAdView.init();
    }
}

/*------------------------------------------------------------------------------
 © CloudBanter, 2015/07/30 16:38:13
------------------------------------------------------------------------------*/
package com.cloudbanter.adssdk.ad.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// startup cloudbanter offline service managers 
public class BootReceiver extends BroadcastReceiver {
  public void onReceive(Context context, Intent intent) {
    // Cloudbanter Image Download Service
    final Intent i = new Intent(context, CbDownloadService.class);
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startService(i);
  }
}

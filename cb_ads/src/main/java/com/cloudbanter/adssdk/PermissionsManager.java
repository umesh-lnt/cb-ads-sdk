package com.cloudbanter.adssdk;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.SparseArrayCompat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This manager ensures to request a list of permissions at least 3 times. At the end, if the user
 * doesn't grant all permissions, then the callback is notified.
 *
 * @author <a href="mailto:aajn88@gmail.com">Antonio Jimenez</a>
 * @since 3/4/17
 */
public class PermissionsManager {

  private static final int MAX_REQUESTS_COUNT = 2;
  private static PermissionsManager sPermissionsManager;

  private final SparseArrayCompat<RequestWrapper> mCallbacks =
          new SparseArrayCompat<>();

  public static PermissionsManager getInstance() {
    if (sPermissionsManager == null) {
      sPermissionsManager = new PermissionsManager();
    }
    return sPermissionsManager;
  }

  @SuppressLint("NewApi")
  public void requestPermissions(Object object, @NonNull String[] permissions, int requestCode,
                                 OnRequestPermissionsCallback callback,
                                 String... optionalPermissions) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      callback.onPermissionsGranted(requestCode);
    }
    Set<String> setOptionalPermissions = new HashSet<>(optionalPermissions.length);
    Collections.addAll(setOptionalPermissions, optionalPermissions);
    boolean granted = true;
    for (String permission : permissions) {
      granted &= ContextCompat.checkSelfPermission(getContext(object), permission) ==
              PackageManager.PERMISSION_GRANTED || setOptionalPermissions.contains(permission);
    }
    if (!granted) {
      mCallbacks.append(requestCode, getWrapper(object, setOptionalPermissions, callback));
      requestPermissions(object, permissions, requestCode);
    } else {
      callback.onPermissionsGranted(requestCode);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  private void requestPermissions(Object object, @NonNull String[] permissions, int requestCode) {
    if (object instanceof Activity) {
      ((Activity) object).requestPermissions(permissions, requestCode);
      return;
    }
    if (object instanceof Fragment) {
      ((Fragment) object).requestPermissions(permissions, requestCode);
      return;
    }
    throw new IllegalArgumentException("Received object is not Activity nor Fragment");
  }

  private Context getContext(Object object) {
    if (object instanceof Activity) {
      return (Context) object;
    }
    if (object instanceof Fragment) {
      return ((Fragment) object).getActivity();
    }
    throw new IllegalArgumentException("Received object is not Activity nor Fragment");
  }

  @TargetApi(Build.VERSION_CODES.M)
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    RequestWrapper requestWrapper = mCallbacks.get(requestCode);
    if (requestWrapper == null) {
      return;
    }
    boolean granted = true;
    for (int i = 0; i < grantResults.length; i++) {
      int result = grantResults[i];
      granted &= result == PackageManager.PERMISSION_GRANTED ||
              requestWrapper.mOptionalPermissions.contains(permissions[i]);
    }
    if (granted) {
      manageGrantedPermissions(requestWrapper, requestCode);
    } else {
      manageDeniedPermissions(permissions, requestWrapper, requestCode);
    }
  }

  private void manageGrantedPermissions(RequestWrapper requestWrapper, int requestCode) {
    mCallbacks.remove(requestCode);
    requestWrapper.mCallback.onPermissionsGranted(requestCode);
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  private void manageDeniedPermissions(@NonNull String[] permissions, RequestWrapper requestWrapper,
                                       int requestCode) {
    if (requestWrapper.mRequestsCount >= MAX_REQUESTS_COUNT) {
      mCallbacks.remove(requestCode);
      requestWrapper.mCallback.onPermissionsDenied(requestCode);
    } else {
      requestWrapper.mRequestsCount++;
      requestWrapper.requestPermissions(permissions, requestCode);
    }
  }

  RequestWrapper getWrapper(Object object, Set<String> optionalPermissions,
                            OnRequestPermissionsCallback callback) {
    if (object instanceof Activity) {
      return new RequestWrapper((Activity) object, optionalPermissions, callback);
    }
    if (object instanceof Fragment) {
      return new RequestWrapper((Fragment) object, optionalPermissions, callback);
    }
    throw new IllegalArgumentException("Received object is not Activity nor Fragment");
  }

  private class RequestWrapper {

    Activity mActivity;

    Fragment mFragment;

    OnRequestPermissionsCallback mCallback;

    Set<String> mOptionalPermissions;

    int mRequestsCount;

    private RequestWrapper(Fragment fragment, Set<String> optionalPermissions,
                           OnRequestPermissionsCallback callback) {
      mFragment = fragment;
      this.mCallback = callback;
      this.mOptionalPermissions = optionalPermissions;
    }

    private RequestWrapper(Activity activity, Set<String> optionalPermissions,
                           OnRequestPermissionsCallback callback) {
      this.mActivity = activity;
      this.mCallback = callback;
      this.mOptionalPermissions = optionalPermissions;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void requestPermissions(String[] permissions, int requestCode) {
      PermissionsManager.this.requestPermissions(mActivity != null ? mActivity : mFragment,
              permissions, requestCode);
    }
  }

  public interface OnRequestPermissionsCallback {

    void onPermissionsGranted(int requestCode);

    void onPermissionsDenied(int requestCode);

  }

}

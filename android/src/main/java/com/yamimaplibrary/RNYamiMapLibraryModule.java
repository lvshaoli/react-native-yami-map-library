
package com.yamimaplibrary;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.google.gson.Gson;
import com.minew.beacon.BluetoothState;
import com.minew.beacon.MinewBeacon;
import com.minew.beacon.MinewBeaconManager;
import com.minew.beacon.MinewBeaconManagerListener;
import com.yamimaplibrary.bean.Beacon;
import com.yamimaplibrary.bean.JsData;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

public class RNYamiMapLibraryModule extends ReactContextBaseJavaModule implements ActivityEventListener {

  public ReactApplicationContext reactContext = null;
  private static final int PERMISSION_REQUEST_CODE = 1;
  private static final String E_ACTIVITY_DOES_NOT_EXIST = "E_ACTIVITY_DOES_NOT_EXIST";

  private static final String E_PICKER_CANCELLED_KEY = "E_PICKER_CANCELLED";
  private static final String E_PICKER_CANCELLED_MSG = "User cancelled image selection";

  private static final String E_CALLBACK_ERROR = "E_CALLBACK_ERROR";
  private static final String E_FAILED_TO_SHOW_PICKER = "E_FAILED_TO_SHOW_PICKER";
  private static final String E_FAILED_TO_OPEN_CAMERA = "E_FAILED_TO_OPEN_CAMERA";
  private static final String E_NO_IMAGE_DATA_FOUND = "E_NO_IMAGE_DATA_FOUND";
  private static final String E_CAMERA_IS_NOT_AVAILABLE = "E_CAMERA_IS_NOT_AVAILABLE";
  private static final String E_CANNOT_LAUNCH_CAMERA = "E_CANNOT_LAUNCH_CAMERA";
  private static final String E_PERMISSIONS_MISSING = "E_PERMISSION_MISSING";
  private static final String E_ERROR_WHILE_CLEANING_FILES = "E_ERROR_WHILE_CLEANING_FILES";
  private WebView mWebView;
  private JsInterface mJsInterface;
  private MinewBeaconManager mMinewBeaconManager;
  private Gson mGson = new Gson();
  private JsData mJsData = new JsData();

  public RNYamiMapLibraryModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNYamiMapLibrary";
  }

  public WebView initView() {
    initManager();
    mWebView = new WebView(this.reactContext);
    mWebView.setBackgroundColor(2);
    mJsInterface = new JsInterface();
    WebSettings webSettings = mWebView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setDomStorageEnabled(true);
    webSettings.setAppCacheMaxSize(Long.MAX_VALUE);
    webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    webSettings.setDefaultTextEncodingName("utf-8");
    webSettings.setAllowFileAccessFromFileURLs(true);
    webSettings.setAllowUniversalAccessFromFileURLs(true);
    mWebView.addJavascriptInterface(mJsInterface, "Native");
    mWebView.loadUrl("https://tx.yunjinginc.com/wechatexp/beaconDemo/index.html");
    initListener();
    return mWebView;
  }


  private void checkBluetooth() {
    BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
    switch (bluetoothState) {
      // 不支持蓝牙
      case BluetoothStateNotSupported:
        startBeaconComplete("system unsupported");
        break;
      // 蓝牙未开启
      case BluetoothStatePowerOff:
        startBeaconComplete("bluetooth power off");
        break;
      // 蓝牙开启
      case BluetoothStatePowerOn:
        startBeaconComplete("ok");
        if (mMinewBeaconManager != null) {
          mMinewBeaconManager.startScan();
        }
        break;
      default:
    }
  }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
//            permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions,
//                grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE){
//            if (grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // 获得权限
//                checkBluetooth();
//            } else {
//                // 未获得权限
//                startBeaconComplete("location service disable");
//            }
//        }
//    }



  private void initManager() {
    mMinewBeaconManager = MinewBeaconManager.getInstance(this.reactContext);
  }

  private void initListener() {
    mJsInterface.setOnJsListener(mOnJsListener);
  }


  private OnJsListener mOnJsListener = new OnJsListener() {
    @Override
    public void onStartBeacon() {
            final Activity activity = getCurrentActivity();
//      ActivityCompat.requestPermissions(activity,
//              new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//              PERMISSION_REQUEST_CODE);
      List<String> missingPermissions = new ArrayList<>();
      missingPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

      ((PermissionAwareActivity) activity).requestPermissions(missingPermissions.toArray(new String[missingPermissions.size()]), PERMISSION_REQUEST_CODE, new PermissionListener() {

        @Override
        public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
          if (requestCode == PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
              // 获得权限
              checkBluetooth();
            } else {
              // 未获得权限
              startBeaconComplete("location service disable");
            }
          }
          return true;
        }
      });
    }

    @Override
    public void onSearchBeacon() {
      if (mMinewBeaconManager != null) {
        mMinewBeaconManager.setDeviceManagerDelegateListener(mMinewBeaconManagerListener);
      }
    }

    @Override
    public void onStopBeacon() {
      if (mMinewBeaconManager != null) {
        mMinewBeaconManager.setDeviceManagerDelegateListener(null);
        mMinewBeaconManager.stopScan();
        stopBeaconComplete("ok");
      }
    }
  };



  private MinewBeaconManagerListener mMinewBeaconManagerListener =  new MinewBeaconManagerListener() {

    @Override
    public void onAppearBeacons(List<MinewBeacon> minewBeacons) {

    }

    @Override
    public void onDisappearBeacons(List<MinewBeacon> minewBeacons) {

    }

    @Override
    public void onRangeBeacons(final List<MinewBeacon> minewBeacons) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          updateBeacon(minewBeacons);
        }
      });
    }

    @Override
    public void onUpdateState(BluetoothState bluetoothState) {
    }

  };

  private void startBeaconComplete(String states) {
    mWebView.loadUrl("javascript:backSDK.startBeacon.complete({errMsg:'startSearchBeacons:" + states + "'})");
  }

  private void searchBeaconComplete(String data) {
    mWebView.loadUrl("javascript:backSDK.searchBeacon.complete(" + data + ")");
  }

  private void stopBeaconComplete(String states) {
    mWebView.loadUrl("javascript:backSDK.stopBeacon.complete({errMsg:'stopSearchBeacons:" + states + "'})");
  }

  private void updateBeacon(List<MinewBeacon> minewBeacons) {
    if (minewBeacons == null || minewBeacons.size() == 0) {
      return;
    }
    mJsData.getBeacons().clear();
    for (MinewBeacon minewBeacon : minewBeacons) {
      Beacon beacon = Beacon.minewBeacon2Beacon(minewBeacon);
      if (beacon != null) {
        mJsData.getBeacons().add(beacon);
      }
    }
    if (mJsData.getBeacons().size() == 0) {
      return;
    }
    String data = mGson.toJson(mJsData);
    searchBeaconComplete(data);
  }


  @Override
  public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

  }

  @Override
  public void onNewIntent(Intent intent) {

  }

}
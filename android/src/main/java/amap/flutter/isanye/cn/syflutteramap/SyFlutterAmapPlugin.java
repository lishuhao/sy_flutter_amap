package amap.flutter.isanye.cn.syflutteramap;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import android.support.v4.app.ActivityCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;

/** SyFlutterAmapPlugin */
public class SyFlutterAmapPlugin implements MethodCallHandler,ActivityCompat.OnRequestPermissionsResultCallback {

  private final Activity activity;
  private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
  private AMapLocationClient locationClient = null;

  private SyFlutterAmapPlugin(Activity activity){
    this.activity = activity;
    this.locationClient = new AMapLocationClient(activity);
  }

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "sy_flutter_amap");
    channel.setMethodCallHandler(new SyFlutterAmapPlugin(registrar.activity()));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    }else if(call.method.equals("hasPermission")){
      Log.e("amap","hasPermission");
      int res = this.hasPermission() ? 1 : 2;
      result.success(res);
    }else if(call.method.equals("getLocation")){
      Log.e("amap","getLocation");
      this.getLocation(result);
    } else {
      result.notImplemented();
    }
  }

  private boolean hasPermission(){
    int permissionState = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
    return permissionState == PackageManager.PERMISSION_GRANTED;
  }

  private void requestPermissions() {
    ActivityCompat.requestPermissions(
            activity,
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            REQUEST_PERMISSIONS_REQUEST_CODE
    );
  }

  private void getLocation(Result result){
    if(!this.hasPermission()){
      this.requestPermissions();
      AMapLocation location = locationClient.getLastKnownLocation();
      Log.e("amap",location.getAddress());
    }else{
      Log.e("amap","getLocation");
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
      Log.e("amap","onRequestPermissionsResult");
    }
  }
}

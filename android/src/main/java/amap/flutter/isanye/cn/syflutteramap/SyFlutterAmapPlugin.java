package amap.flutter.isanye.cn.syflutteramap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import android.support.v4.app.ActivityCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** SyFlutterAmapPlugin */
public class SyFlutterAmapPlugin implements MethodCallHandler,ActivityCompat.OnRequestPermissionsResultCallback {

  private final Activity activity;
  private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
  private AMapLocationClient locationClient;

  /**
   * 需要进行检测的权限数组
   */
  private String[] needPermissions = {
          Manifest.permission.ACCESS_COARSE_LOCATION,
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.WRITE_EXTERNAL_STORAGE,
          Manifest.permission.READ_EXTERNAL_STORAGE,
          Manifest.permission.READ_PHONE_STATE
  };

  private SyFlutterAmapPlugin(Activity activity){
    this.activity = activity;
    this.locationClient = new AMapLocationClient(activity);
    //AMapLocationClient.setApiKey("");
  }

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "cn.isanye.sy_flutter_amap");
    channel.setMethodCallHandler(new SyFlutterAmapPlugin(registrar.activity()));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    switch (call.method) {
      /*case "constructor":
        final String apiKey = call.argument("apiKey");
        AMapLocationClient.setApiKey(apiKey);
        break;*/
      case "hasPermission":
        String res = this.hasPermission() ? "true" : "false";
        result.success(res);
        break;
      case "getLocation":
        this.getLocation(result);
        break;
      case "signSha1":
        result.success(signSha1(activity));
        break;
      default:
        result.notImplemented();
        break;
    }
  }

  private boolean hasPermission(){
    Log.e("amap","hasPermission");
    List<String> needRequestPermissionList = _findDeniedPermissions(needPermissions);
    return null == needRequestPermissionList || needRequestPermissionList.size() <= 0;
  }

  private void requestPermissions() {
    List<String> needRequestPermissionList = _findDeniedPermissions(needPermissions);
    ActivityCompat.requestPermissions(
            activity,
            needRequestPermissionList.toArray(new String[needRequestPermissionList.size()]),
            REQUEST_PERMISSIONS_REQUEST_CODE
    );
  }

  private void getLocation(Result result){
    if(!this.hasPermission()){
      this.requestPermissions();
      return;
    }
    Log.e("amap","getLocation");
    AMapLocationClientOption option = new AMapLocationClientOption();
    //设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
    option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
    option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
    //获取一次定位结果：
    //该方法默认为false。
    option.setOnceLocation(true);
    //获取最近3s内精度最高的一次定位结果：
    //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
    option.setOnceLocationLatest(true);
    //设置是否返回地址信息（默认返回地址信息）
    option.setNeedAddress(true);

    if(locationClient != null){
      locationClient.setLocationOption(option);
      //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
      locationClient.stopLocation();
      locationClient.startLocation();
      AMapLocation location = locationClient.getLastKnownLocation();
      if(location != null){
        if(location.getErrorCode() == 0){
          result.success(location.toStr());
          Log.e("amap",location.toStr());
        }else{
          result.error(Integer.toString(location.getErrorCode()),location.getErrorInfo(),null);
          Log.e("amap err",location.getErrorInfo());
        }
      }else {
        result.error("LOCATION_IS_NULL","location_is_null",null);
      }
      locationClient.stopLocation();
    }else{
      result.error("LOCATION_CLIENT_IS_NULL","location_client_is_null",null);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
      Log.e("amap","onRequestPermissionsResult");
    }
  }

  //打包签名的sha1，设置高徳key的时候需要用
  private static String signSha1(Context context) {
    try {
      PackageInfo info = context.getPackageManager().getPackageInfo(
              context.getPackageName(), PackageManager.GET_SIGNATURES);
      byte[] cert = info.signatures[0].toByteArray();
      MessageDigest md = MessageDigest.getInstance("SHA1");
      byte[] publicKey = md.digest(cert);
      StringBuffer hexString = new StringBuffer();
      for (int i = 0; i < publicKey.length; i++) {
        String appendString = Integer.toHexString(0xFF & publicKey[i])
                .toUpperCase(Locale.US);
        if (appendString.length() == 1)
          hexString.append("0");
        hexString.append(appendString);
        hexString.append(":");
      }
      String result = hexString.toString();
      return result.substring(0, result.length()-1);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 获取权限集中需要申请权限的列表
   */
  private List<String> _findDeniedPermissions(String[] permissions) {
    List<String> needRequestPermissionList = new ArrayList<>();
    for (String perm : permissions) {
      if (ContextCompat.checkSelfPermission(activity,
              perm) != PackageManager.PERMISSION_GRANTED
              || ActivityCompat.shouldShowRequestPermissionRationale(
              activity, perm)) {
        needRequestPermissionList.add(perm);
      }
    }
    return needRequestPermissionList;
  }
}

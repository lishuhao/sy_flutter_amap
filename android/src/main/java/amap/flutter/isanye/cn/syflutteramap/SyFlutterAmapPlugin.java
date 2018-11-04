package amap.flutter.isanye.cn.syflutteramap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import android.support.v4.app.ActivityCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;

import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** SyFlutterAmapPlugin */
public class SyFlutterAmapPlugin implements MethodCallHandler,PluginRegistry.RequestPermissionsResultListener {

  private Result result;
  private final Activity activity;
  private final Context context;
  private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 0;
  private AMapLocationClient locationClient;

  private static String TAG = "Amap";

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

  private SyFlutterAmapPlugin(Registrar registrar){
    this.activity = registrar.activity();
    this.context = registrar.context();
    this.locationClient = new AMapLocationClient(context);
    //AMapLocationClient.setApiKey("");
  }

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    MethodChannel channel = new MethodChannel(registrar.messenger(), "cn.isanye.sy_flutter_amap");
    SyFlutterAmapPlugin amapPlugin = new SyFlutterAmapPlugin(registrar);
    channel.setMethodCallHandler(amapPlugin);
    registrar.addRequestPermissionsResultListener(amapPlugin);//重要!!!，缺少这行会导致 onRequestPermissionsResult 回调失败
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {

    switch (call.method) {
      /*case "constructor":
        final String apiKey = call.argument("apiKey");
        AMapLocationClient.setApiKey(apiKey);
        break;*/
      case "hasPermission":
        boolean res = this.hasPermission();
        result.success(res);
        break;
      case "requestPermission":
        this.result = result;
        this.requestPermissions();
        break;
      case "getLocation":
        this.getLocation(result);
        break;
      case "signSha1":
        result.success(signSha1(context));
        break;
      default:
        result.notImplemented();
        break;
    }
  }

  private boolean hasPermission(){
    List<String> needRequestPermissionList = _findDeniedPermissions(needPermissions);
    return null == needRequestPermissionList || needRequestPermissionList.size() <= 0;
  }

  private void requestPermissions() {
    List<String> needRequestPermissionList = _findDeniedPermissions(needPermissions);
    if(needRequestPermissionList.size() <= 0){
      Result result = this.result;
      this.result = null;
      result.success(true);
      return;
    }
    ActivityCompat.requestPermissions(
            activity,
            needRequestPermissionList.toArray(new String[needRequestPermissionList.size()]),
            REQUEST_PERMISSIONS_REQUEST_CODE
    );
  }

  private void getLocation(Result result){
    if(!this.hasPermission()){
      result.error("NOT_HAVE_PERMISSION","not have permission,please call requestPermission first",null);
      return;
    }
    AMapLocationClientOption option = new AMapLocationClientOption();
    option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
    option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
    option.setOnceLocation(true);
    option.setOnceLocationLatest(true);
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
          Log.e(TAG,location.toStr());
        }else{
          result.error(Integer.toString(location.getErrorCode()),location.getErrorInfo(),null);
          Log.e("amap err",location.getErrorInfo());
        }
      }else {
        result.error("LOCATION_IS_NULL","请检查网络连接及GPS是否开启",null);
      }
      locationClient.stopLocation();
    }else{
      result.error("LOCATION_CLIENT_IS_NULL","location_client_is_null",null);
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
      if (ContextCompat.checkSelfPermission(context,
              perm) != PackageManager.PERMISSION_GRANTED
              || ActivityCompat.shouldShowRequestPermissionRationale(
              activity, perm)) {
        needRequestPermissionList.add(perm);
      }
    }
    return needRequestPermissionList;
  }

  @Override
  public boolean onRequestPermissionsResult(int i, String[] strings, int[] ints) {
    Result result = this.result;
    this.result = null;
    if(result == null){
      return true;
    }
    if (i == REQUEST_PERMISSIONS_REQUEST_CODE) {
      if(this.hasPermission()){
        result.success(true);
      }else{
        result.success(false);
      }
    }
    return true;
  }
}

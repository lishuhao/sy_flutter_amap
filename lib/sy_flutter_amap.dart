library sy_flutter_amap;

import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';

part 'models/location.dart';

class SyFlutterAmap {
  static const MethodChannel _channel =
      const MethodChannel('cn.isanye.sy_flutter_amap');

  static Future<bool> hasPermission() async {
    final bool isGranted = await _channel.invokeMethod('hasPermission');
    print(isGranted);
    return isGranted;
  }

  //请求定位权限
  static Future<bool> requestPermission() async {
    final bool result = await _channel.invokeMethod('requestPermission');
    print(result);
    return result;
  }

  //获取最近一次的定位数据
  static Future<SyLocation> getLastKnownLocation(
      {String androidApiKey, String iosApiKey}) async {
    var result;
    try {
      result =
          await _channel.invokeMethod('getLastKnownLocation', <String, dynamic>{
        'iosApiKey': iosApiKey ?? '',
        'androidApiKey': androidApiKey ?? '',
      });
      return SyLocation.fromJson(json.decode(result));
    } on PlatformException catch (e) {
      print(e);
      return null;
    }
  }

  //获取安全码SHA1，申请高德地图android key的时候用
  static Future<String> signSha1() async {
    var result = await _channel.invokeMethod('signSha1');
    print(result);
    return result;
  }
}

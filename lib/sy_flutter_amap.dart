import 'dart:async';

import 'package:flutter/services.dart';

class SyFlutterAmap {
  static const MethodChannel _channel =
      const MethodChannel('cn.isanye.sy_flutter_amap');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  Future<bool> hasPermission() async {
    var result = await _channel.invokeMethod('hasPermission');
    print(result);
    return true;
  }

  Future<Map<String, double>> getLocation() async {
    var result = await _channel.invokeMethod('getLocation');
    //.then((result) => result.cast<String, double>());
    print(result);
    return Map();
  }
}

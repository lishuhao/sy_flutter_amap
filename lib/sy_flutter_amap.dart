library sy_flutter_amap;

import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';

part 'models/location.dart';

class SyFlutterAmap {
  static const MethodChannel _channel =
      const MethodChannel('cn.isanye.sy_flutter_amap');

  SyFlutterAmap() {
/*    _channel.setMethodCallHandler((MethodCall call) {
      switch (call.method) {
        case 'onRequestPermissionsResult':
          print('a');
          break;
        default:
          print('b');
      }
    });*/
  }

  Future<bool> hasPermission() async {
    final bool isGranted = await _channel.invokeMethod('hasPermission');
    print(isGranted);
    return isGranted;
  }

  Future<bool> requestPermission() async {
    final bool result = await _channel.invokeMethod('requestPermission');
    print(result);
    return true;
  }

  Future<SyLocation> getLocation() async {
    var result;
    try {
      result = await _channel.invokeMethod('getLocation');
      return SyLocation.fromJson(json.decode(result));
    } on PlatformException catch (e) {
      //print(e);
      return null;
    }
  }

  Future<String> signSha1() async {
    var result = await _channel.invokeMethod('signSha1');
    print(result);
    return result;
  }
}

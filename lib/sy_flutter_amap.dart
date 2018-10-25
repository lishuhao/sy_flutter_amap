library sy_flutter_amap;

import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';

part 'models/location.dart';

class SyFlutterAmap {
  static const MethodChannel _channel =
      const MethodChannel('cn.isanye.sy_flutter_amap');

  /*Future onReady;

  SyFlutterAmap(String apiKey) {
    onReady = new Future(() {
      _channel.invokeMethod('constructor', <String, dynamic>{"apiKey": apiKey});
    });
  }*/

  Future<bool> hasPermission() async {
    var result = await _channel.invokeMethod('hasPermission');
    print(result);
    return true;
  }

  Future<SyLocation> getLocation() async {
    var result;
    try {
      result = await _channel.invokeMethod('getLocation');
      return SyLocation.fromJson(json.decode(result));
    } on PlatformException catch (e) {
      print(e);
      return null;
    }
  }

  Future<String> signSha1() async {
    var result = await _channel.invokeMethod('signSha1');
    //.then((result) => result.cast<String, double>());
    print(result);
    return result;
  }
}

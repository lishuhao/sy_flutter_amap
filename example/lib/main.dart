import 'package:flutter/material.dart';
import 'package:sy_flutter_amap/sy_flutter_amap.dart';

void main() => runApp(new MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(
          title: const Text('Plugin example app'),
        ),
        body: new Center(
            child: Column(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: <Widget>[
            new RaisedButton(
              child: Text('检查权限'),
              onPressed: () async {
                await SyFlutterAmap().hasPermission();
              },
            ),
            new RaisedButton(
              child: Text('获取权限'),
              onPressed: () async {
                await SyFlutterAmap().requestPermission();
              },
            ),
            new RaisedButton(
              child: Text('获取位置'),
              onPressed: () async {
                SyLocation loc = await SyFlutterAmap().getLocation();
                if (loc != null) {
                  print(loc.toJon());
                }
              },
            ),
            new RaisedButton(
              child: Text('获取签名'),
              onPressed: () async {
                String s = await SyFlutterAmap().signSha1();
                print(s);
              },
            ),
          ],
        )),
      ),
    );
  }
}

/*
 * @Author: your name
 * @Date: 2020-12-28 10:36:47
 * @LastEditTime: 2020-12-29 11:29:37
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: /example/Users/imacmini/ParentNode/Flutter/Package/lbwWake/lib/lbwWake.dart
 */
import 'dart:async';

import 'package:flutter/services.dart';

class LbwWake {
  static const MethodChannel _channel = const MethodChannel('lbwWake');

  //自定义几个关键字 来区分android端口传来的参数
  static const List<String> enumeration = ["wake", "temporary", "end"];

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<void> initSDK(
      {String appid, String appkey, String secretkey}) async {
    Map<String, String> params = Map<String, String>();
    params['appid'] = appid;
    params['appkey'] = appkey;
    params['secretkey'] = secretkey;
    await _channel.invokeMethod("initsdk", params);
  }

  static Future<void> startspeak() async {
    await _channel.invokeMethod("startspeak", "");
  }

  static void addListen(Function callback) {
    _channel.setMethodCallHandler((call) {
      callback(call.method, call.arguments);
    });
  }
}

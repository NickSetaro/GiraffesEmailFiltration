import 'dart:collection';

import 'package:flutter/cupertino.dart';

import 'bucket.dart';
import 'bucket_list.dart';
import 'notifications.dart';
import 'package:flutter/services.dart';

class notReceiver {
  static const platform = MethodChannel("samples.flutter.dev/native");
  Notif not;
  List<Notif> notList;
  Map<String, List<String>> notMap = new HashMap<String, List<String>>();

  Future<void> setNots(Bucket bucket) async{
      getNotificationMap(bucket.address);
      notMap.forEach((key, value) {
        not = new Notif(bucket.address);
        not.date = key;
        not.subject = value[0];
        not.bodySnip = value[1];
        bucket.addNotification(not);
      });
  }

  Future<Map<String, List<String>>> getNotificationMap(String email) async{
    Map<String, List<String>> map = await platform.invokeMapMethod("getNotifications", {"address": email,});
    return map;
  }

}
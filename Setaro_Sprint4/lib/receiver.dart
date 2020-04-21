import 'dart:collection';

import 'bucket.dart';
import 'bucket_list.dart';
import 'notifications.dart';
import 'package:flutter/services.dart';

class notReceiver {
  static const platform = MethodChannel("samples.flutter.dev/native");
  Bucket bucket;
  HashMap<String, List<String>> notMap;
  notReceiver(this.bucket){
    setNots(bucket.address);
  }

  void setNots(String address){
    getNotificationMap(address);
    notMap.forEach((key,value){
      String date = key;
      String subject = value[0];
      String bodySnip = value[1];
      bucket.addNotification(new Notif(subject, bodySnip, date, ''));
    });
  }

  Future<void> getNotificationMap(String email) async{
    HashMap<String, List<String>> map;
    map = await platform.invokeMapMethod("getNotifications", {
      "address": email,
    });
    notMap = map;
  }

}
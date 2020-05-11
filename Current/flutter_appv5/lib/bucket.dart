
import 'package:flutterappv5/notifications.dart';
import 'package:flutter/services.dart';
import 'notifications.dart';

class Bucket {
  String name;
  String address;
  List<Notif> notifications = new List<Notif>();
  List<String> keyWords = new List<String>();
  static const platform = MethodChannel("samples.flutter.dev/native");

  Bucket(String name, String address) {
    this.name = name;
    this.address = address;
  }

  void addNotification(Notif n) {
    notifications.add(n);
  }

  void clearNotifications() {
    notifications = new List<Notif>();
  }


  Future<void> removeNot(String uuid) async{
    String result = "";
    for(Notif n in notifications){
      if(n.uuid == uuid)
        notifications.remove(n);
    }
    try{
      result = await platform.invokeMethod('removeNot', {
        "uuid": uuid,
        "address": address
      });

    }on PlatformException catch (e) {
      result = "Failed to Invoke: '${e.message}'.";
    }
  }
}
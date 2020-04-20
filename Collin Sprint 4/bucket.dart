
import 'package:flutterappv5/notifications.dart';
import 'package:flutter/services.dart';
import 'notifications.dart';

class Bucket {
  String name;
  String address;
  List<Notif> notifications = new List<Notif>();
  static const platform = MethodChannel("samples.flutter.dev/native");

  Bucket(String name, String address) {
    this.name = name;
    this.address = address;
  }

  void addNotification(Notif n) {
    notifications.add(n);
  }
}
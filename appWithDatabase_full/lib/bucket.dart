
import 'notifications.dart';
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


  void removeNot(Notif not) {
    notifications.remove(not);
  }

  void addKeyword(String keyword) {
    keyWords.add(keyword);
  }

  void removeKeyword(String keyword) {
    keyWords.remove(keyword);
  }

  void setKeywords(List<String> keyWords) {
    this.keyWords = keyWords;
  }
}
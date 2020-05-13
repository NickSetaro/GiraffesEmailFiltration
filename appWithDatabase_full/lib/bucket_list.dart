import 'dart:developer';

import 'package:flutter/material.dart';
import 'bucket.dart';
import 'package:flutter/services.dart';


class BucketList {
  final List<Bucket> bucketList = [];
  static const platform = MethodChannel("samples.flutter.dev/native");

  void addBucket(String name, String address) {
    Bucket bucket = new Bucket(name, address);
    bucketList.add(bucket);
  }

  List<Bucket> getBucketList(){
    return bucketList;
  }

}
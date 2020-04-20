import 'dart:developer';

import 'package:flutter/material.dart';
import 'bucket.dart';
import 'package:flutter/services.dart';


class BucketList {
  final List<Bucket> bucketList = [];
  static const platform = MethodChannel("samples.flutter.dev/native");

  Future<void> addBucket(String name, String address) async{
    Bucket bucket = new Bucket(name, address);
    await nativeAdd(address);
    bucketList.add(bucket);
  }

  List<Bucket> getBucketList(){
    return bucketList;
  }

  Future<void> nativeAdd(String address) async{
    String result;
    result = await platform.invokeMethod('addBucket', {
      "address": address,
    });
    log(result);
  }

}
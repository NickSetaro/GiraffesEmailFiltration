import 'dart:developer';
import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class AddBucket extends StatelessWidget {

  static const platform = MethodChannel("samples.flutter.dev/native");
  String result;

  final Function addBucketCallback;

  AddBucket(this.addBucketCallback);

  final controllerName = TextEditingController();
  final controllerAddress = TextEditingController();

  @override
  Widget build(BuildContext context) {
    String name;
    String address;
    return Container(
      color: Color(0xff757575),
      child: Container(
        height: 510,
        padding: EdgeInsets.all(10),
        decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.only(
                topRight: Radius.circular(20), topLeft: Radius.circular(20))),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: <Widget>[
            Text(
              'Add Bucket',
              textAlign: TextAlign.center,
              style: TextStyle(
                fontSize: 30,
                color: Colors.lightBlueAccent,
              ),
            ),
            TextField(
              controller: controllerName,
              autofocus: true,
              textAlign: TextAlign.center,
              decoration: InputDecoration(
                  contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
                  hintText: "Bucket Name",
                  border:
                  OutlineInputBorder(borderRadius: BorderRadius.circular(10.0))),
            ),
            TextField(
              controller: controllerAddress,
              autofocus: true,
              textAlign: TextAlign.center,
              decoration: InputDecoration(
                  contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
                  hintText: "Enter Email Address",
                  border:
                  OutlineInputBorder(borderRadius: BorderRadius.circular(10.0))),
            ),
            FlatButton(
              child: Text(
                'Add',
                style: TextStyle(color: Colors.white),
              ),
              color: Colors.lightBlueAccent,
              onPressed: () async {
                addBucketCallback(controllerName.text, controllerAddress.text);
                addEmail(controllerAddress.text);

                controllerAddress.clear();
                controllerName.clear();
              },
            ),
          ],
        ),
      ),
    );
  }

  Future<void> addEmail(String email) async{
    String result = "";
    try{
      result = await platform.invokeMethod('addEmail', {
        "email": email
      });

    }on PlatformException catch (e) {
      result = "Failed to Invoke: '${e.message}'.";
    }
    log(result);
  }
}




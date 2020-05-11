import 'package:flutter/material.dart';
import 'bucket_list.dart';
import 'package:flutter/services.dart';
import 'dart:developer';
import 'dart:io';


// ignore: camel_case_types
class snoozeSetting extends StatefulWidget {

  @override
  _settingScroll createState() => _settingScroll();
}

// ignore: camel_case_types
class _settingScroll extends State<snoozeSetting> {
  static const platform = MethodChannel("samples.flutter.dev/native");
  int _currentValue = 1;

  @override
  Widget build(BuildContext context) {
    //work in progress
    /*return new Scaffold(
      body: new Center(
        child: new Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            new NumberPicker.integer(
                initialValue: _currentValue,
                minValue: 0,
                maxValue: 100,
                onChanged: (newValue) =>
                    setState(() => _currentValue = newValue)),
            new Text("Current number: $_currentValue"),
          ],
        ),
      ),
      appBar: new AppBar(
        title: new Text(widget.title),
      ),
    );
  }*/
}

}
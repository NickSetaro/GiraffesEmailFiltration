import 'package:flutter/material.dart';
import 'bucket_list.dart';
import 'package:flutter/services.dart';
import 'dart:developer';
import 'dart:io';


// ignore: camel_case_types
class emailList extends StatefulWidget {

  @override
  _emailListPage createState() => _emailListPage();
}

// ignore: camel_case_types
class _emailListPage extends State<emailList> {
  static const platform = MethodChannel("samples.flutter.dev/native");
  List<dynamic> emails = <dynamic>[];

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<List>(
        future: _getEmailList(),
        // ignore: missing_return
        builder: (BuildContext context, AsyncSnapshot<List> snapshot){
          if(snapshot.hasError){
            return Card(
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Text(
                  "Email List Empty",
                  style: TextStyle(fontSize: 22.0),),
              ),
            );
          }
          else if(snapshot.hasData) {
            return new SingleChildScrollView(
              padding: new EdgeInsets.all(16.0),
              child: Text(
                snapshot.data.toString(), style: TextStyle(fontSize: 22.0),),
            );
          }
        });
  }


  Future<List> _getEmailList() async{
    final emails = await platform.invokeListMethod('getemaillist');
    log(emails[0]);
    return emails;
  }
}

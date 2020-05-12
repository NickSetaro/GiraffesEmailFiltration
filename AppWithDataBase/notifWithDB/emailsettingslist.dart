import 'package:flutter/material.dart';
import 'bucket_list.dart';
import 'package:flutter/services.dart';
import 'dart:developer';
import 'dart:io';

import 'dbms.dart';


// ignore: camel_case_types
class emailList extends StatefulWidget {

  @override
  _emailListPage createState() => _emailListPage();
}

// ignore: camel_case_types
class _emailListPage extends State<emailList> {
  final dbms = DBMS.dbms;
  static const platform = MethodChannel("samples.flutter.dev/native");


  Widget listWidget() {
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
          return ListView.separated(
            padding: const EdgeInsets.all(16.0),
            itemCount: snapshot.data.length,
            itemBuilder: (BuildContext context, int index) {
              return Container(
                height: 50,
                color: Colors.orangeAccent,
                child: Center(
                  child: Text(
                      snapshot.data[index],
                      textAlign: TextAlign.left,
                    style: TextStyle(
                        fontSize: 18
                    )
                  ),
                )
              );
            },
            separatorBuilder: (BuildContext context, int index) => const Divider(),
          );
        }
      });
    }

    @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Emails List'),
      ),
      body: listWidget(),
    );
  }

    Future<List> _getEmailList() async{
      return await dbms.getFilterAddresses();
  }
}
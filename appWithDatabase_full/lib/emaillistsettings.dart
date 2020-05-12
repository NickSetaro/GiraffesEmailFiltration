import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:mailapp4232020/dbms.dart';
import 'bucket_list.dart';
import 'package:flutter/services.dart';
import 'dart:developer';
import 'dart:io';


// ignore: camel_case_types
class Listemail extends StatefulWidget {

  @override
  listpage createState() => listpage();
}

// ignore: camel_case_types
class listpage extends State<Listemail> {
  final dbms = DBMS.dbms;
  static const platform = MethodChannel("samples.flutter.dev/native");
  List<dynamic> emails = <dynamic>[];
  final teController = TextEditingController();



  Widget build(BuildContext context) {
    _getEmailList();
    return Scaffold(
      appBar: AppBar(title: Text("Email List")),
      body: Column(
        children: <Widget>[
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Container(
              padding: const EdgeInsets.all(10),
              height: 70,
              alignment: Alignment(0, 0),
              color: Colors.orange,
              child: Text(
                "To remove an item, swipe the tile to the right or tap the trash icon.",
                style: TextStyle(color: Colors.white),
              ),
            ),
          ),
          Expanded(
            child: ListView.builder(
              itemCount: emails.length,
              itemBuilder: (context, index) {
                final item = emails[index];
                return Dismissible(
                  key: Key(item),
                  direction: DismissDirection.startToEnd,
                  child: ListTile(
                    title: Text(item),
                    trailing: IconButton(
                      icon: Icon(Icons.delete_forever),
                      onPressed: () async {
                        removeEmail(emails[index]);
                        setState(() {

                          _getEmailList();
                        });
                      },
                    ),
                  ),
                  onDismissed: (direction) async {
                    removeEmail(emails[index]);
                    setState(() {

                      _getEmailList();
                    });
                  },
                );
              },
            ),
          ),
          Divider(
            color: Colors.grey,
            height: 5,
            indent: 10,
            endIndent: 10,
          ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 10),
            child: Row(
              children: <Widget>[
                Text("Insert Email Address:"),
                Expanded(
                  child: Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    child: TextField(
                      controller: teController,
                      onSubmitted: (text) {
                        setState(() {
                          if (teController.text != "") {
                            emails.add(teController.text);
                          }
                        });
                        teController.clear();
                      },
                    ),
                  ),
                ),
                RaisedButton(
                  child: Text("Add"),
                  onPressed: () {
                    setState(() {
                      if (teController.text != "") {
                        addEmail(teController.text);
                        _getEmailList();
                      }
                    });
                    teController.clear();
                  },
                )
              ],
            ),
          ),
        ],
      ),
    );
  }


  Future<void> _getEmailList() async{
    List<String> email;

    email = await dbms.getFilterAddresses();
    setState(() {
      emails = email;
    });
  }

  Future<void> removeEmail(Map<String, dynamic> email) async{
    await dbms.deleteMessage(email[DBMS.bucketColEmail], email[DBMS.mailColDate]);
  }


  Future<void> addEmail(String email) async{
    await dbms.insertBucket(email, email);
  }



  void waitMethod() async{
    List<String> list;
    await _getEmailList();
  }
}
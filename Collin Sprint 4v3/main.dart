import 'dart:collection';
import 'dart:developer';
import 'package:flutter_slidable/flutter_slidable.dart';

import 'package:flutter/material.dart';
import 'package:flutterappv5/receiver.dart';
import 'add_bucketv1.dart';
import 'package:flutter/services.dart';
import 'bucket.dart';
import 'bucket_list.dart';
import 'settings.dart';
import 'notifications.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Rowan Email Filtration',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: LoginPage(title: 'Rowan Email Filtration'),
    );
  }
}

class LoginPage extends StatefulWidget {
  LoginPage({Key key, this.title}) : super(key: key);
  final String title;
  @override
  _LoginPageState createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {

  String sCheck = "";
  bool failedLogin = false;
  static const platform = MethodChannel("samples.flutter.dev/native");

  TextStyle style = TextStyle(fontFamily: 'Montserrat', fontSize: 20.0);
  final passwordController = TextEditingController();
  final emailController = TextEditingController();


  @override
  Widget build(BuildContext context) {
    final emailField = TextField(
      controller: emailController,
      obscureText: false,
      style: style,
      decoration: InputDecoration(
          contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
          hintText: "Email",
          errorText: isEmailAddressValid(emailController.text),

          border:
          OutlineInputBorder(borderRadius: BorderRadius.circular(32.0))),
    );

    final passwordField = TextField(
      controller: passwordController,
      obscureText: true,
      style: style,
      decoration: InputDecoration(
          contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
          hintText: "Password",
          errorText: failedLogin ? 'Email or Password is Incorrect' : null,
          border:
          OutlineInputBorder(borderRadius: BorderRadius.circular(32.0))),
    );

    final loginButton = Material(
      elevation: 5.0,
      borderRadius: BorderRadius.circular(30.0),
      color: Colors.orangeAccent,
      child: MaterialButton(
        minWidth: MediaQuery.of(context).size.width,
        padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
        onPressed: () async {

          await stringCheck(emailController.text, passwordController.text);

          if(sCheck == "true"){
            Navigator.pushReplacement(
                context, MaterialPageRoute(builder: (context) => HomePage(emailController.text, passwordController.text)));
          }else{
          }
        },
        child: Text("Login",
            textAlign: TextAlign.center,
            style: style.copyWith(
                color: Colors.brown, fontWeight: FontWeight.bold)),
      ),
    );

    return Scaffold(
      resizeToAvoidBottomInset: false,
      body: Center(
        child: Container(
          color: Colors.white,
          child: Padding(
            padding: const EdgeInsets.all(36.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                SizedBox(
                  height: 155.0,
                  child: Image.asset(
                    "assets/images/rowanlogo.png",
                    fit: BoxFit.contain,
                  ),
                ),
                SizedBox(height: 45.0),
                emailField,
                SizedBox(height: 25.0),
                passwordField,
                SizedBox(
                  height: 35.0,
                ),
                loginButton,
                SizedBox(
                  height: 15.0,
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Future<void> stringCheck(String username, String password) async {
    String result = "";
    try {
      result = await platform.invokeMethod('checklogin', <String, String>{'address': username, 'pass': password});

      if (result == "false") {
        setState(() {
          failedLogin = true;
        });
      }
    }on PlatformException catch (e) {
      result = "Failed to Invoke: '${e.message}'.";
    }
    setState(() {
      sCheck = result;
    });
  }



  String isEmailAddressValid(String email) {
    RegExp exp = new RegExp (
      r"^[a-zA-Z0-9.a-zA-Z0-9.!#$%&'*+-/=?^_`{|}~]+@[a-zA-Z0-9]+\.[a-zA-Z]+",
      caseSensitive: false,
      multiLine: false,
    );
    if(email.trim() == "")
      return null;
    if (exp.hasMatch(email.trim()) == false){
      return "Email is invalid";
    }
    return null;
  }

}

class HomePage extends StatefulWidget {
  String email;
  String password;
  HomePage(this.email, this.password);

  _HomePageState createState() => _HomePageState(email, password);
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: const Text('Rowan Email Filtration')));
  }
}

class _HomePageState extends State<HomePage> {
  BucketList buckets = new BucketList();
  static const platform = MethodChannel("samples.flutter.dev/native");
  List<String> notList = new List<String>();

  final String email;
  final String password;


  _HomePageState(this.email, this.password);
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: Text('Notifications'), actions: <Widget>[
          IconButton (
              icon: const Icon(Icons.cloud_download),
              /*
              This connects to the inbox and pull the emails over to native. Had to keep this seperate from the channel for getting the nots from native.
              Pull down on the screen to update the nots after pressing this button
               */
              onPressed: () async{
                await checkMail(email, password);
              }
          ),
          IconButton(
              icon: const Icon(Icons.settings),
              onPressed: () {
                Navigator.push(context,
                    MaterialPageRoute(builder: (context) => SettingsPage()));
              }),
        ]),

        floatingActionButton: FloatingActionButton(
          onPressed: () {
            showModalBottomSheet(
                context: context,
                builder: (BuildContext context) =>
                    AddBucket((name, address){
                       buckets.addBucket(name, address);
                       setState(() {
                      Navigator.pop(context);
                       });
                    }));
          },
          tooltip: 'Add Bucket',
          child: const Icon(Icons.add),
        ),


        body: new Container(
            child: new Center(
                child: new RefreshIndicator(
                    child: ListView.builder(
                      padding: const EdgeInsets.all(8),
                        itemCount: buckets.bucketList.length,
                      itemBuilder: (BuildContext context, int index) {
                      return new ExpansionTile(
                       title: Text(buckets.bucketList[index].name),
                       children: <Widget>[
                          new Column(
                            children: _buildExpandableNotifs(
                                buckets.bucketList[index])
                        )
                      ]
                  );
                }
            ),
                    onRefresh: _refreshPage,
                )
            )
        )
    );
  }

  _buildExpandableNotifs(Bucket bucket) {
    List<Widget> notifs = [];
    for (Notif notif in bucket.notifications)
      notifs.add(
          new ListTile(
              isThreeLine: true,
              title: new Text(notif.subject),
              subtitle: new Text(notif.dateAndBody),
              onTap: () {
                //follow url to email
              }
          )
      );
    return notifs;
  }





  Future<void> checkMail(String email, String password) async {
    String result;
    try {
      result = await platform.invokeMethod(
          'checkMail', <String, String>{'email': email, 'pass': password});

    } on PlatformException catch (e) {
      result = "Connection failed to be established";
    }
  }

  Future<void> _refreshPage() async {
    buckets.bucketList.forEach((b) async{
      List<String> list = await platform.invokeListMethod("getListNotification", {"address": b.address,});
      setState(() {
        notList = list;
        setNotList(b);
      });
    });
  }


  void setNotList(Bucket b) {
    Notif not;
    int notIndex = 0;
    for(int i = 0; i < notList.length; i++){
      if(notList[i] == "/" + notIndex.toString()){
        not = new Notif(b.address);
        not.subject = notList[i + 2];
        not.dateAndBody = notList[i + 1] + "\n" + notList[i + 3];
        b.addNotification(not);
        notIndex++;
      }
    }
  }



}


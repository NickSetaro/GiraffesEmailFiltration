import 'package:flutter/material.dart';
import 'add_bucketv1.dart';
import 'package:flutter/services.dart';
import 'bucket.dart';
import 'bucket_list.dart';
import 'settings.dart';
import 'dart:collection';
import 'notifications.dart';
import 'demobucket.dart';

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
                context, MaterialPageRoute(builder: (context) => HomePage()));
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
  @override
  _HomePageState createState() => _HomePageState();
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: const Text('Rowan Email Filtration')));
  }
}

class _HomePageState extends State<HomePage> {

  ListOfBuckets buckets = new ListOfBuckets();

  static const platform = MethodChannel("samples.flutter.dev/native");

  String email;
  String password;

  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Notifications'), actions: <Widget>[
        IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () {
              //refresh notifications
            }
        ),
       IconButton(
          icon: const Icon(Icons.settings),
          onPressed: () {
            Navigator.push(context,
                MaterialPageRoute(builder: (context) => SettingsPage()));
        }),
    ]),
      body: ListView.builder(
        padding: const EdgeInsets.all(8),
        itemCount: buckets.bucketList.length,
        itemBuilder: (BuildContext context, int index) {
          return new ExpansionTile(
            title: Text(buckets.bucketList[index].name),
            children: <Widget>[
              new Column(
                children: _buildExpandableNotifs(buckets.bucketList[index])
              )
            ]
          );
        }
      )
    );
  }

  _buildExpandableNotifs(Bucket bucket) {
    List<Widget> notifs = [];
    for(Notif notif in bucket.notifications)
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

  Future<void> getNotifications() async {
    List<String> result = new List<String>();
    try {
      result = await platform.invokeListMethod('getNotifications');
    }on PlatformException catch (e) {
      result = null;
    }
    setState(() {
      buckets = null;
    });
  }
}
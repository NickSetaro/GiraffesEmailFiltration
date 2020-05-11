import 'dart:collection';
import 'dart:developer';
//import 'package:flutter_slidable/flutter_slidable.dart';
import 'dart:math';
import 'package:android_intent/android_intent.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
//import 'package:flutterappv5/receiver.dart';
import 'package:flutter/services.dart';
import 'package:flutter_slidable/flutter_slidable.dart';
import 'bucket.dart';
import 'bucket_list.dart';
import 'settings.dart';
import 'notifications.dart';
import 'dart:io';
import 'package:url_launcher/url_launcher.dart';

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
                InkWell(
                    child: Text('Forgot Password?'),
                    onTap: () async {
                      if(await canLaunch("https://id.rowan.edu")) {
                        await launch("https://id.rowan.edu");
                      }
                    }
                )
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
  final String email;
  final String password;
  HomePage(this.email, this.password);

  _HomePageState createState() => _HomePageState(email, password);
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: const Text('Rowan Email Filtration')));
  }
}

class _HomePageState extends State<HomePage> {
  BucketList buckets = new BucketList();//final?
  static const platform = MethodChannel("samples.flutter.dev/native");
  List<String> notList = new List<String>();//final?
  List<String> keyWordList = new List<String>();

  final String email;
  final String password;
  final controllerName = TextEditingController();
  final controllerAddress = TextEditingController();
  final controllerKeyword = TextEditingController();

  FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin =
  new FlutterLocalNotificationsPlugin();
  var initializationSettingsAndroid;
  var initializationSettingsIOS;
  var initializationSettings;


  String bucketNotification(String messageText) {
    var androidPlatformChannelSpecifics = AndroidNotificationDetails(
        'channel_ID', 'channel name', 'channel description',
        importance: Importance.Max,
        priority: Priority.High,
        ticker: 'test ticker');



    var iOSChannelSpecifics = IOSNotificationDetails();
    var platformChannelSpecifics = NotificationDetails(androidPlatformChannelSpecifics, iOSChannelSpecifics);

    var buffer = new StringBuffer();
    buffer.write('from ');
    buffer.write(messageText);

    flutterLocalNotificationsPlugin.show(0, 'You have a new message', buffer.toString(), platformChannelSpecifics, payload: 'test payload');
    return null;
  }
  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    initializationSettingsAndroid =
    new AndroidInitializationSettings('app_icon');
    initializationSettingsIOS = new IOSInitializationSettings(
        onDidReceiveLocalNotification: onDidReceiveLocalNotification);
    initializationSettings = new InitializationSettings(
        initializationSettingsAndroid, initializationSettingsIOS);
    flutterLocalNotificationsPlugin.initialize(initializationSettings,
        onSelectNotification: onSelectNotification);

  }

  Future onDidReceiveLocalNotification(
      int id, String title, String body, String payload) async {
    await showDialog(
        context: context,
        builder: (BuildContext context) => CupertinoAlertDialog(
          title: Text(title),
          content: Text(body),
          actions: <Widget>[
            CupertinoDialogAction(
              isDefaultAction: true,
              child: Text('Ok'),
              onPressed: () async {
                Navigator.of(context, rootNavigator: true).pop();
                await Navigator.push(context,
                    MaterialPageRoute(builder: (context) => SecondRoute()));
              },
            )
          ],
        ));
  }

  Future onSelectNotification(String payload) async{
    /*if (payload != null){
      debugPrint('Notification payload: $payload');
    }
    await Navigator.push(context, new MaterialPageRoute(builder: (context)=> new SecondRoute()));
     */


    AndroidIntent intent = AndroidIntent(
      action: 'android.intent.action.MAIN',
      category: 'android.intent.category.APP_EMAIL',
    );
    intent.launch().catchError((e) {

    });
  }

  _HomePageState(this.email, this.password);
  Widget build(BuildContext context) {
    return MaterialApp(
      home:DefaultTabController(
        length: 3,
        child: Scaffold(
          appBar: AppBar(title: Text('Notifications'), actions: <Widget>[
            IconButton (
              icon: const Icon(Icons.refresh),
              /*
              This connects to the inbox and pull the emails over to native. Had to keep this seperate from the channel for getting the nots from native.
              Pull down on the screen to update the nots after pressing this button
               */
              onPressed: () async{
                await checkMail(email, password);
                notList.clear();
                _refreshPage();
              }
          ),
            IconButton(
              icon: const Icon(Icons.settings),
              onPressed: () {
                Navigator.push(context,
                    MaterialPageRoute(builder: (context) => SettingsPage(email)));
              }),
        ],
              bottom: TabBar(
              tabs: [
                  Tab(text: 'Notifications'),
                  Tab(text: 'Pinned'),
                  Tab(text: 'Snoozed')
              ]
          )
        ),

          floatingActionButton: FloatingActionButton(
          onPressed: () {
            showModalBottomSheet(
              context: context,
              builder: (context) =>
                  Container(
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
                                  addEmail(controllerAddress.text);
                                  Navigator.pop(context);
                                  buckets.getBucketList().add(new Bucket(controllerName.text, controllerAddress.text));
                                  setState(() {
                                    controllerName.clear();
                                    controllerAddress.clear();
                                  });
                                }
                            )
                          ],
                        ),
                      )
                  ),
            );
          },
            tooltip: 'Add Bucket',
            child: const Icon(Icons.add),
        ),

              body: TabBarView(
                children: <Widget>[
                  new Container(
                      child: new Center(
                        child: ListView.builder(
                            padding: const EdgeInsets.all(8),
                            itemCount: buckets.bucketList.length,
                            itemBuilder: (BuildContext context, int index) {
                              return Stack(
                                  children: <Widget>[
                                    Card(
                                      color: (index % 2 == 0) ? Colors.white: Colors.white60,
                                      child: ExpansionTile(
                                          title: Text(buckets.bucketList[index].name),
                                          subtitle: Text(buckets.bucketList[index].address),
                                          children: <Widget>[
                                            new SingleChildScrollView(
                                              scrollDirection: Axis.horizontal,
                                              child: Row(
                                                  children: <Widget>[
                                                    Wrap(
                                                      spacing: 5.0,
                                                      children:_buildKeywordList(buckets.bucketList[index]),
                                                    )
                                                  ]),
                                            ),
                                            new Column(
                                                children: _buildExpandableNotifs(
                                                    buckets.bucketList[index])
                                            ),
                                          ]
                                      ),
                                    ),
                                    Positioned(
                                      right: 39.0,
                                      top: 16.0,
                                      child: IconButton(
                                        onPressed: () {
                                          getKeyWords(buckets.bucketList[index].address);
                                          if(buckets.bucketList[index].keyWords.length == 5){
                                            _maxKeywordAlert();
                                          }
                                          else {
                                            _addKeyword(buckets.bucketList[index].address);
                                          }
                                        },
                                        iconSize: 28.0,
                                        icon: Icon(Icons.add),
                                      ),
                                    ),
                                  ]
                              );
                            }
                        ),
                      )
                  ),
                  new Text('Pinned'),
                  new Text('Snoozed'),
                ]
              )

          )
      )
    );
  }
  _buildKeywordList(Bucket b){
    List<Widget> keywords = [];
    for(String k in b.keyWords)
        keywords.add(
          new Chip(
            label: Text(k),
            deleteIcon: Icon(Icons.close),
            onDeleted: () {
              deleteKeyword(b.address, k);
            },
          ),
        );
    return keywords;
    }

  _buildExpandableNotifs(Bucket bucket) {
    List<Widget> notifs = [];
    for (Notif notif in bucket.notifications)
      notifs.add(
          new Column(
              children: <Widget>[
                Divider(
                  height: 20,
                  thickness: 0,
                ),
                Card(
                  elevation: 15,
                  child: ListTile(
                      title: new Text(notif.subject.substring(
                          0, min(notif.subject.length, 40))),
                      subtitle: new Text(notif.dateAndBody),
                      trailing: IconButton(
                        icon: Icon(Icons.close),
                        onPressed: () {
                          bucket.removeNot(notif.uuid);
                          setState(() {
                            build(context);
                          });
                        },
                      ),
                      onTap: () {
                        AndroidIntent intent = AndroidIntent(
                          action: 'android.intent.action.MAIN',
                          category: 'android.intent.category.APP_EMAIL',
                        );
                        intent.launch().catchError((e) {});
                      }
                  ),
                ),
              ]
          )
      );
    return notifs;
  }

  void _maxKeywordAlert(){
    showDialog(
        context: context,
      builder: (BuildContext context) {
          return AlertDialog(
            title: Text("Keyword Limit Reached"),
            content: Text("Cannot exceed 5 keywords"),
            actions: <Widget>[
              new FlatButton(
                  onPressed: () {
                    Navigator.of(context).pop();
                  },
                  child: Text("Close"),
              ),
            ]
          );
      }
    );
  }

  _addKeyword(String email){
    showModalBottomSheet(
        context: context,
        builder: (context) =>
          new Container(
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
                      'Add KeyWord',
                      textAlign: TextAlign.center,
                      style: TextStyle(
                        fontSize: 30,
                        color: Colors.lightBlueAccent,
                      ),
                    ),
                    TextField(
                      controller: controllerKeyword,
                      autofocus: true,
                      textAlign: TextAlign.center,
                      decoration: InputDecoration(
                        contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
                        hintText: "Enter KeyWord",
                         errorText: invalidKeyWord(controllerKeyword.text) ?
                         "KeyWords can not be greater than 20 characters": null,
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
                            if (invalidKeyWord(controllerKeyword.text) != true) {
                              await addKeyword(controllerKeyword.text, email);
                              Navigator.pop(context);
                              setState(() {
                                controllerKeyword.clear();
                              });
                            }
                            else{
                              FocusScope.of(context).unfocus();
                            }
                          }
                      )
                    ],
                  ),
            ),
          ),
    );
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
    List<String> list;
    buckets.bucketList.forEach((b) async{
      list = await platform.invokeListMethod("getListNotification", {"address": b.address,});
      setState(() {
        notList = list;
        setNotList(b);
        var emailName = b.address;
        if (list.isNotEmpty){
          bucketNotification (emailName);
        }
      });
    });
    list.clear();
   }


  void setNotList(Bucket b) {
    Notif not;
    var body;
    int notIndex = 0;
    b.clearNotifications();
    for(int i = 0; i < notList.length; i++){
      if(notList[i] == "/" + notIndex.toString()){
        not = new Notif(b.address);
        not.subject = notList[i + 2];
        not.dateAndBody = notList[i + 1] + "\n" + notList[i + 3];
        not.uuid = notList[i + 4];
        b.addNotification(not);
        notIndex++;
      }
    }
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
  }

  Future<void> addKeyword(String keyword, String email) async{
    String result = "";
    try{
      result = await platform.invokeMethod('addKeyword', {
        "email": email,
        "keyWord": keyword,
      });
    }on PlatformException catch (e) {
      result = "Failed to Invoke: '${e.message}'.";
    }
    setState(() {
      getKeyWords(email);
    });
  }

  Future<void> getKeyWords(String address) async{
    List<String> list;
    list = await platform.invokeListMethod("getKeywords", {
        "address": address,
      });
      setState(() {
        keyWordList = list;
        setKeyWords(address);
      });
  }

  void setKeyWords(String email){
    for(Bucket b in buckets.bucketList) {
      if(b.address == email) {
        b.keyWords = keyWordList;
      }
    }
  }

  Future<void> deleteKeyword(String address, String keyword) async{
    String result;
    result = await platform.invokeMethod("deleteKeyWord", {
      "address": address,
      "keyword": keyword,
    });
    getKeyWords(address);
  }

  bool invalidKeyWord(String keyword){
    if(keyword.length > 20)
      return true;
    else
      return false;
  }

  bool numKeywordCheck(String email){
    getKeyWords(email);
    if(keyWordList.length == 5)
      return false;
    else
      return true;
  }


}

class SecondRoute extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return Scaffold(
      appBar: AppBar(
        title: Text('AlertPage'),
      ),
      body: Center(
        child: RaisedButton(
          child: Text('Go Back'),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
      ),
    );
  }
}


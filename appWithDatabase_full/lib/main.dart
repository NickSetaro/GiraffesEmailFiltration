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
import 'package:mailapp4232020/dbms.dart';
import 'bucket.dart';
import 'bucket_list.dart';
import 'settings.dart';
import 'notifications.dart';
import 'dart:io';
import 'package:url_launcher/url_launcher.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final dbms = DBMS.dbms;
  var list = await dbms.queryUserData();
  var buckets = await dbms.queryAllBuckets();
  int numberOfBuckets = buckets.length;
  print('list length: ${list.length}');
  list.forEach((item) => print(item));
  bool userExists = (list.length > 1);
  WidgetsFlutterBinding.ensureInitialized();
  runApp(MyApp(verified: userExists, user: list, initialBuckets: numberOfBuckets,));
}

class MyApp extends StatelessWidget {
  MyApp({Key key, this.verified = false, this.user, this.initialBuckets}) : super(key: key);

  final int initialBuckets;
  final bool verified;
  final List<String> user;
  static final dbms = DBMS.dbms;
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Rowan Email Filtration',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: _startPoint(),
    );
  }
   _startPoint() {
    if(verified){
      return HomePage(user[0],user[1]);
    }else{
      return LoginPage(title: 'Rowan Email Filtration');
    }
  }
}

class LoginPage extends StatefulWidget {
  LoginPage({Key key, this.title}) : super(key: key);
  final String title;
  @override
  _LoginPageState createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  static const platform = MethodChannel("samples.flutter.dev/native");
  final dbms = DBMS.dbms;
  String sCheck = "";
  bool failedLogin = false;


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
            dbms.createUser(emailController.text, passwordController.text, '1');
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
  final int buckets;
  HomePage(this.email, this.password, {this.buckets = 0});

  _HomePageState createState() => _HomePageState(email, password, buckets);
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: const Text('Rowan Email Filtration')));
  }
}

class _HomePageState extends State<HomePage> {
  final dbms = DBMS.dbms;
  var bucketList;
  var messageList;
  var keywordList;
  BucketList buckets = new BucketList();
  static const platform = MethodChannel("samples.flutter.dev/native");
  List<String> notList = new List<String>();//final?
  List<Notif> pinned = new List<Notif>();
  List<Notif> snoozed = new List<Notif>();

  int bucketCount;
  int keywordAmount;
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
    _refreshPage();
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

  _HomePageState(this.email, this.password, this.bucketCount);


  Widget build(BuildContext context) {
    return MaterialApp(
        home: DefaultTabController(
            length: 3,
            child: Scaffold(
                appBar: AppBar(title: Text('Home Page'), actions: <Widget>[
                  IconButton (
                      icon: const Icon(Icons.refresh),
                      /*
              This connects to the inbox and pull the emails over to native. Had to keep this seperate from the channel for getting the nots from native.
              Pull down on the screen to update the nots after pressing this button
               */
                      onPressed: () {
//                        await checkMail(email, password);
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
                                          //buckets.getBucketList().add(new Bucket(controllerName.text, controllerAddress.text));
                                          dbms.insertBucket(controllerName.text, controllerAddress.text);
                                          setState(() async {
                                            await dbms.checkMail();
                                              controllerName.clear();
                                              controllerAddress.clear();
                                              _refreshPage();
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
                                              if(buckets.bucketList[index].keyWords.length == 5){
                                                _maxKeywordAlert();
                                              }
                                              else {
                                                _addKeyword(buckets.bucketList[index]);
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
                      new Column(
                          children: _buildPinnedNotifs()
                      ),
                      new Column(
                          children: _buildSnoozedNotifs()
                      )
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
            b.removeKeyword(k);
            _refreshPage();
          },
        ),
      );
    return keywords;
  }

  _launchURL(String query) async {
    var buffer = new StringBuffer();
    buffer.write('https://mail.google.com/mail/mu/mp/119/#tl/search/%27');
        buffer.write(query);
    if (await canLaunch(buffer.toString())) {
      await launch(buffer.toString());
    } else {
      throw 'Could not launch $buffer';
    }
  }

  _buildPinnedNotifs() {
    List<Widget> notifs = [];
    if(pinned.isEmpty){
      notifs.add(Text('No pinned notifications'));
    }else {
      for (Notif notif in pinned) {
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
                        subtitle: new Text('${notif.date} /n  ${notif.bodySnip}'),
                        trailing: IconButton(
                          icon: Icon(Icons.close),
                          onPressed: () {
                            notif.toggle();
                            pinned.remove(notif);
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
      }
    }
    return notifs;
  }

  _buildSnoozedNotifs() {
    List<Widget> notifs = [];
    if(snoozed.isEmpty) {
      notifs.add(Text('No snoozed notifications'));
    } else {
      for (Notif notif in snoozed) {
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
                        subtitle: new Text('${notif.date} /n  ${notif.bodySnip}'),
                        trailing: IconButton(
                          icon: Icon(Icons.close),
                          onPressed: () {
                            notif.toggle();
                            snoozed.remove(notif);
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
      }
    }
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

  _addKeyword(Bucket b){
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
                      await addKeyword(controllerKeyword.text, b.address);
                      b.addKeyword(controllerKeyword.text);
                      Navigator.pop(context);
                      setState(() {
                        controllerKeyword.clear();
                      });
                    }
                    else{
                      FocusScope.of(context).unfocus();
                    }
                    _refreshPage();
                  }
              )
            ],
          ),
        ),
      ),
    );
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
                      subtitle: new Text('${notif.date} \n ${notif.bodySnip}'),
                      trailing: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: <Widget> [
                            IconButton(
                                icon: Icon(Icons.fiber_pin),
                                onPressed: () {
                                  if(notif.pinned) {
                                    pinned.remove(notif);
                                  } else {
                                    pinned.add(notif);
                                  }
                                  notif.toggle();
                                  setState(() {

                                  });
                                },
                                color: notif.pinned ? Colors.blue: Colors.grey
                            ),
                            IconButton(
                                icon: Icon(Icons.snooze),
                                onPressed: () {
                                  _snoozeNotif(notif);
                                  bucketNotification('Something');
                                  setState(() {
                                    _snoozeNotif(notif);
                                  });
                                },
                                color: notif.snoozed ? Colors.blue: Colors.grey
                            ),
                            IconButton(
                              icon: Icon(Icons.close),
                              onPressed: () {
                                bucket.removeNot(notif);
                                //remove from database
                                removeNot(notif.address,notif.date, notif.subject);
                                setState(() {
                                  build(context);
                                });
                              },
                            ),
                          ]
                      ),
                      onTap: () {
                        _launchURL(notif.subject);
                      }
                  ),
                ),
              ]
          )
      );
    return notifs;
  }

  Notif createNotif(String address, String subject, String bodySnip, String date) {
    Notif notif = new Notif(address,subject,bodySnip,date);
    return notif;
  }

  void getKeyWord(String address) {

  }

  Future<void> getAmountKeyWords(String address) async {
    List<String> list;
    list = await dbms.keywordsByBucket(address);
    setState(() {
      keywordAmount = list.length;
    });
  }

  Future<void> addKeyword(String keyword, String email) async{
    await dbms.addKeyword(email, keyword);
    setState(() {

    });
  }

  Future<void> deleteKeyword(String address, String keyword) async{
    await dbms.removeKeyword(address, keyword);
  }

  Future<void> checkMail(String email, String password) async {
    String result;
    try {
      result = await platform.invokeMethod(
          'checkMail2', <String, String>{'email': email, 'pass': password});

    } on PlatformException catch (e) {
      result = "Connection failed to be established";
    }
  }

  Future<void> _refreshPage() async {
      await dbms.checkMail();
      var mybuckets = await dbms.queryAllBuckets();
      var mymessages = await dbms.queryAllMessages();
      var keywords = await dbms.queryAllKeywords();

    setState(() {
      bucketList = mybuckets;
      messageList = mymessages;
      bucketCount = bucketList.length;
      keywordList = keywords;
      setNotList();
    });
  }

  void setNotList() async{
    buckets = new BucketList();
    for (int i = 0; i < bucketCount; i++) {
      String name = bucketList[i][DBMS.bucketColName];
      String address = bucketList[i][DBMS.bucketColEmail];
      buckets.addBucket(name,address);
      for (Map message in messageList) {
        if (address == message[DBMS.bucketColEmail]) {
          String dateValue = (message[DBMS.mailColDate] ??= '1');
          String date = DateTime.fromMillisecondsSinceEpoch(int.parse(dateValue)).toString();
          String text = (message[DBMS.mailColMessageText] ??= 'No Message Text');
          String snip = text.substring(0, min(text.length, 40));
          String subject = (message[DBMS.mailColSubject] ??= 'No Subject');
          print('notif created for subject $subject');
          Notif notif = createNotif(message[DBMS.bucketColEmail], subject, snip, date);
          bucketNotification(subject);
          buckets.getBucketList()[i].addNotification(notif);
        }
        List<String> keywordList = await dbms.keywordsByBucket(buckets.getBucketList()[i].address);
        buckets.getBucketList()[i].setKeywords(keywordList);
      }
    }
  }

  Future<void> removeNot(String email, String date, String subject) async{
    await dbms.deleteMessage(email,date, subject);
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

  bool invalidKeyWord(String keyword){
    if(keyword.length > 20)
      return true;
    else
      return false;
  }

  _snoozeNotif(Notif notif){
    snoozed.add(notif);
    int dropdownValueHour = 1;
    int dropdownValueDay = 0;
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
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Text(
                'Choose Time to Snooze Notification',
                textAlign: TextAlign.center,
                style: TextStyle(
                  fontSize: 30,
                  color: Colors.lightBlueAccent,
                ),
              ),
              Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget> [
                    DropdownButton<int>(
                      value: dropdownValueHour,
                      onChanged: (int newValue) {
                        setState(() {
                          dropdownValueHour = newValue;
                        });
                      },
                      items: <int> [1,2,5,10,15]
                          .map<DropdownMenuItem<int>>((int value) {
                        return DropdownMenuItem<int>(
                          value: value,
                          child: Text(value.toString()),
                        );
                      }).toList(),
                    ),
                    Text('Hour(s)')
                  ]
              ),
              Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget> [
                    DropdownButton<int>(
                      value: dropdownValueDay,
                      onChanged: (int newValue) {
                        setState(() {
                          dropdownValueDay = newValue;
                        });
                      },
                      items: <int> [0,1,2,5,7,14]
                          .map<DropdownMenuItem<int>>((int value) {
                        return DropdownMenuItem<int>(
                          value: value,
                          child: Text(value.toString()),
                        );
                      }).toList(),
                    ),
                    Text('Day(s)')
                  ]
              ),
              FlatButton(
                  child: Text(
                    'Snooze',
                    style: TextStyle(color: Colors.white),
                  ),
                  color: Colors.lightBlueAccent,
                  onPressed: () {
                    notif.snoozeHours = dropdownValueHour;
                    notif.snoozeDays = dropdownValueDay;
                  }
              )
            ],
          ),
        ),
      ),
    );
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


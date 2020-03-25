import 'package:flutter/material.dart';
import 'add_bucket.dart';

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
          border:
              OutlineInputBorder(borderRadius: BorderRadius.circular(32.0))),
    );

    final loginButton = Material(
      elevation: 5.0,
      borderRadius: BorderRadius.circular(30.0),
      color: Colors.yellow,
      child: MaterialButton(
        minWidth: MediaQuery.of(context).size.width,
        padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
        onPressed: () {
          if(passwordController.text.compareTo('password')==0 && emailController.text.compareTo('setaron5@students.rowan.edu')==0) {
            Navigator.pushReplacement(
                context, MaterialPageRoute(builder: (context) => HomePage()));
          } else {

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
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Notifications') ,actions: <Widget>[
        IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () {
              Navigator.push(context,
                  MaterialPageRoute(builder: (context) => SettingsPage()));
            })
      ]),
      body: ListView(
          padding: const EdgeInsets.all(8),
          scrollDirection: Axis.vertical,
          children: <Widget>[
            Container(
                decoration: const BoxDecoration(
                  border: Border(
                    top: BorderSide(width: 1.0, color: Colors.black),
                    left: BorderSide(width: 1.0, color: Colors.black),
                    right: BorderSide(width: 1.0, color: Colors.black),
                    bottom: BorderSide(width: 1.0, color: Colors.black),
                  ),
                ),
                child: Column(children: <Widget>[
                  Text('Pinned Notifications'),
                  Divider(thickness: 3, color: Colors.black),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      Divider(thickness: 2, color: Colors.grey),
                      Text(
                        'myers@rowan.edu \nSubj:  \'Goto meeting\' \n"Hi all. Hope you are doing well."  \nSent: 03/22',
                        style: TextStyle(fontSize: 16,
                        fontWeight: FontWeight.bold
                        ),
                      ),
                      Divider(thickness: 2, color: Colors.grey),
                    ],
                  ),
                ])),
            SizedBox(height: 20, width: 20),
            Container(
                decoration: const BoxDecoration(
                  border: Border(
                    top: BorderSide(width: 1.0, color: Colors.black),
                    left: BorderSide(width: 1.0, color: Colors.black),
                    right: BorderSide(width: 1.0, color: Colors.black),
                    bottom: BorderSide(width: 1.0, color: Colors.black),
                  ),
                ),
                child: Column(children: <Widget>[
                  Text('Snoozed Notifications'),
                  Divider(thickness: 3, color: Colors.black),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      Divider(thickness: 2, color: Colors.grey),
                      Text(
                        'rowan@getrave.com \nSubj:  \'Two Confirmed Cases, Campus Closures\''
                            ' \n"Rowan Alert/Timely Warning:Today, we report that two individuals"  \nSent: 03/21',
                        style: TextStyle(fontSize: 16,
                            fontWeight: FontWeight.bold
                        ),
                      ),
                      Divider(thickness: 2, color: Colors.grey),
                      Divider(thickness: 2, color: Colors.grey),
                      Text(
                        'colomys7@students.rowan.edu \nSubj:  \'Team Meeting\''
                            ' \n"Hey guys, we have the upcomming sprint review on Wednesday"  \nSent: 03/20',
                        style: TextStyle(fontSize: 16,
                            fontWeight: FontWeight.bold
                        ),
                      ),
                      Divider(thickness: 2, color: Colors.grey),
                    ],
                  ),
                ])),
            SizedBox(height: 20, width: 20),
            Container(
                decoration: const BoxDecoration(
                  border: Border(
                    top: BorderSide(width: 1.0, color: Colors.black),
                    left: BorderSide(width: 1.0, color: Colors.black),
                    right: BorderSide(width: 1.0, color: Colors.black),
                    bottom: BorderSide(width: 1.0, color: Colors.black),
                  ),
                ),
                child: Column(children: <Widget>[
                  Text('Daily Notifications'),
                  Divider(thickness: 3, color: Colors.black),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      Divider(thickness: 2, color: Colors.grey),
                      Text(
                        'poolos@rowan.edu \nSubj:  \'	Important Information from CS Advising\''
                            ' \n"Hello, Computer Science and Computing & Informatics Students, We care deeply about your success"  \nSent: 03/24',
                        style: TextStyle(fontSize: 16,
                            fontWeight: FontWeight.bold
                        ),
                      ),
                      Divider(thickness: 2, color: Colors.grey),
                      Divider(thickness: 2, color: Colors.grey),
                      Text(
                        'IRTSupport@rowan.edu \nSubj:  \'IRT COVID-19 Updates: Blackboard maintenance for Glassboro & Webex scheduling tip\''
                            ' \n"As Information Resources & Technology responds to the University’s transition to working, teaching and learning remotely"  \nSent: 03/24',
                        style: TextStyle(fontSize: 16,
                            fontWeight: FontWeight.bold
                        ),
                      ),
                      Divider(thickness: 2, color: Colors.grey),
                    ],
                  ),
                ])),
          ]),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          showModalBottomSheet(
              context: context, builder: (BuildContext context) => AddBucket());
        },
        tooltip: 'Add Bucket',
        child: const Icon(Icons.add),
      ),
    );
  }
}

class SettingsPage extends StatefulWidget {
  @override
  _SettingsPageState createState() => _SettingsPageState();
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: const Text('Rowan Email Filtration')));
  }
}

class _SettingsPageState extends State<SettingsPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Settings')),
      body: ListView(
          padding: const EdgeInsets.all(8),
          scrollDirection: Axis.vertical,
          children: <Widget>[
            Column(children: <Widget>[Text('Setting One')]),
            Divider(color: Colors.black, thickness: 5),
            Column(children: <Widget>[Text('Setting Two')]),
          ]),
    );
  }
}

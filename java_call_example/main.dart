import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'dart:async';

void main() => runApp(new MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      title: 'Flutter Demo',
      theme: new ThemeData(
        primarySwatch: Colors.yellow,
      ),
      home: new LoginScreen(),
    );
  }
}

class LoginScreen extends StatefulWidget {
  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
/*
Fields for the login screen:
-style for text.
-decoration for input on email and password.
-controller for email and password.
 */

  final TextStyle style = TextStyle(
    fontFamily: 'Montserrat',
    fontSize: 20.0,
  );
  final InputDecoration emailDecor = InputDecoration(
      contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
      hintText: "Email",
      border: OutlineInputBorder(borderRadius: BorderRadius.circular(32.0)));
  final InputDecoration passwordDecor = InputDecoration(
      contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
      hintText: "Password",
      border: OutlineInputBorder(borderRadius: BorderRadius.circular(32.0)));
  var _email = new TextEditingController();
  var _password = new TextEditingController();

  static const platformMethodChannel =
      const MethodChannel('heartbeat.fritz.ai/native');
  String nativeMessage = 'it didn\'t work';

  //Future<Null>

  Future<void> callTest() async {
    try {
      nativeMessage = await platformMethodChannel.invokeMethod(
          "testMail", {"email": _email.text, "password": _password.text});
    } on PlatformException catch (e) {
      nativeMessage = "Can't do native stuff ${e.message}.";
    }
//    setState(() {
//
//      nativeMessage = _message;
//    }
//    );
  }



  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text("Login:"),
      ),
      body: new ListView(
        children: <Widget>[
          new ListTile(
            title: new TextField(
              controller: _email,
              obscureText: false,
              style: style,
              decoration: emailDecor,
            ),
          ),
          new ListTile(
            title: new TextField(
              controller: _password,
              obscureText: true,
              style: style,
              decoration: passwordDecor,
            ),
          ),
          new ListTile(
            title: new Material(
              elevation: 5.0,
              borderRadius: BorderRadius.circular(30.0),
              color: Colors.yellow,
              child: MaterialButton(
                minWidth: MediaQuery.of(context).size.width,
                padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
                onPressed: () {
                  callTest();
                  var route = new MaterialPageRoute(
                    builder: (BuildContext context) => new TestPage(
                      message: nativeMessage,
                    ),
                  );
                  Navigator.of(context).push(route);
                },
                child: Text("Login",
                    textAlign: TextAlign.center,
                    style: style.copyWith(
                        color: Colors.brown, fontWeight: FontWeight.bold)),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class MyHomePage extends StatefulWidget {
  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  var _textController = new TextEditingController();
  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text("Home Page"),
      ),
      body: new ListView(
        children: <Widget>[
          new ListTile(
            title: new TextField(
              controller: _textController,
            ),
          ),
          new ListTile(
            title: new RaisedButton(
                child: new Text("Next"),
                onPressed: () {
                  var route = new MaterialPageRoute(
                    builder: (BuildContext context) => new NextPage(
                      value: _textController.text,
                    ),
                  );
                  Navigator.of(context).push(route);
                }),
          ),
        ],
      ),
    );
  }
}

class MyWelcomePage extends StatefulWidget {
  final String email;
  final String password;
  MyWelcomePage({Key key, this.email, this.password}) : super(key: key);
  @override
  _MyWelcomePageState createState() => _MyWelcomePageState();
}

class _MyWelcomePageState extends State<MyWelcomePage> {
  static const platformMethodChannel =
      const MethodChannel('heartbeat.fritz.ai/native');
  String nativeMessage = '';

  Future<Null> _accessMail() async {
    String _message;
    try {
      final String result = await platformMethodChannel.invokeMethod("parTest",
          {"email": "${widget.email}", "password": "${widget.password}"});
      _message = result;
    } on PlatformException catch (e) {
      _message = "Can't do native stuff ${e.message}.";
    }
    setState(() {
      nativeMessage = _message;
    });
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text("Welcome"),
      ),
      body: new Text(nativeMessage),
    );
  }
}

class TestPage extends StatefulWidget {
  final String message;
  TestPage({Key key, this.message}) : super(key: key);
  @override
  _TestPageState createState() => _TestPageState();
}

class _TestPageState extends State<TestPage> {
  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text("Welcome"),
      ),
      body: new Text(widget.message),
    );
  }
}

class NextPage extends StatefulWidget {
  final String value;

  NextPage({Key key, this.value}) : super(key: key);
  @override
  _NextPageState createState() => _NextPageState();
}

class _NextPageState extends State<NextPage> {
  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text("Next Page"),
      ),
      body: new Text("${widget.value}"),
    );
  }
}

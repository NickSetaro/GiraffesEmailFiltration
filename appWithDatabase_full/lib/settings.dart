import 'package:flutter/material.dart';
import 'bucket_list.dart';
import 'emaillistsettings.dart';
import 'package:mailapp4232020/dbms.dart';
import 'main.dart';

class SettingsPage extends StatefulWidget {

    final String userEmail;
    SettingsPage(this.userEmail);
  _SettingsPageState createState() => _SettingsPageState(userEmail);
}

class _SettingsPageState extends State<SettingsPage> {

  bool _dark = false;
  bool _notification = true;
  bool _daily = true;
  bool _update = true;
  double _timer = 0;
  final String userEmail;
  final dbms = DBMS.dbms;

  _SettingsPageState(this.userEmail);


  Brightness _getBrightness() {
    return _dark ? Brightness.dark : Brightness.light;
  }

  @override
  Widget build(BuildContext context) {
    return Theme(
      isMaterialAppTheme: true,
      data: ThemeData(
        brightness: _getBrightness(),
      ),
      child: Scaffold(
        backgroundColor: _dark ? null : Colors.grey.shade200,
        appBar: AppBar(
          elevation: 0,
          brightness: _getBrightness(),
          iconTheme: IconThemeData(color: _dark ? Colors.white : Colors.black),
          backgroundColor: Colors.transparent,
          title: Text(
            'Settings',
            style: TextStyle(color: _dark ? Colors.white : Colors.black),
          ),
          actions: <Widget>[
            IconButton(
              icon: Icon(Icons.brightness_6),
              onPressed: () {
                setState(() {
                  _dark = !_dark;
                });
              },
            )
          ],
        ),
        body: Stack(
          fit: StackFit.expand,
          children: <Widget>[
            SingleChildScrollView(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  Card(
                    elevation: 8.0,
                    shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10.0)),
                    color: Colors.orangeAccent,
                    child: ListTile(
                      onTap: () {
                        //open edit profile
                      },
                      title: Text(
                        userEmail,
                        textAlign: TextAlign.center,
                        style: TextStyle(
                          color: Colors.white,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(height: 10.0),
                  Card(
                    elevation: 4.0,
                    margin: const EdgeInsets.fromLTRB(32.0, 8.0, 32.0, 16.0),
                    shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10.0)),
                    child: Column(
                      children: <Widget>[
                        ListTile(
                          leading: Icon(
                            Icons.email,
                            color: Colors.orangeAccent,
                          ),
                          title: Text("Edit Email List"),
                          trailing: Icon(Icons.keyboard_arrow_right),
                          onTap: () {
                            Navigator.push(
                                    context,
                                MaterialPageRoute(builder: (context) => Listemail()));
                          },
                        ),
                        _buildDivider(),
                      ],
                    ),
                  ),
                  const SizedBox(height: 20.0),
                  Text(
                    "Notification Settings",
                    style: TextStyle(
                      fontSize: 20.0,
                      fontWeight: FontWeight.bold,
                      color: Colors.indigo,
                    ),
                  ),
                  SwitchListTile(
                    activeColor: Colors.orangeAccent,
                    contentPadding: const EdgeInsets.all(0),
                    value: _notification,
                    title: Text("Receive Push Notifications"),
                    onChanged: (bool value) {
                      setState(() {
                        _notification = value;
                      });
                    },
                  ),
                  const SizedBox(height: 20.0),
                  Text(
                    "General Settings",
                    style: TextStyle(
                      fontSize: 20.0,
                      fontWeight: FontWeight.bold,
                      color: Colors.indigo,
                    ),
                  ),
                  const SizedBox(height: 15.0),
                  InkWell(
                    splashColor: Colors.orange,
                    child: Text('Log Out', style: TextStyle(fontSize: 15.0)),
                    onTap: () {
                      _showCheck();
                    }
                  )
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Container _buildDivider() {
    return Container(
      margin: const EdgeInsets.symmetric(
        horizontal: 8.0,
      ),
      width: double.infinity,
      height: 1.0,
      color: Colors.grey.shade400,
    );
  }

  void _logOut() {
    dbms.logOut();
  }

  void _showCheck() {
    showDialog(
        context: context,
        builder: (BuildContext context) {
          return AlertDialog(
              title: Text("Log Out"),
              content: Text("Are you sure you want to log out?"),
              actions: <Widget>[
                new FlatButton(
                  onPressed: () {
                    Navigator.pushReplacement(
                        context, MaterialPageRoute(builder: (context) => LoginPage()));
                  },
                  child: Text("Log Out"),
                ),
              ]
          );
        }
    );
  }
}
import 'package:flutter/material.dart';
import 'bucket_list.dart';
import 'emailsettingslist.dart';

class SettingsPage extends StatefulWidget {

  @override
  _SettingsPageState createState() => _SettingsPageState();
}

class _SettingsPageState extends State<SettingsPage> {
  bool _dark = false;
  bool _notification = true;
  bool _daily = true;
  bool _update = true;
  double _timer = 0;

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
                        "Users Email",
                        style: TextStyle(
                          color: Colors.white,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                      leading: Icon(
                        Icons.assignment_ind,
                        color: Colors.white,
                      ),
                      trailing: Icon(
                        Icons.edit,
                        color: Colors.white,
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
                            Icons.lock_outline,
                            color: Colors.orangeAccent,
                          ),
                          title: Text("Change Password"),
                          trailing: Icon(Icons.keyboard_arrow_right),
                          onTap: () {
                            //open change password
                          },
                        ),
                        _buildDivider(),
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
                                MaterialPageRoute(builder: (context) => emailList()));
                          },
                        ),
                        _buildDivider(),
                        ListTile(
                          leading: Icon(
                            Icons.access_alarm,
                            color: Colors.orangeAccent,
                          ),
                          title: Text("Notification Life Time"),
                          trailing: Icon(Icons.keyboard_arrow_right),
                          onTap: () {

                          },
                        ),
                        _buildDivider(),
                        ListTile(
                          leading: Icon(
                            Icons.snooze,
                            color: Colors.orangeAccent,
                          ),
                          title: Text("Snooze Settings"),
                          trailing: Icon(Icons.keyboard_arrow_right),
                          onTap: () {

                          },
                        ),
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
                    title: Text("Received notification"),
                    onChanged: (bool value) {
                      setState(() {
                        _notification = value;
                      });
                    },
                  ),
                  SwitchListTile(
                    activeColor: Colors.orangeAccent,
                    contentPadding: const EdgeInsets.all(0),
                    value: _daily,
                    title: Text("Daily Notifications"),
                    onChanged: (bool value) {
                      setState(() {
                        _daily = value;
                      });
                    },
                  ),
                  SwitchListTile(
                    activeColor: Colors.orangeAccent,
                    contentPadding: const EdgeInsets.all(0),
                    value: _update,
                    title: Text("Received App Updates"),
                    onChanged: (bool value) {
                      setState(() {
                        _update = value;
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
                  const SizedBox(height: 60.0),
                ],
              ),
            ),
            Positioned(
              bottom: -20,
              left: -20,
              child: Container(
                width: 80,
                height: 80,
                alignment: Alignment.center,
                decoration: BoxDecoration(
                  color: Colors.orangeAccent,
                  shape: BoxShape.circle,
                ),
              ),
            ),
            Positioned(
              bottom: 00,
              left: 00,
              child: IconButton(
                icon: const Icon(Icons.backspace),
                onPressed: () {
                  //log out
                },
              ),
            )
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
}
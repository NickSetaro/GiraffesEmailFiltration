import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class AddBucket extends StatefulWidget {
  _AddBucket createState() => _AddBucket();
}

class _AddBucket extends State<AddBucket> {
  final emailController = TextEditingController();
  static const platform = MethodChannel("samples.flutter.dev/native");


  @override
  Widget build(BuildContext context) {
    return Container(
      color: Color(0xff757575),
      child: Container(
        padding: EdgeInsets.all(20),
        decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.only(
                topRight: Radius.circular(20), topLeft: Radius.circular(20))),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: <Widget>[
            Text(
              'Add Email',
              textAlign: TextAlign.center,
              style: TextStyle(
                fontSize: 30,
                color: Colors.lightBlueAccent,
              ),
            ),
            TextField(
              autofocus: true,
              textAlign: TextAlign.center,
              controller: emailController,
              decoration: InputDecoration(
                  contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
                  hintText: "Enter Email",
                  errorText: isEmailAddressValid(emailController.text),
                  border:
                  OutlineInputBorder(
                      borderRadius: BorderRadius.circular(32.0))),
            ),
            FlatButton(
              child: Text(
                'Add',
                style: TextStyle(color: Colors.white),
              ),
              color: Colors.lightBlueAccent,
              onPressed: () {
                if (isEmailAddressValid(emailController.text) !=
                    "Email is invalid") {
                  //addEmail(emailController.text);
                  Navigator.pop(context);
                }
              },
            ),
          ],
        ),
      ),
    );
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

}





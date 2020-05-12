import 'dart:io';

import 'package:flutter/services.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';
import 'package:path_provider/path_provider.dart';

class DBMS {
  static const platform = MethodChannel("samples.flutter.dev/native");
  FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin =
  new FlutterLocalNotificationsPlugin();

  static final _dbName = "notifications.db";
  static final _dbVersion = 1;

  static final userTable = 'user';
  static final userColName = 'user_name';
  static final userColPassword = 'password';
  static final userColEmail = 'email';
  static final userColLastCheck = 'last_check';
  static final userColId = 'iduser';

  static final bucketTable = 'bucket';
  static final bucketColName = 'name';
  static final bucketColEmail = 'email';

  static final mailTable = 'mail';
  static final mailColMessageText = 'message_text';
  static final mailColMessageHtml = 'message_html';
  static final mailColSender = 'sender';
  static final mailColCCList = 'cc_list';
  static final mailColDate = 'message_date';
  static final mailColId = 'idmail';
  static final mailColSubject = 'message_subject';

  static final keywordTable = 'keyword';
  static final keywordColKey = 'keyword';
  static final keywordId = 'idkeyword';

  static final aliasTable = 'alias';
  static final aliasColAlias = 'alias';
  static final aliasColId = 'idalias';


  DBMS._privateConstructor();


  static final DBMS dbms = DBMS._privateConstructor();

  static Database _db;


  Future<void> createUser(String email, String password, String dateAsLong) async {
    var user ={
      userColLastCheck : dateAsLong,
      userColEmail : email,
      userColPassword : password,
    };
    print(user);
    try{
      _db = await dbms.database;
      await _db.insert(userTable, user);
    }
    on DatabaseException catch (e){
      print("user already exists.");
    }

  }

  Future<void> updateLastCheck() async {
    DateTime now = DateTime.now();
    await _db.update(userTable, {userColLastCheck : now.millisecondsSinceEpoch.toString()},where: "$userColId = ?", whereArgs: [1]);
  }

  Future<List<Map<String, dynamic>>> createFilter() async {
    List<String> addresses = new List<String>();
    List<Map<String, dynamic>> filter = new List<Map<String, dynamic>>();
    List<Map<String, dynamic>> emails = await _db.query(bucketTable, columns: [bucketColEmail]);
    List<Map<String, dynamic>> keywords = await _db.query(keywordTable, columns: [keywordColKey, bucketColEmail]);
    emails.forEach((row) => addresses.add(row[bucketColEmail]));
    for(String address in addresses){
      List<String> keyList = new List<String>();
      for(Map<String, dynamic> bucket in keywords){
        if(bucket[bucketColEmail] == address){
          keyList.add(bucket[keywordColKey]);
        }
      }
      Map<String, dynamic> map = new Map<String, dynamic>();
      if(keyList.length < 1){
        keyList.add('.*');
      }
      map[address] = keyList;
      filter.add(map);
    }
    print('my filter:');
    filter.forEach((row)=>print(row));
    return filter;
  }

  Future<List<String>> queryUserData() async {
    _db = await dbms.database;
    var result = await _db.query(userTable);
    print('users: \n');
    List<dynamic> myData = result;
    var myList;
    var myMap;
    if(myData.length < 1){
      myList = ['No Data'];
      return myList;
    }
    else{
      myList = myData;
    }
    myMap = myList[0];
    print('user:  $myMap');
    var email = myMap['email'];
    var pass = myMap['password'];
    var date = myMap['last_check'];
    return [email, pass, date];
  }

  Future<List<Map<String, dynamic>>> queryAllBuckets() async {
    _db = await dbms.database;
    var table = await _db.query(bucketTable);
    table.forEach((row)=> print(row));
    if(table.length < 1){
      print('No Buckets');
    }
    return table;
  }

  Future<List<Map<String, dynamic>>> queryAllKeywords() async {
    _db = await dbms.database;
    var table = await _db.query(keywordTable);
    table.forEach((row) => print(row));
    if(table.length < 1){
      print('No Keywords');
    }
    return table;
  }

  Future<List<String>> keywordsByBucket(String email) async {
    _db = await dbms.database;
    List<String> keys = new List<String>();
    var table = await _db.query(keywordTable, columns: [keywordColKey], where: '$bucketColEmail = ?', whereArgs: [email]);
    table.forEach((row) => keys.add(row[keywordColKey]));
    print('Keys for $email: \n');
    keys.forEach((key)=>print(key));
    return keys;
  }


  Future<List<String>> getFilterAddresses() async {
    _db = await dbms.database;
    List<String> addresses = new List<String>();
    var table = await queryAllAddresses();
    table.forEach((row) => addresses.add(row[bucketColEmail]));
    return addresses;
  }

  Future<List<Map<String, dynamic>>> queryAllAddresses() async {
    _db = await dbms.database;
    var table = await _db.query(bucketTable, columns: [bucketColEmail]);
    table.forEach((row) => print(row));
    if(table.length < 1){
      print('Addresses empty');
    }
    return table;
  }

  Future<void> addKeyword(String email, String keyword) async {
    _db = await dbms.database;
    List<String> myKeys = await dbms.keywordsByBucket(email);
    if(myKeys.contains('.*')){
      dbms.removeKeyword(email, '.*');
    }
    if(!myKeys.contains(keyword)){
      var values = {keywordColKey : keyword, bucketColEmail : email};
      await _db.insert(keywordTable, values);
      print('inserted $keyword into $email keywords');
    }

  }

  Future<void> deleteMessage(String email, String date) async {
    _db = await dbms.database;
    try{
      _db.delete(mailTable, where: '$mailColDate = ? AND $bucketColEmail = ?', whereArgs: [date, email]);
      print('message deleted');
    } on Exception catch (e){
      print('error deleting message');
    }

  }

  Future<void> removeKeyword(String email, String keyword) async {
    _db = await dbms.database;
    try{
      await _db.delete(keywordTable, where: '$bucketColEmail = ? AND $keywordColKey = ?', whereArgs: ['$email', '$keyword']);
      print('$keyword deleted from the keywords');
    }on DatabaseException catch (e){
      print('Error deleting $keyword');
    }


  }

  Future<List<Map<String, dynamic>>> queryAllMessages() async {
    _db = await dbms.database;
    var table = await _db.query(mailTable);
    table.forEach((row) => print(row));
    if(table.length < 1){
      print('Messages empty');
    }
    return table;
  }

  Future<void> insertMessages(List<dynamic> newMail) async{
    for(int i = 0;i < newMail.length;i += 7){

      Map<String, dynamic> mailObject = {
        mailColMessageHtml : newMail[i],
        mailColMessageText : newMail[i+1],
        mailColCCList : newMail[i+2],
        mailColDate : newMail[i+3],
        mailColSender : newMail[i+4],
        mailColSubject : newMail[i+5],
        bucketColEmail : newMail[i+6]
      };
      print(mailObject);
      await dbms.insertMessage(mailObject);
    }

  }

  Future<int> insertMessage(Map<String, dynamic> message) async{
    _db = await dbms.database;
    return await _db.insert(mailTable, message);
}

  Future<void> insertKeywords(String email, List<String> keys) async{
    _db = await dbms.database;
    for(int i = 0;i < keys.length;i++){
      await _db.insert(keywordTable, {bucketColEmail: email, keywordColKey: keys[i]});
    }

  }

  Future<void> insertBucket(String name, String email) async {
    _db = await dbms.database;
    var mybucket = {'$bucketColName' : name, '$userColId' : 1, '$bucketColEmail' : email};

    await _db.insert(bucketTable, mybucket);
    await dbms.insertKeywords(email, ['.*']);

  }

  Future<void> deleteBucket(String email) async {
    _db = await dbms.database;
    await _db.delete(bucketTable, where: '$bucketColEmail = ?', whereArgs: [email]);

  }

  Future<void> insertBucketWithKeys(String name, String email, List<String> keys) async {
    _db = await dbms.database;
    var mybucket = {'$bucketColName' : name, '$userColId' : 1, '$bucketColEmail' : email};

    await _db.insert(bucketTable, mybucket);
    await dbms.insertKeywords(email, keys);
  }

  Future<Database> get database async {
    if (_db != null) return _db;
    _db = await _initDatabase();
    return _db;
  }

  // All of the rows are returned as a list of maps, where each map is
  // a key-value list of columns.
  Future<List<Map<String, dynamic>>> queryAllRows() async {
    _db = await dbms.database;
    return await _db.query(userTable);
  }

  Future<void> checkMail() async {


    List<dynamic> list = await dbms.queryUserData();
    String email = list[0];
    String password = list[1];
    String date = list[2];
    List<Map<String, dynamic>> filter;

    try{
      filter = await dbms.createFilter();
    } on Exception catch (e){
      print(e);
    }

    var result;
    try {
      result = await platform.invokeListMethod(
          'checkMail2', {'user': email, 'password': password, 'date' : date, 'filter' : filter});
    } on PlatformException catch (e) {
      print(e.message);
    }
    List<String> newMailSenders;
    List<dynamic> myMail = result as List<dynamic>;
//    myMail.forEach((row) => newMailSenders.add(row[DBMS.bucketColEmail]));
//    newMailSenders.forEach((sender) => bucketNotification(sender));

    print('runtime type for myMail: ${myMail.runtimeType}');
    await dbms.insertMessages(myMail);
    await dbms.updateLastCheck();

    //return result;
  }

//  Future<List<Map<String, dynamic>>> getMessages(List<String> buckets) async{
//    _db = await dbms.database;
//    return await _db.rawQuery(
//          'Select $mailColMessageText '
//          'FROM $mailTable '
//          'WHERE $mailTable.$mailColSender IN '
//              '($buckets);'
//    );
//  }
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

  // this opens the database (and creates it if it doesn't exist)
  _initDatabase() async {
    Directory documentsDirectory = await getApplicationDocumentsDirectory();
    String path = join(documentsDirectory.path, _dbName);
    return await openDatabase(path,
        version: _dbVersion,
        onCreate: _onCreate);
  }

  Future _onCreate(Database db, int version) async {
    await db.execute('''
  CREATE TABLE $userTable (
  $userColId INTEGER PRIMARY KEY AUTOINCREMENT, 
  $userColEmail VARCHAR(150) NOT NULL, 
  $userColPassword VARCHAR(45) NOT NULL,
  $userColLastCheck VARCHAR(45) NOT NULL
  )
          ''');

    await db.execute('''
  CREATE TABLE $bucketTable (
  $bucketColEmail VARCHAR(100) NOT NULL PRIMARY KEY,
  $bucketColName VARCHAR(200) NOT NULL,
  $userColId INT NOT NULL REFERENCES $userTable ($userColId) ON DELETE CASCADE ON UPDATE CASCADE    
  )
          ''');

    await db.execute('''
  CREATE TABLE $mailTable (
  $mailColId INTEGER PRIMARY KEY AUTOINCREMENT,
  $mailColMessageText MEDIUMTEXT NOT NULL,
  $mailColMessageHtml MEDIUMTEXT NOT NULL,
  $mailColSubject VARCHAR(400),
  $mailColSender VARCHAR(75) NOT NULL,
  $mailColCCList VARCHAR(1000) NULL,
  $mailColDate INTEGER NOT NULL,
  $bucketColEmail VARCHAR(100) NOT NULL,
  CONSTRAINT idmail_bucket
    FOREIGN KEY ($bucketColEmail)
    REFERENCES $bucketTable ($bucketColEmail)
    ON DELETE CASCADE
    ON UPDATE CASCADE
  )
          ''');



    await db.execute('''
  CREATE TABLE $keywordTable (
  $keywordId INTEGER PRIMARY KEY AUTOINCREMENT,
  $bucketColEmail VARCHAR(100) NOT NULL,
  $keywordColKey VARCHAR(75) NOT NULL,
  CONSTRAINT idaddress_keyword
    FOREIGN KEY ($bucketColEmail)
    REFERENCES $bucketTable ($bucketColEmail)
    ON DELETE CASCADE
    ON UPDATE CASCADE
  )
          ''');

  }
}

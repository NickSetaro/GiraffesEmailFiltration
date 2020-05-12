package com.example.mailapp4232020;

import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.ContactsContract;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends FlutterActivity{

//    final private Handler mainHandler = new Handler();
//    private EmailReceiver receiver = new EmailReceiver();
//    private User user  = new User();
//    private Filter filter = new Filter();
    static final String CHANNEL = "samples.flutter.dev/native";
    public String email;

    public void configureFlutterEngine(FlutterEngine flutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler((call, result) -> {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                                if(call.method.equals("checklogin")) {
                                    MailChecker receiver = new MailChecker();
                                    String address = call.argument("address");
                                    String pass = call.argument("pass");

                                        result.success(receiver.stringCheck(address, pass));
//                                        user.setUserName(address);
//                                        user.setPassword(pass);

                                }


//                                else if(call.method.equals("addEmail")) {
//                                    email = call.argument("email");
//                                    int size = user.getFilter().emailFilter.size();
//                                    user.addAddress(email);
//                                    user.addBucket(email);
//                                    if (user.getFilter().emailFilter.size() < size) {
//                                        result.error("error", "email not added to email list", null);
//                                    } else {
//                                        result.success("Email added to list");
//                                    }
//                                }

//                                else if(call.method.equals("getemaillist")){
//                                    result.success(user.getFilter().getEmailAddresses());
//                                }

//                                else if(call.method.equals("removeEmail")){
//                                    email = call.argument("email");
//                                    user.deleteBucket(email);
//                                    user.getFilter().removeAddress(email);
//                                    int size = user.getFilter().emailFilter.size();
//                                    if(user.getFilter().emailFilter.size() >= size){
//                                        result.error("error", "Email was not removed from list", null);
//                                    }else {
//                                        result.success("Email was removed from list");
//                                    }
//                                }

//                                else if(call.method.equals("checkMail")){
//                                    String address = call.argument("email");
//                                    String pass = call.argument("pass");
//                                    try {
//                                        user.getMail();
//                                        result.success("Connection established");
//                                    } catch (IOException ex) {
//                                        result.error("error", "Connection failed to be established", null);
//                                    }
//                                }
                                else if (call.method.equals("checkMail2")) {
                                    String address = call.argument("user");
                                    String pass = call.argument("password");
                                    String date = call.argument("date");
                                    ArrayList<Map<String, List<String>>> filter = call.argument("filter");
                                    MailChecker receiver = new MailChecker(date, filter);
                                    System.out.println("address: " + address);
                                    System.out.println("pass: " + pass);
                                    System.out.println("date: " + date);
                                    System.out.println("filter: " + filter);
                                    result.success(receiver.checkEmail(address, pass));
                                }

//                                else if(call.method.equals("addBucket")){
//                                    String address = call.argument("address");
//                                    EmailBucket bucket = new EmailBucket(address);
//                                    result.success("Bucket Added");
//                                }

//                                else if(call.method.equals("getListNotification")){
//                                    String address = call.argument("address");
//                                            result.success(user.getNotifications(address));
//                                }

                        }
                    });
                });
    }


}
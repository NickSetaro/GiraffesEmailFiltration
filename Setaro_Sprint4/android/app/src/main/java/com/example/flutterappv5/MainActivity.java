package com.example.flutterappv5;

import android.os.Handler;
import android.os.StrictMode;
import android.os.Looper;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FlutterActivity{

    final private Handler mainHandler = new Handler();
    private EmailReceiver receiver = new EmailReceiver();
    private Filter filter = new Filter();
    static final String CHANNEL = "samples.flutter.dev/native";
    public String email;
    ArrayList<EmailBucket> buckets = new ArrayList<>();

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
                                    String address = call.argument("address");
                                    String pass = call.argument("pass");
                                    try {
                                        result.success(receiver.stringCheck(address, pass));
                                    } catch (IOException ex) {
                                    }
                                }

                                else if(call.method.equals("addEmail")) {
                                    String email = call.argument("address");
                                    int size = filter.emailFilter.size();
                                    filter.addAbsolute(email);
                                    if (filter.emailFilter.size() < size) {
                                        result.error("error", "email not added to email list", null);
                                    } else {
                                        result.success("Email added to list");
                                    }
                                }

                                else if(call.method.equals("getemaillist")){
                                    result.success((filter.getEmailAddresses()));
                                }

                                else if(call.method.equals("removeEmail")){
                                    email = call.argument("email");
                                    filter.removeAddress(email);
                                    int size = filter.emailFilter.size();
                                    if(filter.emailFilter.size() >= size){
                                        result.error("error", "Email was not removed from list", null);
                                    }else {
                                        result.success("Email was removed from list");
                                    }
                                }

                                else if(call.method.equals("checkMail")){
                                    String address = call.argument("email");
                                    String pass = call.argument("pass");
                                    try {
                                        buckets = EmailBucket.sortMail(buckets, receiver.checkEmail(address, pass, filter));
                                        result.success("Connection established");
                                    } catch (IOException ex) {
                                        result.error("error", "Connection failed to be established", null);
                                    }
                                }

                                else if(call.method.equals("addBucket")){
                                    String address = call.argument("address");
                                    EmailBucket bucket = new EmailBucket(address);
                                    buckets.add(bucket);
                                    result.success("Bucket Added");
                                }

                                else if(call.method.equals("getNotifications")){
                                    String address = call.argument("address");
                                    for(EmailBucket b: buckets){
                                        if(b.getBucketName().equals(address)){
                                            result.success(b.messageMap());

                                        }else{
                                            result.error("error", "Bucket not found", null);
                                        }
                                    }
                                }

                                else if(call.method.equals("getLifeTime")) {
                                    //put lifetime int in appropriate place
                                    //something = call.argument(key: "lifeTime");
                                    result.success(null);
                                }

                                else if(call.method.equals("reorder")) {
                                    //put oldIndex and newIndex in appropriate place to reorder notifications
                                    //something = call.argument(key "oldIndex, key: "newIndex");
                                    result.success(null);
                                }

                        }
                    });
                });
    }



}
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


public class MainActivity extends FlutterActivity{

    final private Handler mainHandler = new Handler();
    private EmailReceiver receiver = new EmailReceiver();
    private Filter filter = new Filter();
    static final String CHANNEL = "samples.flutter.dev/native";

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

                                else if(call.method.equals("getLifeTime")) {
                                    //put lifetime int in appropriate place
                                    //something = call.argument(key lifeTime);
                                    result.success(null);
                                }

                        }
                    });
                });
    }



}
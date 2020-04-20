package com.example.flutterappv5;


import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugins.GeneratedPluginRegistrant;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;

import java.io.IOException;

public class DartHandler {

    final private Handler mainHandler = new Handler();
    private EmailReceiver receiver = new EmailReceiver();
    private Filter filter = new Filter();
    static final String CHANNEL = "com.flutter.test/native";


    public void MethodChannel(FlutterEngine flutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler((call, result) -> {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            switch (call.method) {

                                case "loginCheck":
                                    String address = call.argument("address");
                                    String pass = call.argument("pass");
                                    try {
                                        result.success(receiver.stringCheck(address, pass));
                                    } catch (IOException ex) {
                                    }

                                case "addEmail":
                                    String email = call.argument("email");
                                        filter.addAbsolute(email);



                            }
                        }
                    });
                });
    }
}

package com.example.flutterappv5;

import android.os.Handler;
import android.os.StrictMode;

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
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            switch (call.method) {

                                case "checklogin":
                                    String address = call.argument("address");
                                    String pass = call.argument("pass");
                                    try {
                                        result.success(receiver.stringCheck(address, pass));
                                    } catch (IOException ex) {
                                    }

                                case "addBucket":
                            }
                        }
                    });
                });
    }



}
package com.example.mailapp;

import androidx.annotation.NonNull;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {
  private static final String CHANNEL = "heartbeat.fritz.ai/native";
  private Trial trial = new Trial();
  private User user;

  @Override
  public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
    GeneratedPluginRegistrant.registerWith(flutterEngine);

    new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
            .setMethodCallHandler((call, result) -> {
              if (call.method.equals("parTest")) {
                result.success(trial.parTest(call.argument("email"),call.argument("password")));
              }
              if (call.method.equals("testMail")) {
                  try {
                      user = new User(call.argument("email"), call.argument("password"));
                      if(user.getMail())
                      {
                          result.success("get mail successful");
                      }
                      else {
                          result.success(user.getFileName() + "\nemail: " + call.argument("email"));
                      }
                  }
                  catch (Exception e)
                  {
                      result.success("didn't get it");
                      e.printStackTrace();
                  }
              }
            }
            );

  }
}

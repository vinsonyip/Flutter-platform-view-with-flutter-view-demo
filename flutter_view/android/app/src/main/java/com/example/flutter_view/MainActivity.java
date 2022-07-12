package com.example.flutter_view;

import android.content.Context;
import android.content.Intent;
import io.flutter.embedding.engine.FlutterEngine;


import androidx.annotation.NonNull;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;


public class MainActivity extends FlutterActivity {

    private static final String CHANNEL = "samples.flutter.io/platform_view";
    private static final String METHOD_SWITCH_VIEW = "switchView";
    private static final int COUNT_REQUEST = 1;

    private MethodChannel.Result result;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        new MethodChannel(flutterEngine.getDartExecutor(), CHANNEL).setMethodCallHandler(
            new MethodChannel.MethodCallHandler() {
                @Override
                public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
                    MainActivity.this.result = result;
                    int count = methodCall.arguments();
                    System.out.println("======== Intent count:" + count);
                    if (methodCall.method.equals(METHOD_SWITCH_VIEW)) {
                        onLaunchFullScreen(count);
                    } else {
                        result.notImplemented();
                    }
                }
            }
        );
    }

    private void onLaunchFullScreen(int count) {
        Intent fullScreenIntent = new Intent(this, AndroidNativeActivity.class);
        fullScreenIntent.putExtra(AndroidNativeActivity.EXTRA_COUNTER, count);
        startActivityForResult(fullScreenIntent, COUNT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COUNT_REQUEST) {
            if (resultCode == RESULT_OK) {
//        print("--------------- ---------------");
                int intentRet = data.getIntExtra(AndroidNativeActivity.EXTRA_COUNTER, 0);
                result.success(intentRet);
                System.out.println(intentRet);
//        print(intentRet);
            } else {
                result.error("ACTIVITY_FAILURE", "Failed while launching activity", null);
            }
        }
    }
}

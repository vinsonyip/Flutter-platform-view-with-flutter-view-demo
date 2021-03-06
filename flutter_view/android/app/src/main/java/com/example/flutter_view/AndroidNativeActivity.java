package com.example.flutter_view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import io.flutter.embedding.android.FlutterView;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.engine.dart.DartExecutor.DartEntrypoint;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.BasicMessageChannel.MessageHandler;
import io.flutter.plugin.common.BasicMessageChannel.Reply;
import io.flutter.plugin.common.StringCodec;
import java.util.ArrayList;

public class AndroidNativeActivity extends AppCompatActivity {
    public static final String EXTRA_COUNTER = "counter";
    private static FlutterEngine flutterEngine;

    private FlutterView flutterView;
    private int counter = 0;
    private static final String CHANNEL = "increment";
    private static final String EMPTY_MESSAGE = "";
    private static final String PING = "ping";
    private BasicMessageChannel<String> messageChannel;

    private String[] getArgsFromIntent(Intent intent) {
        // Before adding more entries to this list, consider that arbitrary
        // Android applications can generate intents with extra data and that
        // there are many security-sensitive args in the binary.
        ArrayList<String> args = new ArrayList<>();
        if (intent.getBooleanExtra("trace-startup", false)) {
            args.add("--trace-startup");
        }
        if (intent.getBooleanExtra("start-paused", false)) {
            args.add("--start-paused");
        }
        if (intent.getBooleanExtra("enable-dart-profiling", false)) {
            args.add("--enable-dart-profiling");
        }
        if (!args.isEmpty()) {
            String[] argsArray = new String[args.size()];
            return args.toArray(argsArray);
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] args = getArgsFromIntent(getIntent());

        if (flutterEngine == null) {
            flutterEngine = new FlutterEngine(this, args);
            flutterEngine.getDartExecutor().executeDartEntrypoint(
                    DartEntrypoint.createDefault()
            );
        }
        setContentView(R.layout.flutter_view_layout);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }

        counter = getIntent().getIntExtra(EXTRA_COUNTER, 0);
        updateText();


        flutterView = findViewById(R.id.flutter_view);
        flutterView.attachToFlutterEngine(flutterEngine);

        messageChannel = new BasicMessageChannel<>(flutterEngine.getDartExecutor(), CHANNEL, StringCodec.INSTANCE);
        messageChannel.
                setMessageHandler(new MessageHandler<String>() {
                    @Override
                    public void onMessage(String s, Reply<String> reply) {
                        onFlutterIncrement();
                        reply.reply(EMPTY_MESSAGE);
                    }
                });

        FloatingActionButton fab = findViewById(R.id.button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAndroidIncrement();

            }
        });

        Button switchBackToFlutterBtn = findViewById(R.id.switchBackToFlutterBtn);
        switchBackToFlutterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                returnToFlutterView();
            }
        });

    }

    private void updateText() {
        TextView textView = findViewById(R.id.button_tap);
        String value = "Flutter button tapped " + counter + (counter == 1 ? " time" : " times");
        textView.setText(value);
    }

    private void returnToFlutterView() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_COUNTER, counter);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void sendAndroidIncrement() {
        messageChannel.send(PING);
    }

    private void onFlutterIncrement() {
        counter++;
        TextView textView = findViewById(R.id.button_tap);
        String value = "Flutter button tapped " + counter + (counter == 1 ? " time" : " times");
        textView.setText(value);
    }

    @Override
    protected void onResume() {
        super.onResume();
        flutterEngine.getLifecycleChannel().appIsResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        flutterEngine.getLifecycleChannel().appIsInactive();
    }

    @Override
    protected void onStop() {
        super.onStop();
        flutterEngine.getLifecycleChannel().appIsPaused();
    }

    @Override
    protected void onDestroy() {
        flutterView.detachFromFlutterEngine();
        super.onDestroy();
    }
}


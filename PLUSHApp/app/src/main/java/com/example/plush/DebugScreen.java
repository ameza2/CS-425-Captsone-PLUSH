package com.example.plush;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plush.data.DataApplication;

public class DebugScreen extends AppCompatActivity {

    DataApplication application;

    TextView ipText;
    TextView messageText;
    Button connectButton;
    Button sendButton;
    Button backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Create Page //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_main);

        application = (DataApplication) getApplication();
        ipText = findViewById(R.id.debugTextIP);
        messageText = findViewById(R.id.debugTextMessage);
        connectButton = findViewById(R.id.debugButtonConnect);
        sendButton = findViewById(R.id.debugButtonSend);
        backButton = findViewById(R.id.debugButtonBack);

        connectButton.setOnClickListener(new View.OnClickListener() { // execute on staff button click
            public void onClick(View v) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() { // execute on staff button click
            public void onClick(View v) {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() { // execute on staff button click
            public void onClick(View v) {
                Intent intent = new Intent(DebugScreen.this, SelectAccountScreen.class);
                startActivity(intent);
            }
        });
    }
}

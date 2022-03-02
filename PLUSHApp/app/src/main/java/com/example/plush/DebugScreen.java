package com.example.plush;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

        // Stuff copy and pasted from website: https://iotdesignpro.com/projects/create-android-app-with-android-studio-to-control-led-over-wifi-using-nodemcu
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       android.text.Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart)
                            + source.subSequence(start, end)
                            + destTxt.substring(dend);
                    if (!resultingTxt
                            .matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i = 0; i < splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }
        };
        ipText.setFilters(filters);
        WifiManager wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()){
            connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataApplication.connectedThread2.send(1, ipText.getText().toString());
                }
            });
        }else{
            //Toast.makeText(WifiActivityDevices.this, "Turned on your wifi",Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
    }
}

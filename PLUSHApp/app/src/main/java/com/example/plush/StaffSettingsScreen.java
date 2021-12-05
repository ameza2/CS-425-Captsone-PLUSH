package com.example.plush;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StaffSettingsScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_settings_screen);


        Button softwareDetailsButton;

        softwareDetailsButton = (Button) findViewById(R.id.SoftwareDetailsButtonID);
        softwareDetailsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StaffSettingsScreen.this, staff_SoftwareDetailsScreen.class);
                startActivity(intent);
            }
        });

    }
}
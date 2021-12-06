package com.example.plush;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StaffSupportScreen extends AppCompatActivity {

    Button supportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_support_screen);

        supportButton = findViewById(R.id.submitButton);
        supportButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(StaffSupportScreen.this, StaffHomeScreen.class);
                startActivity(intent);
            }
        });
    }

}
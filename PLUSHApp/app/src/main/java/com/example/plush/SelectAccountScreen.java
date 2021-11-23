package com.example.plush;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectAccountScreen extends AppCompatActivity {

    Button StaffButton;
    Button PatientButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        StaffButton = (Button)findViewById(R.id.GoToStaff);
        PatientButton = (Button)findViewById(R.id.GoToPatient);

        StaffButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(SelectAccountScreen.this, StaffLoginScreen.class);
                startActivity(intent);
            }
        });
        PatientButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(SelectAccountScreen.this, PatientLoginScreen.class);
                startActivity(intent);
            }
        });
    }
}
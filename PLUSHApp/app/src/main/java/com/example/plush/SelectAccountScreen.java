package com.example.plush;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectAccountScreen extends AppCompatActivity {

    Button StaffButton; // the staff button
    Button PatientButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // must be id from layout


        StaffButton = (Button)findViewById(R.id.GoToStaff); // get staff button
        PatientButton = (Button)findViewById(R.id.GoToPatient);

        StaffButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(SelectAccountScreen.this, StaffLoginScreen.class);
                // in here, can have button that changes color, for ex
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
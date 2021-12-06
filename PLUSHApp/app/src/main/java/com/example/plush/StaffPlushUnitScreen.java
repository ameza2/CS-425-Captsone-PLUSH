package com.example.plush;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.plush.data.DataApplication;


public class StaffPlushUnitScreen extends AppCompatActivity {
    TextView roomNum;
    TextView unitID;
    DataApplication thisApplication;
    Button editButton;
    Button scheduleButton;
    Button musicButton;
    Button shutdownButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_plush_unit_screen);

        //Initializations
        roomNum = (TextView) findViewById (R.id.roomNum);
        unitID = (TextView) findViewById (R.id.unitID);
        editButton = (Button) findViewById (R.id.editButton);
        scheduleButton = (Button) findViewById (R.id.scheduleButton);
        musicButton = (Button) findViewById (R.id.musicButton);
        shutdownButton = (Button) findViewById (R.id.shutdownButton);
        thisApplication = (DataApplication) getApplication();


        //Pass the plush ID and room number from the home screen
        roomNum.setText("temp");
        unitID.setText("temp");


        //Buttons on click
        editButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(StaffPlushUnitScreen.this, StaffAddUnitScreen.class);
                startActivity(intent);
            }
        });
        scheduleButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(StaffPlushUnitScreen.this, StaffAddUnitScreen.class);
                startActivity(intent);
            }
        });
        musicButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(StaffPlushUnitScreen.this, StaffMusicScreen.class);
                startActivity(intent);
            }
        });
        shutdownButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(StaffPlushUnitScreen.this, StaffAddUnitScreen.class);
                startActivity(intent);
            }
        });




    }
}
package com.example.plush;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plush.data.DataApplication;

public class StaffAddUnitScreen extends AppCompatActivity {

    EditText IDEditText;
    EditText RoomEditText;
    Button AddUnitButton;

    DataApplication thisApplication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_add_unit_screen);

        IDEditText = (EditText) findViewById(R.id.editUnitID);
        RoomEditText = (EditText) findViewById(R.id.editRoomNumber);
        AddUnitButton = (Button)findViewById(R.id.buttonAddUnit);

        thisApplication = (DataApplication)getApplication();

        AddUnitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                thisApplication.currUserData().addUnit(IDEditText.getText().toString(), RoomEditText.getText().toString());
                thisApplication.updateJSON();
                Intent intent = new Intent(StaffAddUnitScreen.this, StaffHomeScreen.class);
                startActivity(intent);
            }
        });
    }
}
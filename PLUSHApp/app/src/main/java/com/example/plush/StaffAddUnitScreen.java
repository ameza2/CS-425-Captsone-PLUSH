package com.example.plush;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plush.data.DataApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.AccessMode;
import java.nio.file.Files;
import java.nio.file.Paths;

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

                /*/ Update the JSON file
                /try {
                    JSONArray inputJSONArray = thisApplication.inputJSON.getJSONArray("userlist");
                    for(int i = 0; i < inputJSONArray.length(); i++){
                        if(inputJSONArray.getJSONObject(i).getString("username").equals(thisApplication.currentUser)){
                            JSONArray unitJSONArray = inputJSONArray.getJSONObject(i).getJSONArray("units");
                            JSONObject toPut = new JSONObject();
                            toPut.put("id", IDEditText.getText().toString());
                            toPut.put("room", RoomEditText.getText().toString());
                            unitJSONArray.put(toPut);
                            thisApplication.getFilesDir();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                 */

                Intent intent = new Intent(StaffAddUnitScreen.this, StaffHomeScreen.class);
                startActivity(intent);
            }
        });
    }
}
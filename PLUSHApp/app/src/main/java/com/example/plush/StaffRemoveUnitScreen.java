package com.example.plush;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plush.R;
import com.example.plush.data.DataApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class StaffRemoveUnitScreen extends AppCompatActivity {

    ScrollView unitListScrollView;
    Button RemoveButton;

    DataApplication thisApplication;

    private ArrayList<Button> buttonList;
    private ArrayList<String> buttonIDList;
    private ArrayList<Boolean> buttonPressedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_remove_unit_screen);

        unitListScrollView = (ScrollView)findViewById(R.id.scrollviewremoval);
        RemoveButton = (Button)findViewById(R.id.removeUnitConfirmButton);

        thisApplication = (DataApplication)getApplication();

        // Load the buttons

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(params);

        buttonList = new ArrayList<>();
        buttonIDList = new ArrayList<>();
        buttonPressedList = new ArrayList<>();
        int j = 0;

        for(String i: thisApplication.currUserData().assignedUnits.keySet()) {
            String rn = thisApplication.currUserData().assignedUnits.get(i).room;
            String un = thisApplication.currUserData().assignedUnits.get(i).id;


            buttonList.add(new Button(this));
            Button button = buttonList.get(j);
            button.setText("ROOM " + rn + "\nPLUSH #" + un);
            button.setTextSize(1, 30);
            button.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            button.setLayoutParams(params);
            linearLayout.addView(button);

            buttonIDList.add(i);
            buttonPressedList.add(false);

            button.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    int buttonID = getButtonID(button);
                    if(buttonPressedList.get(buttonID)){ // If it is already selected
                        button.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                        buttonPressedList.set(buttonID, false);
                    }
                    else{ // If it isn't
                        button.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                        buttonPressedList.set(buttonID, true);
                    }

                }
            });

            j++;
        }

        unitListScrollView.addView(linearLayout);

        RemoveButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                // Modify JSON
                try{
                    JSONArray inputJSONArray = thisApplication.inputJSON.getJSONArray("userlist");
                    for(int i = 0; i < inputJSONArray.length(); i++){
                        if(inputJSONArray.getJSONObject(i).getString("username").equals(thisApplication.currentUser)){

                            JSONArray unitJSONArray = inputJSONArray.getJSONObject(i).getJSONArray("units");
                            ArrayList<Integer> jsonRemoval = new ArrayList<>();
                            for(int j = 0; j < unitJSONArray.length(); j++){
                                int ind = buttonIDList.indexOf(unitJSONArray.getJSONObject(i).getString("id"));
                                if(ind != -1){
                                    thisApplication.currUserData().removeUnit(buttonIDList.get(ind));
                                    jsonRemoval.add(j);
                                }
                            }

                            //for(int j = 0; j < jsonRemoval.size(); j++){
                             //   unitJSONArray.remove(jsonRemoval.get(j));
                            //}

                        }
                    }

                    // Save new string
                    File f = new File(thisApplication.getFilesDir(), "userdatabase.json");
                    OutputStream outputStream = new FileOutputStream(f);
                    byte outputBytes[] = thisApplication.inputJSON.toString().getBytes(StandardCharsets.UTF_8);
                    outputStream.write(outputBytes);
                    outputStream.close();

                } catch (JSONException | FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(StaffRemoveUnitScreen.this, StaffHomeScreen.class);
                startActivity(intent);
            }
        });

    }


    private int getButtonID(Button button){
        for (int i = 0; i < buttonList.size(); i++){
            if(buttonList.get(i) == button){
                return i;
            }
        }
        return -1;
    }
}
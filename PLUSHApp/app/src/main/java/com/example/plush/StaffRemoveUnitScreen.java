// Utility Package //
package com.example.plush;

// Libraries //
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

/* Android Widgets */
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.plush.R;
import com.example.plush.data.DataApplication;

/* JSON Objects */
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* File Manipulation */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class StaffRemoveUnitScreen extends AppPLUSHActivity { // StaffRemoveUnitScreen w/ action activities

    ScrollView unitListScrollView; // scrollview variable: used to scroll through PLUSH unit list
    Button RemoveButton; // button variable: remove button (initiate PLUSH unit removal)


    private ArrayList<Button> buttonList; // array consisting of buttons
    private ArrayList<String> buttonIDList; // array consisting of button IDs
    private ArrayList<Boolean> buttonPressedList; // array consisting of buttons pressed

    /* Initialize Page Activity (Staff Remove Unit Screen) */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_remove_unit_screen);

        unitListScrollView = (ScrollView)findViewById(R.id.scrollviewremoval);
        RemoveButton = (Button)findViewById(R.id.removeUnitConfirmButton);


        /* Load the Buttons */

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(params);

        buttonList = new ArrayList<>();
        buttonIDList = new ArrayList<>();
        buttonPressedList = new ArrayList<>();

        int j = 0;

        for (String i: thisApplication.currUserData().assignedUnits.keySet()) {
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

            /* PLUSH Unit Removal Button Color: white = selected button, red = not selected button */
            button.setOnClickListener(new View.OnClickListener() {
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

        /* Remove Button: remove PLUSH unit from user's database entry */
        RemoveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /* Modify JSON File (Database) */
                try {

                    /* Fetch the correct user/unit array */
                    JSONArray inJSONArray = thisApplication.inputJSON.getJSONArray("userlist");
                    JSONArray unitJSONArray = null;
                    for (int l = 0; l < inJSONArray.length(); l++) {
                        if(inJSONArray.getJSONObject(l).getString("username").equals(thisApplication.currentUser)) {
                            unitJSONArray = inJSONArray.getJSONObject(l).getJSONArray("units");
                        }
                    }

                    for (int i = 0; i < buttonList.size(); i++) {
                        /* For each button, check if the button is pressed */
                        if(buttonPressedList.get(i)) {
                            String toremove = buttonIDList.get(i);
                            for(int j = 0; j < unitJSONArray.length(); j++) {
                                if(unitJSONArray.getJSONObject(j).getString("id").equals(toremove)) {
                                    unitJSONArray.remove(j);
                                }
                            }
                            thisApplication.currUserData().removeUnit(toremove);
                        }
                    }

                    /* Save new string */
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

                /* After PLUSH Unit removal, return to PLUSH Home Screen */
                Intent intent = new Intent(StaffRemoveUnitScreen.this, StaffHomeScreen.class);
                startActivity(intent); // page redirect (StaffHomeScreen)
            }
        });

    }

    /* Helper Function: fetch current button ID */
    private int getButtonID(Button button) {
        for (int i = 0; i < buttonList.size(); i++) {
            if(buttonList.get(i) == button) {
                return i;
            }
        }
        return -1;
    }
}
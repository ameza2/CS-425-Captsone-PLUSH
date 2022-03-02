// Utility Package //
package com.example.plush;

// Libraries //
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/* Android Widgets */
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/* Data Application File */
import com.example.plush.data.DataApplication;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class StaffPlushUnitScreen extends AppCompatActivity { // StaffPlushUnitScreen w/ action activities
    TextView roomNum; // textview variable: used to store patient room number from PLUSH instance
    TextView unitID; // textview variable: used to store PLUSH PID from PLUSH instance
    DataApplication thisApplication; // data application: file manipulation
    Button editButton; // button variable: edit button (view/modify patient information)
    Button scheduleButton; // button variable: schedule button (view upcoming events w/ calendar feature)
    Button musicButton; // button variable: music button (configure music settings)
    Button shutdownButton; // button variable: shutdown button (deactivate PLUSH unit and prompt alert message)
    Button backButton; // button variable: sends users back to the unit page
    SeekBar sensitivityBar; // seekbar variable: used to configure hug sensitivity
    TextView sensitivityText; // textview variable: used to display hug sensitivity

    int sensitivity; // variable: used to store hug sensitivity

    /* Initialize Page Activity (Staff PLUSH Unit Screen) */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_plush_unit_screen);

        /* Initializations */
        roomNum = (TextView) findViewById (R.id.roomNum);
        unitID = (TextView) findViewById (R.id.unitID);
        editButton = (Button) findViewById (R.id.editButton);
        scheduleButton = (Button) findViewById (R.id.scheduleButton);
        musicButton = (Button) findViewById (R.id.musicButton);
        shutdownButton = (Button) findViewById (R.id.shutdownButton);
        backButton = (Button) findViewById (R.id.backtounitbutton);
        sensitivityBar = findViewById(R.id.sensitivityBar);
        sensitivityText = findViewById(R.id.sensitivityText);
        thisApplication = (DataApplication) getApplication();


        /* Pass the plush ID and room number from the home screen */
        roomNum.setText("Room " + thisApplication.currUnitData().room);
        unitID.setText("Unit #" + thisApplication.currUnitData().id);

        /* Pass the currently set sensitivity */
        sensitivityText.setText("Hug Sensitivity: " + String.valueOf(thisApplication.currUnitData().hugSensitivity));
        sensitivityBar.setProgress(thisApplication.currUnitData().hugSensitivity);

        /* Hug Sensitivity Bar: used to calibrate PLUSH hug sensitivity */
        sensitivityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sensitivityText.setText("Hug Sensitivity: " + String.valueOf(progress)); // print hug sensitivity value
                sensitivity = progress; // store hug sensitivity value

                thisApplication.currUnitData().hugSensitivity = progress;
                try {
                    JSONArray inputJSONArray = thisApplication.inputJSON.getJSONArray("userlist");
                    for (int i = 0; i < inputJSONArray.length(); i++) {
                        if (inputJSONArray.getJSONObject(i).getString("username").equals(thisApplication.currentUser)) {

                            /* Edit unit properties */
                            JSONArray unitJSONArray = inputJSONArray.getJSONObject(i).getJSONArray("units");
                            for(int j = 0; j < unitJSONArray.length(); j++){
                                if(unitJSONArray.getJSONObject(j).getString("id").equals(thisApplication.currentUnit)){
                                    unitJSONArray.getJSONObject(j).put("hugSensitivity", progress);
                                }
                            }

                            /* Save new string to user database */
                            File f = new File(thisApplication.getFilesDir(), "userdatabase.json");
                            OutputStream outputStream = new FileOutputStream(f);
                            byte outputBytes[] = thisApplication.inputJSON.toString().getBytes(StandardCharsets.UTF_8);
                            outputStream.write(outputBytes);
                            outputStream.close();
                        }
                    }

                    DataApplication.connectedThread2.send(100 + progress, thisApplication.currentUnit);

                } catch (JSONException | FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // DO NOTHING //
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // DO NOTHING //
            }
        });

        /* Edit Button: used to view/configure patient details */
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StaffPlushUnitScreen.this, StaffAddUnitScreen.class);
                startActivity(intent); // redirect page (StaffAddUnitScreen [temp])
            }
        });

        /* Schedule Button: used to view upcoming patient events via a calendar */
        scheduleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StaffPlushUnitScreen.this, StaffScheduleScreen.class);
                startActivity(intent); // redirect page (StaffScheduleScreen)
            }
        });

        /* Music Button: used to configure PLUSH music preferences */
        musicButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StaffPlushUnitScreen.this, StaffMusicScreen.class);
                startActivity(intent); // redirect page (StaffMusicScreen)
            }
        });

        /* Shutdown Button: abort PLUSH instructions and shutdown PLUSH unit */
        shutdownButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "PLUSH #" + thisApplication.currUnitData().id + " has been deactivated!", Toast.LENGTH_SHORT).show(); // deactivation prompt
            }
        });

        /* Back Button: return to PLUSH Home Page */
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                thisApplication.currentUnit = "";
                Intent intent = new Intent(StaffPlushUnitScreen.this, StaffHomeScreen.class);
                startActivity(intent); // page redirect (StaffHomeScreen)
            }
        });
    }

    /* Add Units After Pressing Back */
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        thisApplication.currentUnit = "";
    }
}
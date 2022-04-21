// Utility Package //
package com.example.plush;

// Libraries //
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

/* Android Widgets */
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

/* Data Application File */
import com.example.plush.data.DataApplication;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class PatientPlushHomeScreen extends AppPLUSHActivity {
    ScrollView unitListScrollView;

    TextView roomNum; // textview variable: used to store patient room number from PLUSH instance
    TextView unitID; // textview variable: used to store PLUSH PID from PLUSH instance
    Button musicButton; // button variable: music button (configure music settings)
    SeekBar sensitivityBar; // seekbar variable: used to configure hug sensitivity
    TextView sensitivityText; // textview variable: used to display hug sensitivity
    int sensitivity;

    /* Initialize Page Activity (Staff PLUSH Unit Screen) */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_plush_home_screen);

        /* Initializations */
        roomNum = (TextView) findViewById (R.id.roomNum);
        unitID = (TextView) findViewById (R.id.unitID);
        musicButton = (Button) findViewById (R.id.musicButton);
        sensitivityBar = findViewById(R.id.sensitivityBar);
        sensitivityText = findViewById(R.id.sensitivityText);


        /* Pass the plush ID and room number from the home screen */
        roomNum.setText("Room " + thisApplication.currUnitData().room);
        unitID.setText("Unit #" + thisApplication.currUnitData().id);

        /* Hug Sensitivity Bar: used to calibrate PLUSH hug sensitivity */
        sensitivityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    updateHugSensitivity(progress);
                    sensitivityText.setText("Hug Sensitivity: " + String.valueOf(progress)); // print hug sensitivity value
                    sensitivity = progress; // store hug sensitivity value
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

        /* Music Button: used to configure PLUSH music preferences */
        musicButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PatientPlushHomeScreen.this, PatientMusicScreen.class);
                startActivity(intent); // redirect page (PatientMusicScreen)
            }
        });
    }

    // Back button should redirect to login screen
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(PatientPlushHomeScreen.this, PatientLoginScreen.class);
        startActivity(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
        sensitivityText.setText("Hug Sensitivity: " + String.valueOf(thisApplication.currUnitData().hugSensitivity));
        sensitivityBar.setProgress(thisApplication.currUnitData().hugSensitivity);
    }

    public void updateHugSensitivity(int h){
        thisApplication.currUnitData().hugSensitivity = h;
        try {
            JSONArray inputJSONArray = thisApplication.inputJSON.getJSONArray("userlist");
            for (int i = 0; i < inputJSONArray.length(); i++) {
                if (inputJSONArray.getJSONObject(i).getString("username").equals(thisApplication.currentUser)) {

                    /* Edit unit properties */
                    JSONArray unitJSONArray = inputJSONArray.getJSONObject(i).getJSONArray("units");
                    for(int j = 0; j < unitJSONArray.length(); j++){
                        if(unitJSONArray.getJSONObject(j).getString("id").equals(thisApplication.currentUnit)){
                            unitJSONArray.getJSONObject(j).put("hugSensitivity", h);
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

//                    DataApplication.connectedThread2.send(100 + progress, thisApplication.currentUnit);

        } catch (JSONException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

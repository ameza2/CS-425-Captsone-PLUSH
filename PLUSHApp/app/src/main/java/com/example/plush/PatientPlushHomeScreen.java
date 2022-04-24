// Utility Package //
package com.example.plush;

// Libraries //
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    Button hugButton; // button variable: hug button (hug patient)
    Button musicButton; // button variable: music button (configure music settings)
    SeekBar sensitivityBar; // seekbar variable: used to configure hug sensitivity
    TextView sensitivityText; // textview variable: used to display hug sensitivity
    int sensitivity;

    TextView connectionText; // textview variable: used to show current connection status
    Button connectionRetryButton; // button variable: used to retry connection
    Button connectionCloseButton; // button variable: used to stop attempting to connect
    View connectionLayout; // layout variable: shows the attempted connection status
    PatientPlushHomeScreen.UnitConnectionThread connectionThread;

    /* Initialize Page Activity (Staff PLUSH Unit Screen) */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_plush_home_screen);

        /* Initializations */
        roomNum = (TextView) findViewById (R.id.roomNum);
        unitID = (TextView) findViewById (R.id.unitID);
        musicButton = (Button) findViewById (R.id.musicButton);
        hugButton = (Button) findViewById (R.id.hugButton);
        sensitivityBar = findViewById(R.id.sensitivityBar);
        sensitivityText = findViewById(R.id.sensitivityText);
        connectionText = (TextView) findViewById(R.id.connectionTextView);
        connectionCloseButton = (Button) findViewById(R.id.closeConnectionButton);
        connectionRetryButton = (Button) findViewById(R.id.retryConnectionButton);
        connectionLayout = (View) findViewById(R.id.connectionLayout);


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
                DataApplication.connectedThread2.send("HSEN:" + Integer.toString(seekBar.getProgress()));
            }
        });

        /* Music Button: used to configure PLUSH music preferences */
        musicButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PatientPlushHomeScreen.this, PatientMusicScreen.class);
                startActivity(intent); // redirect page (PatientMusicScreen)
            }
        });


        /* Hug Button: used to hug the patient */
        hugButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DataApplication.connectedThread2.send("HUGP:1");
            }
        });

        connectionLayout.setVisibility(GONE);
        if(thisApplication.firstTime) {
            connectionLayout.setVisibility(VISIBLE);
            connectionThread = new PatientPlushHomeScreen.UnitConnectionThread();
            connectionThread.start();
        }

        thisApplication.firstTime = false;
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

    public class UnitConnectionThread extends Thread{
        public void run(){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StatsScreen(false, false, "Connecting...");
                }
            });

            DataApplication.connectedThread2.sendUDP(thisApplication.currentUnit, "255.255.255.255", 4210);
            int status = 0;
            while(status == 0){
                status = DataApplication.connectedThread2.checkIfFinitshed();
            }

            Log.e("Connection?", Integer.toString(status));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int status = DataApplication.connectedThread2.checkIfFinitshed();
                    String statText = status == -1 ? "Connection failed" : "Connection established";
                    StatsScreen(status == -1, true, statText);
                }
            });

            connectionRetryButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    connectionThread = new PatientPlushHomeScreen.UnitConnectionThread();
                    connectionThread.start();
                }
            });

            connectionCloseButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    connectionLayout.setVisibility(GONE);
                }
            });

        }
    }

    public void StatsScreen(boolean retryButtonOn, boolean closeButtonOn, String text){
        connectionRetryButton.setVisibility(retryButtonOn ? VISIBLE : GONE);
        connectionCloseButton.setVisibility(closeButtonOn ? VISIBLE : GONE);
        connectionText.setText(text);
    }
}

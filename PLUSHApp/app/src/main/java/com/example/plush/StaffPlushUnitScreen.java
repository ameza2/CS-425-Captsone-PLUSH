// Utility Package //
package com.example.plush;

// Libraries //
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
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

public class StaffPlushUnitScreen extends AppPLUSHActivity { // StaffPlushUnitScreen w/ action activities
    TextView roomNum; // textview variable: used to store patient room number from PLUSH instance
    TextView unitID; // textview variable: used to store PLUSH PID from PLUSH instance
    Button editButton; // button variable: edit button (view/modify patient information)
    Button scheduleButton; // button variable: schedule button (view upcoming events w/ calendar feature)
    Button musicButton; // button variable: music button (configure music settings)
    Button shutdownButton; // button variable: shutdown button (deactivate PLUSH unit and prompt alert message)
    SeekBar sensitivityBar; // seekbar variable: used to configure hug sensitivity
    TextView sensitivityText; // textview variable: used to display hug sensitivity

    TextView connectionText; // textview variable: used to show current connection status
    Button connectionRetryButton; // button variable: used to retry connection
    Button connectionCloseButton; // button variable: used to stop attempting to connect
    View connectionLayout; // layout variable: shows the attempted connection status

    int sensitivity; // variable: used to store hug sensitivity

    UnitConnectionThread connectionThread;

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
        sensitivityBar = findViewById(R.id.sensitivityBar);
        sensitivityText = findViewById(R.id.sensitivityText);
        connectionText = (TextView) findViewById(R.id.connectionTextView);
        connectionCloseButton = (Button) findViewById(R.id.closeConnectionButton);
        connectionRetryButton = (Button) findViewById(R.id.retryConnectionButton);
        connectionLayout = (View) findViewById(R.id.connectionLayout);


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

//                    DataApplication.connectedThread2.send(100 + progress, thisApplication.currentUnit);

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
                DataApplication.connectedThread2.send("HSEN:" + Integer.toString(seekBar.getProgress()));
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

        connectionRetryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connectionThread = new UnitConnectionThread();
                connectionThread.start();
            }
        });

        connectionCloseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connectionLayout.setVisibility(GONE);
            }
        });

        connectionThread = new UnitConnectionThread();
        connectionThread.start();
    }

    /* Add Units After Pressing Back */
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(StaffPlushUnitScreen.this, StaffHomeScreen.class);
        startActivity(intent); // page redirect (StaffHomeScreen)
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

        }
    }

    public void StatsScreen(boolean retryButtonOn, boolean closeButtonOn, String text){
        connectionRetryButton.setVisibility(retryButtonOn ? VISIBLE : GONE);
        connectionCloseButton.setVisibility(closeButtonOn ? VISIBLE : GONE);
        connectionText.setText(text);
    }

}
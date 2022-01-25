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

import java.util.ArrayList;

public class PatientPlushHomeScreen extends AppCompatActivity {
    ScrollView unitListScrollView;

    TextView roomNum; // textview variable: used to store patient room number from PLUSH instance
    TextView unitID; // textview variable: used to store PLUSH PID from PLUSH instance
    DataApplication thisApplication; // data application: file manipulation
    Button musicButton; // button variable: music button (configure music settings)
    Button backButton; // button variable: sends users back to the unit page
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
        backButton = (Button) findViewById (R.id.backtounitbutton);
        sensitivityBar = findViewById(R.id.sensitivityBar);
        sensitivityText = findViewById(R.id.sensitivityText);
        thisApplication = (DataApplication) getApplication();


        /* Pass the plush ID and room number from the home screen */
//        roomNum.setText("Room " + thisApplication.currUnitData().room);
//        unitID.setText("Unit #" + thisApplication.currUnitData().id);

        /* Hug Sensitivity Bar: used to calibrate PLUSH hug sensitivity */
        sensitivityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sensitivityText.setText("Hug Sensitivity: " + String.valueOf(progress)); // print hug sensitivity value
                sensitivity = progress; // store hug sensitivity value
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
                Intent intent = new Intent(PatientPlushHomeScreen.this, StaffMusicScreen.class);
                startActivity(intent); // redirect page (PatientMusicScreen)
            }
        });

        /* Back Button: return to PLUSH Home Page */
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                thisApplication.currentUnit = "";
                Intent intent = new Intent(PatientPlushHomeScreen.this, PatientLoginScreen.class);
                startActivity(intent); // page redirect (PatientLoginScreen)
            }
        });
    }

}

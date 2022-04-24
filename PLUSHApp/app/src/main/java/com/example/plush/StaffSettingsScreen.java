// Utility Package //
package com.example.plush;

// Libraries //
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ComputableLiveData;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

/* Android Widgets */
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class StaffSettingsScreen extends AppPLUSHActivity{ // StaffSettingsScreen w/ action activities

    Button softwareDetailsButton; // button variable: software details button (redirect user to software info page)
    SwitchCompat darkModeSwitch; // dark mode switch: will change the app's theme to dark mode
    SharedPreferences sharedPreferences  =  null;

    /* Initialize Page Activity (Staff Settings Screen) */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_settings_screen);

        softwareDetailsButton = (Button) findViewById(R.id.SoftwareDetailsButtonID);
        darkModeSwitch = findViewById(R.id.darkMode);

        //If app is already in dark mode, set the toggle to be true
        int nightModeFlags =
                darkModeSwitch.getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                darkModeSwitch.setChecked(true);
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                darkModeSwitch.setChecked(false);
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                darkModeSwitch.setChecked(false);
                break;
        }


        // Things for Dark Mode
        sharedPreferences = getSharedPreferences("night", 0);
        Boolean booleanValue = sharedPreferences.getBoolean("night mode", true);
        if (booleanValue){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            darkModeSwitch.setChecked(true);
        }

        /* Software Details Button: used to redirect user to info page illustrating software properties (i.e., version, patch notes, etc.) */
        softwareDetailsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StaffSettingsScreen.this, staff_SoftwareDetailsScreen.class);
                startActivity(intent); // redirect page (staff_SoftwareDetailsScreen)
            }
        });


        // Dark Mode Button: used to turn on and off dark mode
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isNightModeOn){
                if(isNightModeOn){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    darkModeSwitch.setChecked(true);
                    Toast.makeText(getApplicationContext(), "Dark Mode On", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("night mode", true);
                    editor.commit();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    darkModeSwitch.setChecked(false);
                    Toast.makeText(getApplicationContext(), "Dark Mode Off", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("night mode", false);
                    editor.commit();

                }
            }
        });


    }
}
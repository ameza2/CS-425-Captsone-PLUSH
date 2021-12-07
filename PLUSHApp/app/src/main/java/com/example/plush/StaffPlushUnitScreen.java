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


        //Pass the plush ID and room number from the home screen
        roomNum.setText("Room " + thisApplication.currUnitData().room);
        unitID.setText("Unit #" + thisApplication.currUnitData().id);

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
                startActivity(intent); // redirect page (StafMusicScreen)
            }
        });

        /* Shutdown Button: abort PLUSH instructions and shutdown PLUSH unit */
        shutdownButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "PLUSH has been deactivated!", Toast.LENGTH_SHORT).show(); // deactivation prompt
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                thisApplication.currentUnit = "";
                Intent intent = new Intent(StaffPlushUnitScreen.this, StaffHomeScreen.class);
                startActivity(intent);
            }
        });
    }

    // I can add units after pressing back
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        thisApplication.currentUnit = "";
    }
}
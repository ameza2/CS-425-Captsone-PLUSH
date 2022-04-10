// Utility Package //
package com.example.plush;

// Libraries //
import androidx.appcompat.app.AppCompatActivity;

import com.example.plush.data.DataApplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

/* Android Widgets */
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/* Json Objects */
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
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.widget.ScrollView;
import android.widget.TimePicker;
import android.widget.Toast;

public class StaffAddScheduleScreen extends AppPLUSHActivity { // StaffAddUnitScreen w/ action activities

    Button dateButton; // button variable: date selection button
    Button timeButton; // button variable: time selection button
    Button AddScheduleButton; // button variable: addUnit button
    RadioGroup scheduleGroup; // button group variable: used to identify schedule type from group of buttons
    DataApplication thisApplication; // data application variable: used for file manipulation
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    View scheduleButton;
    int scheduleIndex;
    String date = "";
    String time = "";
    Calendar currCal;

    /* Initialize Page Activity (Add PLUSH Unit Screen) */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_add_schedule_screen);
        thisApplication = (DataApplication) getApplication();

        dateButton = (Button) findViewById(R.id.dateButton);
        timeButton = (Button) findViewById(R.id.timeButton);
        AddScheduleButton = (Button) findViewById(R.id.buttonAddSchedule);
        scheduleGroup = (RadioGroup) findViewById(R.id.radioGroup);


        //Schedule button Selection
        scheduleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                scheduleButton = scheduleGroup.findViewById(checkedId);
                scheduleIndex = scheduleGroup.indexOfChild(scheduleButton);
            }
        });

        //Date button Selection
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                currCal = cal;

                DatePickerDialog dialog = new DatePickerDialog(
                        StaffAddScheduleScreen.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
//                Log.d("valid","onDateSet: mm/dd/yyy: " + month + " / " + day + " / " + year);

                date = month + "/" + day + "/" + year;
                dateButton.setText(date);
            }
        };

        //Time button Selection
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(
                        StaffAddScheduleScreen.this,
                        timeSetListener,
                        hour, minute,
                        false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            }
        });
        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
//                Log.d("valid","onTimeSet: h:mm : " + hour + ":" + minute);
                currCal.set(Calendar.HOUR_OF_DAY, hour);
                currCal.set(Calendar.MINUTE, minute);

                if(hour > 12){
                    if(minute < 10){
                        time = hour - 12 + ":" + "0" + minute + " PM";
                    }
                    else{
                        time = hour - 12 + ":" + minute + " PM";
                    }
                }
                else if(hour == 0){
                    if(minute < 10){
                        time = hour + 1 + ":" + "0" + minute + " AM";
                    }
                    else{
                        time = hour + 1 + ":" + minute + " AM";
                    }
                }
                else{
                    if(minute < 10){
                        time = hour + ":" + "0" + minute + " AM";
                    }
                    else{
                        time = hour + ":" + minute + " AM";
                    }
                }
                timeButton.setText(time);
            }
        };

        /* Add Schedule Button: Create a string using PLUSH properties, and append PLUSH unit to user's database entry */
        AddScheduleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                boolean emptyDate = date.isEmpty();
                boolean emptyTime = time.isEmpty();

                int emptyScheduleOption = scheduleGroup.getCheckedRadioButtonId(); // empty value == -1

                if ((emptyDate && emptyTime) | (emptyDate && (emptyScheduleOption == -1)) | (emptyTime && (emptyScheduleOption == -1)) | (emptyDate && emptyTime && (emptyScheduleOption == -1))){
                    Toast.makeText(getApplicationContext(), "Invalid Form Submission: Missing multiple fields.", Toast.LENGTH_LONG).show(); // deactivation prompt
                    //Log.d("Error [1]: ", "Empty Text Field");
                }
                else if (emptyDate) { // Input Text Validation: Required Fields
                    Toast.makeText(getApplicationContext(), "Invalid Form Submission: Missing Schedule Date.", Toast.LENGTH_LONG).show(); // deactivation prompt
                    //Log.d("Error [2]: ", "Empty Text Field");
                }
                else if (emptyTime){
                    Toast.makeText(getApplicationContext(), "Invalid Form Submission: Missing Time.", Toast.LENGTH_LONG).show(); // deactivation prompt
                    //Log.d("Error [3]: ", "Empty Text Field");
                }
                else if (emptyScheduleOption == -1) {
                    Toast.makeText(getApplicationContext(), "Invalid Form Submission: Missing Schedule Type.", Toast.LENGTH_LONG).show(); // deactivation prompt
                    //Log.d("Error [4]: ", "Empty Text Field");
                }
                else {
                    Log.d("Success:", "Valid Text Fields");
                    if(scheduleIndex == 0) {
                        //Log.d("valid", "selected Hug");
                        thisApplication.currUnitData().hugSchedule.add(date + "," + time);
                        thisApplication.saveNewSchedule("hugSchedule");
                        //
                    }
                    else if(scheduleIndex == 1) {
                        //Log.d("valid", "selected Music");
                        thisApplication.currUnitData().musicSchedule.add(date + "," + time);
                        thisApplication.saveNewSchedule("musicSchedule");
                        //
                    }
                    else if(scheduleIndex == 2) {
                       // Log.d("valid", "selected Other");
                        thisApplication.currUnitData().otherSchedule.add(date + "," + time);
                        thisApplication.saveNewSchedule("otherSchedule");

                        //
                    }

                    //Add schedule to timer
                    Timer t = new Timer();
                    Date date = currCal.getTime();
                    t.schedule(
                            new TimerTask()
                            {
                                public void run()
                                {
                                    if(scheduleIndex == 0) {
                                        //Log.d("valid", "Hug schedule activated!");
                                        //To be replaced with actual arduino command
                                        DataApplication.connectedThread2.send("HUGP:1");
                                    }
                                    else if(scheduleIndex == 1) {
                                        //Log.d("valid", "Music schedule activated!");
                                        //To be replaced with actual arduino command
                                        DataApplication.connectedThread2.send("PMUS:1");
                                    }
                                    else if(scheduleIndex == 2) {
                                        // Log.d("valid", "Other schedule activated!");
                                        //To be replaced with actual arduino command
                                        //DataApplication.connectedThread2.send("MVOL:"+Integer.toString(seekBar.getProgress()));
                                    }
                                }
                            },
                            date);      // run task on date

                        Intent intent = new Intent(StaffAddScheduleScreen.this, StaffScheduleScreen.class);
                        startActivity(intent); // redirect page (StaffHomeScreen)
                    }
//                }
            }
        });
    }
}
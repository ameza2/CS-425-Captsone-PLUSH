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
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.Calendar;

import android.widget.TimePicker;
import android.widget.Toast;

public class StaffAddScheduleScreen extends AppCompatActivity { // StaffAddUnitScreen w/ action activities

    Button dateButton; // button variable: date selection button
    Button timeButton; // button variable: time selection button
    Button AddScheduleButton; // button variable: addUnit button
    RadioGroup scheduleGroup; // button group variable: used to identify schedule type from group of buttons
    RadioButton scheduleButton; // button group variable: used to store schedule
    DataApplication thisApplication; // data application variable: used for file manipulation
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    String date;
    String time;

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

        int scheduleID = scheduleGroup.getCheckedRadioButtonId(); // fetch sex option from button input
        scheduleButton = findViewById(scheduleID);

        thisApplication = (DataApplication)getApplication();

        //Date button Selection
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

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

                date = month + " / " + day + " / " + year;
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
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                if(hour > 12){
                    hour -= 12;
                }
//                Log.d("valid","onTimeSet: h:mm : " + hour + ":" + minute);

                time = hour + " : " + minute;
            }
        };

        /* Add Schedule Button: Create a string using PLUSH properties, and append PLUSH unit to user's database entry */
        AddScheduleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                boolean emptyDate = date.isEmpty();
                boolean emptyTime = time.isEmpty();

                int emptySex = scheduleGroup.getCheckedRadioButtonId(); // empty value == -1

                if ((emptyDate && emptyTime) | (emptyDate && (emptySex == -1)) | (emptyTime && (emptySex == -1)) | (emptyDate && emptyTime && (emptySex == -1))){
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
                else if (emptySex == -1) {
                    Toast.makeText(getApplicationContext(), "Invalid Form Submission: Missing Schedule Type.", Toast.LENGTH_LONG).show(); // deactivation prompt
                    //Log.d("Error [4]: ", "Empty Text Field");
                }
                else {
                    //Log.d("Success:", "Valid Text Fields");

                    /* If there isn't a schedule to add: */
                    if (thisApplication.currentUnit.equals("")) {

                        switch(scheduleButton.getText().toString()){
                            case("Hug"):
                                thisApplication.currUnitData().hugSchedule.add(date + "," + time);
                                break;
                            case("Music"):
                                thisApplication.currUnitData().musicSchedule.add(date + "," + time);
                                break;
                            case("Other"):
                                thisApplication.currUnitData().otherSchedule.add(date + "," + time);
                                break;
                        }

                        /* Update JSON File */
                        try {
                            JSONArray inputJSONArray = thisApplication.inputJSON.getJSONArray("userlist");
                            for (int i = 0; i < inputJSONArray.length(); i++) {
                                if (inputJSONArray.getJSONObject(i).getString("username").equals(thisApplication.currentUser)) {

                                    /* Edit unit properties */
                                    JSONArray unitJSONArray = inputJSONArray.getJSONObject(i).getJSONArray("units");
                                    for(int j = 0; j < unitJSONArray.length(); j++){
                                        if(unitJSONArray.getJSONObject(j).getString("id").equals(thisApplication.currentUnit)){
                                            unitJSONArray.getJSONObject(j).put(scheduleButton.getText().toString(),  date + "," + time);
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
                        } catch (JSONException | FileNotFoundException e) { // error-handling statement
                            e.printStackTrace();
                        } catch (IOException e) { // error-handling statement
                            e.printStackTrace();
                        }

                        /* After JSON Update, Return to Home Page w/ Updated PLUSH Unit */
                        Intent intent = new Intent(StaffAddScheduleScreen.this, StaffScheduleScreen.class);
                        startActivity(intent); // redirect page (StaffHomeScreen)


                    }

                    /* If there IS a unit to add */
                    else {
//                        /* Since the data uses a hashmap, have to replace old */
//                        String oldID = thisApplication.currUnitData().id;
//                        thisApplication.currUserData().assignedUnits.remove(oldID);
//                        String newID = dateButton.getText().toString();
//                        String newRoom = timeButton.getText().toString();
//                        thisApplication.currUserData().addUnit(newID, newRoom);
//                        thisApplication.currentUnit = newID;
//
//                        /* Update JSON File */
//                        try {
//                            JSONArray inputJSONArray = thisApplication.inputJSON.getJSONArray("userlist");
//                            for (int i = 0; i < inputJSONArray.length(); i++) {
//                                if (inputJSONArray.getJSONObject(i).getString("username").equals(thisApplication.currentUser)) {
//
//                                    /* Edit unit properties */
//                                    JSONArray unitJSONArray = inputJSONArray.getJSONObject(i).getJSONArray("units");
//                                    for (int j = 0; j < unitJSONArray.length(); j++) {
//                                        if (unitJSONArray.getJSONObject(j).getString("id").equals(oldID)) {
//                                            unitJSONArray.getJSONObject(j).put("id", newID);
//                                            unitJSONArray.getJSONObject(j).put("room", newRoom);
//                                        }
//                                    }
//
//                                    /* Save new string to user database */
//                                    File f = new File(thisApplication.getFilesDir(), "userdatabase.json");
//                                    OutputStream outputStream = new FileOutputStream(f);
//                                    byte outputBytes[] = thisApplication.inputJSON.toString().getBytes(StandardCharsets.UTF_8);
//                                    outputStream.write(outputBytes);
//                                    outputStream.close();
//                                }
//                            }
//                        } catch (JSONException | FileNotFoundException e) { // error-handling statement
//                            e.printStackTrace();
//                        } catch (IOException e) { // error-handling statement
//                            e.printStackTrace();
//                        }

                        /* After JSON Update, Return to Home Page w/ Updated PLUSH Unit */
                        Intent intent = new Intent(StaffAddScheduleScreen.this, StaffScheduleScreen.class);
                        startActivity(intent); // redirect page (StaffScheduleScreen)
                    }
                }
            }
        });
    }
}
// Utility Package //
package com.example.plush;

// Libraries //
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.plush.data.DataApplication;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class StaffRemoveScheduleScreen extends AppPLUSHActivity { // StaffScheduleScreen w/ action activities

    Calendar c = Calendar.getInstance();
    SimpleDateFormat date = new SimpleDateFormat("M/d/yyyy");
    String currDate = date.format(c.getTime());

    CalendarView calendarView;
    TextView dateDisplay;
    Button RemoveButton;
    ListView listView;
    ScrollView unitListScrollView;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> currDayHug = new ArrayList<>();
    ArrayList<String> currDayMusic = new ArrayList<>();
    ArrayList<String> currDayOther = new ArrayList<>();
    ArrayList<Boolean> schedulePressedList = new ArrayList<>(); // array consisting of buttons pressed
    ArrayAdapter<String> arrayAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_remove_schedule_screen);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        dateDisplay = (TextView) findViewById(R.id.date_display);
        unitListScrollView = (ScrollView)findViewById(R.id.scrollview);
        listView = (ListView) findViewById(R.id.listview);
        RemoveButton = (Button) findViewById(R.id.removeButton);
        dateDisplay.setText("Schedules for  " + currDate + ":");

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                currDate = (i1 + 1) + "/" + i2 + "/" + i;
                dateDisplay.setText("Schedules for " + currDate + ":");
                arrayAdapter.clear();
                currDayHug.clear();
                currDayMusic.clear();
                currDayOther.clear();

                //Parse and iterate checking for schedules in the same day
                for(int j = 0; j < thisApplication.currUnitData().hugSchedule.size(); j++) {
                    String[] scheduleDateHug = thisApplication.currUnitData().hugSchedule.get(j).split(",");
                    for (String t : scheduleDateHug) {
                        currDayHug.add(t);
                        //Log.d("valid", "currDay added");
                    }
                }
                for(int j = 0; j < thisApplication.currUnitData().musicSchedule.size(); j++) {
                    String[] scheduleDateMusic = thisApplication.currUnitData().musicSchedule.get(j).split(",");
                    for (String t : scheduleDateMusic) {
                        currDayMusic.add(t);
                        //Log.d("valid", "currDay added");
                    }
                }
                for(int j = 0; j < thisApplication.currUnitData().otherSchedule.size(); j++) {
                    String[] scheduleDateOther = thisApplication.currUnitData().otherSchedule.get(j).split(",");
                    for (String t : scheduleDateOther) {
                        currDayOther.add(t);
                        //Log.d("valid", "currDay added");
                    }
                }

                //Add schedule to buttonList
                for(int counter = 0; counter < currDayHug.size(); counter += 2) {
                    if(currDayHug.get(counter).equals(currDate)){
                        arrayList.add("Hug scheduled at " + currDayHug.get(counter + 1));
                        //Log.d("valid", "currDay added to list");
                    }
                }
                for(int counter = 0; counter < currDayMusic.size(); counter += 2) {
                    if(currDayMusic.get(counter).equals(currDate)){
                        arrayList.add("Music scheduled at " + currDayMusic.get(counter + 1));
                        //Log.d("valid", "currDay added to list");
                    }
                }
                for(int counter = 0; counter < currDayOther.size(); counter += 2) {
                    if(currDayOther.get(counter).equals(currDate)){
                        arrayList.add("Other scheduled at " + currDayOther.get(counter + 1));
                        //Log.d("valid", "currDay added to list");
                    }
                }
                arrayAdapter.notifyDataSetChanged();
                //Toast.makeText(getApplicationContext(), "Selected Date:\n" + "Day = " + i2 + "\n" + "Month = " + i1 + "\n" + "Year = " + i, Toast.LENGTH_LONG).show();
            }
        });

        /* Remove Button: remove PLUSH unit from user's database entry */
        RemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < listView.getChildCount(); i++) {
                    if(schedulePressedList.get(i)){
                        //Find string to remove
                        String toremove = arrayList.get(i);
                        if(toremove.contains("Hug")){
                            //Parse the schedule
                            ArrayList<String> temp = new ArrayList<>();
                            String[] parser = arrayList.get(i).split(" ");
                            String specString = "";
                            for (String t : parser) {
                                temp.add(t);
                            }
                            specString = temp.get(3) + temp.get(4);

                            //Find location of schedule in arraylist
                            int indexOf = currDate.indexOf(specString);
                            indexOf /= 2;

                            //Remove schedule from arraylist
                            thisApplication.currUnitData().hugSchedule.remove(indexOf);
                        }
                        else if(toremove.contains("Music")){
                            //Parse the schedule
                            ArrayList<String> temp = new ArrayList<>();
                            String[] parser = arrayList.get(i).split(" ");
                            String specString = "";
                            for (String t : parser) {
                                temp.add(t);
                            }
                            specString = temp.get(3) + temp.get(4);

                            //Find location of schedule in arraylist
                            int indexOf = currDate.indexOf(specString);
                            indexOf /= 2;

                            //Remove schedule from arraylist
                            thisApplication.currUnitData().musicSchedule.remove(indexOf);
                        }
                        else if(toremove.contains("Other")){
                            //Parse the schedule
                            ArrayList<String> temp = new ArrayList<>();
                            String[] parser = arrayList.get(i).split(" ");
                            String specString = "";
                            for (String t : parser) {
                                temp.add(t);
                            }
                            specString = temp.get(3) + temp.get(4);

                            //Find location of schedule in arraylist
                            int indexOf = currDate.indexOf(specString);
                            indexOf /= 2;

                            //Remove schedule from arraylist
                            thisApplication.currUnitData().otherSchedule.remove(indexOf);
                        }
                    }
                }
                /* After schedule removal, return to scheduler Screen */
                Intent intent = new Intent(StaffRemoveScheduleScreen.this, StaffScheduleScreen.class);
                startActivity(intent); // page redirect (StaffScheduleScreen)
            }
        });

//        RemoveButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                /* Modify JSON File (Database) */
////                try {
////
////                    /* Fetch the correct user/unit array */
////                    JSONArray inJSONArray = thisApplication.inputJSON.getJSONArray("userlist");
////                    JSONArray unitJSONArray = null;
////                    JSONArray unitJSONArray1 = null;
////                    JSONArray unitJSONArray2 = null;
////                    JSONArray unitJSONArray3 = null;
////
////                    for (int l = 0; l < inJSONArray.length(); l++) {
////                        if(inJSONArray.getJSONObject(l).getString("username").equals(thisApplication.currentUser)) {
////                            unitJSONArray = inJSONArray.getJSONObject(l).getJSONArray("units");
////                        }
////                    }
//
//                    for (int i = 0; i < listView.getChildCount(); i++) {
//                        /* For each button, check if the button is pressed */
//                        if(schedulePressedList.get(i)) {
////                            String toremove = listView.getChildAt(i).toString();
////                            for(int j = 0; j < unitJSONArray.length(); j++) {
////                                if(unitJSONArray.getJSONObject(j).getString("hugSchedule").equals(toremove)) {
////                                    unitJSONArray.remove(j);
////                                }
////                                if(unitJSONArray.getJSONObject(j).getString("musicSchedule").equals(toremove)) {
////                                    unitJSONArray.remove(j);
////                                }
////                                if(unitJSONArray.getJSONObject(j).getString("otherSchedule").equals(toremove)) {
////                                    unitJSONArray.remove(j);
////                                }
////                            }
//
////                            thisApplication.currUserData().hug;
//                        }
//                    }
//
////                    /* Save new string */
////                    File f = new File(thisApplication.getFilesDir(), "userdatabase.json");
////                    OutputStream outputStream = new FileOutputStream(f);
////                    byte outputBytes[] = thisApplication.inputJSON.toString().getBytes(StandardCharsets.UTF_8);
////                    outputStream.write(outputBytes);
////                    outputStream.close();
////
////                } catch (JSONException | FileNotFoundException e) {
////                    e.printStackTrace();
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
//
//                /* After schedule removal, return to scheduler Screen */
//                Intent intent = new Intent(StaffRemoveScheduleScreen.this, StaffScheduleScreen.class);
//                startActivity(intent); // page redirect (StaffScheduleScreen)
//            }
//        });

        //Parse and iterate checking for schedules in the same day
        for(int j = 0; j < thisApplication.currUnitData().hugSchedule.size(); j++) {
            String[] scheduleDateHug = thisApplication.currUnitData().hugSchedule.get(j).split(",");
            for (String t : scheduleDateHug) {
                currDayHug.add(t);
                //Log.d("valid", "scheduleDateHug added");
            }
        }
        for(int j = 0; j < thisApplication.currUnitData().musicSchedule.size(); j++) {
            String[] scheduleDateMusic = thisApplication.currUnitData().musicSchedule.get(j).split(",");
            for (String t : scheduleDateMusic) {
                currDayMusic.add(t);
                //Log.d("valid", "scheduleDateMusic added");
            }
        }
        for(int j = 0; j < thisApplication.currUnitData().otherSchedule.size(); j++) {
            String[] scheduleDateOther = thisApplication.currUnitData().otherSchedule.get(j).split(",");
            for (String t : scheduleDateOther) {
                currDayOther.add(t);
                //Log.d("valid", "scheduleDateOther added");
            }
        }

        //Add schedule to buttonList
        for(int counter = 0; counter < currDayHug.size(); counter += 2) {
            if(currDayHug.get(counter).equals(currDate)){
                arrayList.add("Hug scheduled at " + currDayHug.get(counter + 1));
                schedulePressedList.add(false);
                //Log.d("valid", "Hug added to list");
            }
        }
        for(int counter = 0; counter < currDayMusic.size(); counter += 2) {
            if(currDayMusic.get(counter).equals(currDate)){
                arrayList.add("Music scheduled at " + currDayMusic.get(counter + 1));
                schedulePressedList.add(false);
                //Log.d("valid", "Music added to list");
            }
        }
        for(int counter = 0; counter < currDayOther.size(); counter += 2) {
            if(currDayOther.get(counter).equals(currDate)){
                arrayList.add("Other scheduled at " + currDayOther.get(counter + 1));
                schedulePressedList.add(false);
                //Log.d("valid", "Other added to list");
            }
        }

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if(schedulePressedList.get(i)){ // If it is already selected
                        listView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                        schedulePressedList.set(i, false);
                    }
                    else{ // If it isn't
                        listView.getChildAt(i).setBackgroundColor(Color.RED);
                        schedulePressedList.set(i, true);
                    }
                    //Log.d("Selected,", "item: " + arrayList.get(i));
            }
        });
    }
}

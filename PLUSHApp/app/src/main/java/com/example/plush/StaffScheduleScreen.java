// Utility Package //
package com.example.plush;

// Libraries //
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.plush.data.DataApplication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class StaffScheduleScreen extends AppCompatActivity { // StaffScheduleScreen w/ action activities

    Calendar c = Calendar.getInstance();
    SimpleDateFormat date = new SimpleDateFormat("M/d/yyyy");
    String currDate = date.format(c.getTime());

    CalendarView calendarView;
    TextView dateDisplay;
    Button AddButton;
    Button RemoveButton;
    ListView listView;
    ScrollView unitListScrollView;
    DataApplication thisApplication; // data application variable: used for file manipulation
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> currDayHug = new ArrayList<>();
    ArrayList<String> currDayMusic = new ArrayList<>();
    ArrayList<String> currDayOther = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_schedule_screen);
        thisApplication = (DataApplication) getApplication();

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        dateDisplay = (TextView) findViewById(R.id.date_display);
        unitListScrollView = (ScrollView)findViewById(R.id.scrollview);
        listView = (ListView) findViewById(R.id.listview);
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

        AddButton = (Button)findViewById(R.id.addButton);
        AddButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(StaffScheduleScreen.this, StaffAddScheduleScreen.class);
                startActivity(intent);
            }
        });

        RemoveButton = (Button)findViewById(R.id.removeButton);
        RemoveButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(StaffScheduleScreen.this, StaffRemoveScheduleScreen.class);
                startActivity(intent);
            }
        });

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

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
    }
}

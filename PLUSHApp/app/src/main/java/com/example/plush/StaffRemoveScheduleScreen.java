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
                refreshList();
                arrayAdapter.notifyDataSetChanged();
                //Toast.makeText(getApplicationContext(), "Selected Date:\n" + "Day = " + i2 + "\n" + "Month = " + i1 + "\n" + "Year = " + i, Toast.LENGTH_LONG).show();
            }
        });

        /* Remove Button: remove PLUSH unit from user's database entry */
        RemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffRemoveScheduleScreen.this, StaffScheduleScreen.class);
                startActivity(intent); // page redirect (StaffScheduleScreen)
            }
        });

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);

        refreshList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String toremove = arrayList.get(i);
                if(toremove.contains("Hug")){

                    int x = i;
                    String dateToRemove = currDayHug.get(x);
                    thisApplication.scheduler.RemoveSchedule(dateToRemove, 0);
                    thisApplication.currUnitData().hugSchedule.remove(dateToRemove);
                    thisApplication.saveNewSchedule("hugSchedule");
                }
                else if(toremove.contains("Music")){

                    int x = i - (currDayHug.size());
                    String dateToRemove = currDayMusic.get(x);
                    thisApplication.scheduler.RemoveSchedule(dateToRemove, 1);
                    thisApplication.currUnitData().musicSchedule.remove(dateToRemove);
                    thisApplication.saveNewSchedule("musicSchedule");
                }
                else if(toremove.contains("Other")){

                    int x = i - (currDayHug.size() + currDayMusic.size());
                    String dateToRemove = currDayOther.get(x);
                    thisApplication.scheduler.RemoveSchedule(dateToRemove, 2);
                    thisApplication.currUnitData().otherSchedule.remove(dateToRemove);
                    thisApplication.saveNewSchedule("otherSchedule");
                }

                refreshList();
            }
        });
    }

    void refreshList(){
        arrayAdapter.clear();
        currDayHug.clear();
        currDayMusic.clear();
        currDayOther.clear();

        for(int j = 0; j < thisApplication.currUnitData().hugSchedule.size(); j++) {
            String[] scheduleDateHug = thisApplication.currUnitData().hugSchedule.get(j).split(",");
            if(scheduleDateHug[0].equals(currDate)){
                currDayHug.add(thisApplication.currUnitData().hugSchedule.get(j));
                arrayList.add("Hug scheduled at " + scheduleDateHug[1]);
            }
        }

        for(int j = 0; j < thisApplication.currUnitData().musicSchedule.size(); j++) {
            String[] scheduleDateMusic = thisApplication.currUnitData().musicSchedule.get(j).split(",");
            if(scheduleDateMusic[0].equals(currDate)){
                currDayMusic.add(thisApplication.currUnitData().musicSchedule.get(j));
                arrayList.add("Music scheduled at " + scheduleDateMusic[1]);
            }
        }

        for(int j = 0; j < thisApplication.currUnitData().otherSchedule.size(); j++) {
            String[] scheduleDateOther = thisApplication.currUnitData().otherSchedule.get(j).split(",");
            if(scheduleDateOther[0].equals(currDate)){
                currDayOther.add(thisApplication.currUnitData().otherSchedule.get(j));
                arrayList.add("Other scheduled at " + scheduleDateOther[1]);
            }
        }
    }
}

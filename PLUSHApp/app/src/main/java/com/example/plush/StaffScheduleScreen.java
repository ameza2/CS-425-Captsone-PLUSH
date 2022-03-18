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

                int j =0;
                for(int counter = 0; counter < thisApplication.currUnitData().hugSchedule.size(); counter += 2) {
                    ArrayList<String> currDay = new ArrayList<>();
                    //Parse and iterate checking for schedules in the same day
                    String[] scheduleDateHug = thisApplication.currUnitData().hugSchedule.get(j).split(",");
                    for (String t : scheduleDateHug) {
                        currDay.add(t);
                        //Log.d("valid", "currDay added");
                    }

                    //Add schedule to buttonList
                    if(currDay.get(counter).equals(currDate)){
                        arrayList.add("Hug scheduled at " + currDay.get(counter + 1));
                        //Log.d("valid", "currDay added to list");
                    }
                    j++;
                }
                int k =0;
                for(int counter = 0; counter < thisApplication.currUnitData().musicSchedule.size(); counter += 2) {
                    ArrayList<String> currDay = new ArrayList<>();
                    //Parse and iterate checking for schedules in the same day
                    String[] scheduleDateMusic = thisApplication.currUnitData().musicSchedule.get(k).split(",");
                    for (String t : scheduleDateMusic) {
                        currDay.add(t);
                        //Log.d("valid", "currDay added");
                    }

                    //Add schedule to buttonList
                    if(currDay.get(counter).equals(currDate)){
                        arrayList.add("Music scheduled at " + currDay.get(counter + 1));
                        //Log.d("valid", "currDay added to list");
                    }
                    j++;
                }
                int l =0;
                for(int counter = 0; counter < thisApplication.currUnitData().otherSchedule.size(); counter += 2){
                    ArrayList<String> currDay = new ArrayList<>();
                    //Parse and iterate checking for schedules in the same day
                    String[] scheduleDateOther = thisApplication.currUnitData().otherSchedule.get(l).split(",");
                    for (String t : scheduleDateOther) {
                        currDay.add(t);
                        //Log.d("valid", "currDay added");
                    }

                    //Add schedule to buttonList
                    if(currDay.get(counter) == currDate){
                        arrayList.add("Other scheduled at " + currDay.get(counter + 1));
                        //Log.d("valid", "currDay added to list");
                    }
                    j++;
                }
                j = 0;
                k = 0;
                l = 0;
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

        int j =0;
        for(int counter = 0; counter < thisApplication.currUnitData().hugSchedule.size(); counter += 2){
            ArrayList<String> currDay = new ArrayList<>();
            //Parse and iterate checking for schedules in the same day
            String[] scheduleDateHug = thisApplication.currUnitData().hugSchedule.get(j).split(",");
            for (String t : scheduleDateHug) {
                currDay.add(t);
                //Log.d("valid", "currDay added");
            }

            //Add schedule to buttonList
            if(currDay.get(counter).equals(currDate)){
                arrayList.add("Hug scheduled at " + currDay.get(counter + 1));
                //Log.d("valid", "currDay added to list");
            }
            j++;
        }
        int k =0;
        for(int counter = 0; counter < thisApplication.currUnitData().musicSchedule.size(); counter += 2){
            ArrayList<String> currDay = new ArrayList<>();
            //Parse and iterate checking for schedules in the same day
            String[] scheduleDateMusic = thisApplication.currUnitData().musicSchedule.get(k).split(",");
            for (String t : scheduleDateMusic) {
                currDay.add(t);
                //Log.d("valid", "currDay added");
            }

            //Add schedule to buttonList
            if(currDay.get(counter).equals(currDate)){
                arrayList.add("Music scheduled at " + currDay.get(counter + 1));
                //Log.d("valid", "currDay added to list");
            }
            j++;
        }
        int l =0;
        for(int counter = 0; counter < thisApplication.currUnitData().otherSchedule.size(); counter += 2){
            ArrayList<String> currDay = new ArrayList<>();
            //Parse and iterate checking for schedules in the same day
            String[] scheduleDateOther = thisApplication.currUnitData().otherSchedule.get(l).split(",");
            for(String t : scheduleDateOther){
                currDay.add(t);
                //Log.d("valid", "currDay added");
            }

            //Add schedule to buttonList
            if(currDay.get(counter).equals(currDate)){
                arrayList.add("Other scheduled at " + currDay.get(counter + 1));
                //Log.d("valid", "currDay added to list");
            }
            j++;
        }

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);



//        // Load unit data
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//
//        LinearLayout linearLayout = new LinearLayout(this);
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
//        linearLayout.setLayoutParams(params);
//
//        //Create schedule list
//        int j = 0;

//        for(String i: thisApplication.currUserData().assignedUnits.keySet()) {
//            ArrayList<String> currDay = new ArrayList<>();
//
//            //Parse and iterate checking for schedules in the same day
//            String[] scheduleDateHug = thisApplication.currUserData().assignedUnits.get(i).hugSchedule.get(j).split(",");
//            for (String t : scheduleDateHug){
//                currDay.add(t);
//            }
//
//            //Add schedule to buttonList
//            if(currDay.get(0) == currDate){
//                buttonList.add(new Button(this));
//                unitIDList.add(currDay.get(1));
//
//                Button button = buttonList.get(j);
//                button.setText("Hug schedule for " + currDay.get(1));
//                button.setTextSize(1, 10);
//                button.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
//                button.setLayoutParams(params);
//                linearLayout.addView(button);
//            }
//            j++;
//        }
//        int k = 0;
//        for(String i: thisApplication.currUserData().assignedUnits.keySet()) {
//            ArrayList<String> currDay = new ArrayList<>();
//
//            //Parse and iterate checking for schedules in the same day
//            String[] scheduleDateMusic = thisApplication.currUserData().assignedUnits.get(i).musicSchedule.get(j).split(",");
//            for (String t : scheduleDateMusic){
//                currDay.add(t);
//            }
//
//            //Add schedule to buttonList
//            if(currDay.get(0) == currDate){
//                buttonList.add(new Button(this));
//                unitIDList.add(currDay.get(1));
//
//                Button button = buttonList.get(j);
//                button.setText("Music schedule for " + currDay.get(1));
//                button.setTextSize(1, 10);
//                button.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
//                button.setLayoutParams(params);
//                linearLayout.addView(button);
//            }
//            k++;
//        }
//        int l = 0;
//        for(String i: thisApplication.currUserData().assignedUnits.keySet()) {
//            ArrayList<String> currDay = new ArrayList<>();
//
//            //Parse and iterate checking for schedules in the same day
//            String[] scheduleDateOther = thisApplication.currUserData().assignedUnits.get(i).otherSchedule.get(j).split(",");
//            for (String t : scheduleDateOther){
//                currDay.add(t);
//            }
//
//            //Add schedule to buttonList
//            if(currDay.get(0) == currDate){
//                buttonList.add(new Button(this));
//                unitIDList.add(currDay.get(1));
//
//                Button button = buttonList.get(j);
//                button.setText("Other schedule for " + currDay.get(1));
//                button.setTextSize(1, 10);
//                button.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
//                button.setLayoutParams(params);
//                linearLayout.addView(button);
//            }
//            l++;
//        }
//        unitListScrollView.addView(linearLayout);
//

        //Check currentdate with schedules

        //if currentdate == scheduledate check time

        //if currenttime == scheduletime send ping to arduino
    }


}

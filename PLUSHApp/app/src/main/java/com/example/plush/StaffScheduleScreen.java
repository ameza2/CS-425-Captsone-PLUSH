// Utility Package //
package com.example.plush;

// Libraries //
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class StaffScheduleScreen extends AppCompatActivity { // StaffScheduleScreen w/ action activities

    Calendar c = Calendar.getInstance();
    SimpleDateFormat date = new SimpleDateFormat("M / d / yyyy");
    String currDate = date.format(c.getTime());

    CalendarView calendarView;
    TextView dateDisplay;
    Button AddButton;
    Button RemoveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_schedule_screen);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        dateDisplay = (TextView) findViewById(R.id.date_display);
        dateDisplay.setText("Schedules for  " + currDate + ":");

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                String newDate = "" + (i1 + 1) + " / " + i2 + " / " + i;
                dateDisplay.setText("Schedules for  " + newDate + ":");

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

        //Check currentdate with schedules

        //if currentdate == scheduledate check time

        //if currenttime == scheduletime send ping to arduino
    }


}

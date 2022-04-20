package com.example.plush;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.plush.data.DataApplication;

import java.util.ArrayList;

public class PatientMusicSelectionScreen extends AppPLUSHActivity{
    Button selectButton; // button variable: song selection button
    ListView listView;
    TextView trackSelected;
    int selected;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_music_selection_screen);

        selectButton = findViewById(R.id.selectbutton);
        trackSelected = findViewById(R.id.currTrack);
        listView = (ListView) findViewById(R.id.songlist);

        //Add songs
        arrayList.add("Sweden by C418");
        arrayList.add("Clark by C418");
        arrayList.add("Haggstrom by C418");
        arrayList.add("Minecraft by C418");
        arrayList.add("Danny by C418");

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                trackSelected.setText(arrayList.get(i));
                selected = i;
            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /* After schedule removal, return to scheduler Screen */
                Intent intent = new Intent(PatientMusicSelectionScreen.this, PatientMusicScreen.class);
                startActivity(intent); // page redirect (StaffScheduleScreen)
            }
        });
    }
}

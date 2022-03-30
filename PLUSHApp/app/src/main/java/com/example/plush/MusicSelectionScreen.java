// Utility Package //
package com.example.plush;

// Libraries //
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MusicSelectionScreen extends AppPLUSHActivity { // MusicSelectionScreen w/ action activities
    Button selectButton; // button variable: song selection button
    ListView listView;
    TextView trackSelected;
    int selected;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;


    /* Initialize Page Activity (Music Selection Screen) */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Create Page //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_selection_screen);

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

        //Change selected song display
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
                switch(selected){
                    case 0:
                        //Log.d("valid", "Music Sweden Selected!");
                        //To be replaced with actual arduino command
                        break;
                    case 1:
                        //Log.d("valid", "Music Clark Selected!");
                        //To be replaced with actual arduino command
                        break;
                    case 2:
                        //Log.d("valid", "Music Haggstrom Selected!");
                        //To be replaced with actual arduino command
                        break;
                    case 3:
                        //Log.d("valid", "Music Minecraft Selected!");
                        //To be replaced with actual arduino command
                        break;
                    case 4:
                        //Log.d("valid", "Music Danny Selected!");
                        //To be replaced with actual arduino command
                        break;
                }

                /* After schedule removal, return to scheduler Screen */
                Intent intent = new Intent(MusicSelectionScreen.this, StaffMusicScreen.class);
                startActivity(intent); // page redirect (StaffScheduleScreen)
            }
        });
    }

    // Back button should redirect to music screen
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(MusicSelectionScreen.this, StaffMusicScreen.class);
        startActivity(intent);
    }
}
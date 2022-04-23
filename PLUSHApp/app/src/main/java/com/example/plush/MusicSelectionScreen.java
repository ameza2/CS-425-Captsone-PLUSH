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

import com.example.plush.data.DataApplication;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MusicSelectionScreen extends AppPLUSHActivity { // MusicSelectionScreen w/ action activities
    Button selectButton; // button variable: song selection button
    ListView listView;
    TextView trackSelected; // text saying what track was selected
    int selected; // keeps track of selected song
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

                if(selected != -1){
                    thisApplication.currUnitData().musicSong = selected;
                    updateMusicSong(selected);
                    DataApplication.connectedThread2.send("SMUS:" + Integer.toString(selected));
                }

                /* After schedule removal, return to scheduler Screen */
                Intent intent = new Intent(MusicSelectionScreen.this, StaffMusicScreen.class);
                startActivity(intent); // page redirect (StaffScheduleScreen)
            }
        });

        if(thisApplication.currUnitData().musicSong != -1 && thisApplication.currUnitData().musicSong < arrayList.size()){
            trackSelected.setText(arrayList.get(thisApplication.currUnitData().musicSong));
            selected = thisApplication.currUnitData().musicSong;
        }
        else{
            thisApplication.currUnitData().musicSong = -1;
            updateMusicSong(-1);
            selected = -1;
        }
    }

    // Update the music selected to the JSON database
    void updateMusicSong(int s){
        try {
            JSONArray inputJSONArray = thisApplication.inputJSON.getJSONArray("userlist");
            for (int i = 0; i < inputJSONArray.length(); i++) {
                if (inputJSONArray.getJSONObject(i).getString("username").equals(thisApplication.currentUser)) {

                    /* Edit unit properties */
                    JSONArray unitJSONArray = inputJSONArray.getJSONObject(i).getJSONArray("units");
                    for(int j = 0; j < unitJSONArray.length(); j++){
                        if(unitJSONArray.getJSONObject(j).getString("id").equals(thisApplication.currentUnit)){
                            unitJSONArray.getJSONObject(j).put("musicSong", s);
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

        } catch (JSONException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Pressing the back button redirects you to the patient music page
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(MusicSelectionScreen.this, StaffMusicScreen.class);
        startActivity(intent);
    }
}
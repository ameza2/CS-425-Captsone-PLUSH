package com.example.plush;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
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

public class PatientMusicScreen extends AppPLUSHActivity{

    Switch musicToggle; // switch variable: used to toggle PLUSH unit music
    public SeekBar volumeBar; // seekbar variable: used to adjust PLUSH unit volume level
    public TextView musicVolumeText; // textview variable: used to indicate PLUSH unit volume level
    Button musicSelection; // button variable: used to select music option (page redirection)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_music_screen);

        musicToggle = findViewById(R.id.musicToggle);
        volumeBar = findViewById(R.id.volumeBar);
        musicSelection = findViewById(R.id.musicselectionButton);
        musicVolumeText = findViewById(R.id.musicvolumeText);


        /* Music Selection Button: redirect user to MusicSelection page */
        musicSelection.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PatientMusicScreen.this, PatientMusicSelectionScreen.class);
                startActivity(intent); // redirect page (MusicSelectionScreen)
            }
        });

        volumeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() { //
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                musicVolumeText.setText("Music Volume: " + String.valueOf(progress + 1)); // print volume level
                updateMusicVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                DataApplication.connectedThread2.send("MVOL:"+Integer.toString(seekBar.getProgress()));
            }
        });

        /* Music Toggle Switch: enable/disable music */
        musicToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String x = isChecked ? "1" : "0";
                DataApplication.connectedThread2.send("PMUS:" + x);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!thisApplication.currentUnit.equals("")) {
            musicVolumeText.setText("Music Volume: " + String.valueOf(thisApplication.currUnitData().musicVolume + 1));
            volumeBar.setProgress(thisApplication.currUnitData().musicVolume);
        }
    }

    void updateMusicVolume(int v){

        try {
            JSONArray inputJSONArray = thisApplication.inputJSON.getJSONArray("userlist");
            for (int i = 0; i < inputJSONArray.length(); i++) {
                if (inputJSONArray.getJSONObject(i).getString("username").equals(thisApplication.currentUser)) {

                    thisApplication.currUnitData().musicVolume = v;
                    /* Edit unit properties */
                    JSONArray unitJSONArray = inputJSONArray.getJSONObject(i).getJSONArray("units");
                    for(int j = 0; j < unitJSONArray.length(); j++){
                        if(unitJSONArray.getJSONObject(j).getString("id").equals(thisApplication.currentUnit)){
                            unitJSONArray.getJSONObject(j).put("musicVolume", v);
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

            //SHOULD SEND TO onStopTrackingTouch function
//                    DataApplication.connectedThread2.send(200 + progress, thisApplication.currentUnit);

        } catch (JSONException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(PatientMusicScreen.this, PatientPlushHomeScreen.class);
        startActivity(intent);
    }
}

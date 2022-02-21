// Utility Package //
package com.example.plush;

// Libraries //
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/* Android Widgets */
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

public class StaffMusicScreen extends AppCompatActivity { // StaffMusicScreen w/ action activities
    Switch musicToggle; // switch variable: used to toggle PLUSH unit music
    SeekBar volumeBar; // seekbar variable: used to adjust PLUSH unit volume level
    TextView musicVolumeText; // textview variable: used to indicate PLUSH unit volume level
    Button musicSelection; // button variable: used to select music option (page redirection)
    DataApplication thisApplication;

    int volume; // variable: used to store PLUSH unit volume level (1 - 100)

    /* Initialize Page Activity (Staff Music Screen) */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_music_screen);

        thisApplication = (DataApplication) getApplication();

        musicToggle = findViewById(R.id.musicToggle);
        volumeBar = findViewById(R.id.volumeBar);
        musicSelection = findViewById(R.id.musicselectionButton);
        musicVolumeText = findViewById(R.id.musicvolumeText);

        /* Set volume bar */
        musicVolumeText .setText("Music Volume: " + String.valueOf(thisApplication.currUnitData().musicVolume));
        volumeBar.setProgress(thisApplication.currUnitData().musicVolume);

        /* Volume Bar: change volume level using widget slider */
        volumeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() { //
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                musicVolumeText.setText("Music Volume: " + String.valueOf(progress)); // print volume level
                volume = progress; // store volume level

                try {
                    JSONArray inputJSONArray = thisApplication.inputJSON.getJSONArray("userlist");
                    for (int i = 0; i < inputJSONArray.length(); i++) {
                        if (inputJSONArray.getJSONObject(i).getString("username").equals(thisApplication.currentUser)) {

                            thisApplication.currUnitData().musicVolume = progress;
                            /* Edit unit properties */
                            JSONArray unitJSONArray = inputJSONArray.getJSONObject(i).getJSONArray("units");
                            for(int j = 0; j < unitJSONArray.length(); j++){
                                if(unitJSONArray.getJSONObject(j).getString("id").equals(thisApplication.currentUnit)){
                                    unitJSONArray.getJSONObject(j).put("musicVolume", progress);
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

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // DO NOTHING //
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // DO NOTHING //
            }
        });

        /* Music Selection Button: redirect user to MusicSelection page */
        musicSelection.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StaffMusicScreen.this, MusicSelectionScreen.class);
                startActivity(intent); // redirect page (MusicSelectionScreen)
            }
        });

        /* Music Toggle Switch: enable/disable music */
        musicToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TO DO: Implement Music Player tied to PLUSH Unit //
            }
        });
    }
}
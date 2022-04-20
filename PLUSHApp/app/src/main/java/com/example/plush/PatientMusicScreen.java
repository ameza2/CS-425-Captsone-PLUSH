package com.example.plush;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;

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
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}

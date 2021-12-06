package com.example.plush;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;

public class StaffMusicScreen extends AppCompatActivity {
    Switch musicToggle;
    SeekBar volumeBar;
    TextView musicVolumeText;
    Button musicSelection;

    int volume;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_music_screen);

        musicToggle = findViewById(R.id.musicToggle);
        volumeBar = findViewById(R.id.volumeBar);
        musicSelection = findViewById(R.id.musicselectionButton);
        musicVolumeText = findViewById(R.id.musicvolumeText);

        volumeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                musicVolumeText.setText("Music Volume: " + String.valueOf(progress));
                volume = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        musicSelection.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(StaffMusicScreen.this, MusicSelectionScreen.class);
                startActivity(intent);
            }
        });

        musicToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Toggle music - once implemented music player
            }
        });
    }
}
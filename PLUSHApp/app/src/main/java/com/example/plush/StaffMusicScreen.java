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

public class StaffMusicScreen extends AppCompatActivity { // StaffMusicScreen w/ action activities
    Switch musicToggle; // switch variable: used to toggle PLUSH unit music
    SeekBar volumeBar; // seekbar variable: used to adjust PLUSH unit volume level
    TextView musicVolumeText; // textview variable: used to indicate PLUSH unit volume level
    Button musicSelection; // button variable: used to select music option (page redirection)

    int volume; // variable: used to store PLUSH unit volume level (1 - 100)

    /* Initialize Page Activity (Staff Music Screen) */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_music_screen);

        musicToggle = findViewById(R.id.musicToggle);
        volumeBar = findViewById(R.id.volumeBar);
        musicSelection = findViewById(R.id.musicselectionButton);
        musicVolumeText = findViewById(R.id.musicvolumeText);

        /* Volume Bar: change volume level using widget slider */
        volumeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() { //
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                musicVolumeText.setText("Music Volume: " + String.valueOf(progress)); // print volume level
                volume = progress; // store volume level
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
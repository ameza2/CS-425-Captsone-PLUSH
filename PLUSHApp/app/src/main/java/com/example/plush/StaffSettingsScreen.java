// Utility Package //
package com.example.plush;

// Libraries //
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/* Android Widgets */
import android.widget.Button;

public class StaffSettingsScreen extends AppCompatActivity { // StaffSettingsScreen w/ action activities

    Button softwareDetailsButton; // button variable: software details button (redirect user to software info page)

    /* Initialize Page Activity (Staff Settings Screen) */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_settings_screen);

        softwareDetailsButton = (Button) findViewById(R.id.SoftwareDetailsButtonID);

        /* Software Details Button: used to redirect user to info page illustrating software properties (i.e., version, patch notes, etc.) */
        softwareDetailsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StaffSettingsScreen.this, staff_SoftwareDetailsScreen.class);
                startActivity(intent); // redirect page (staff_SoftwareDetailsScreen)
            }
        });
    }
}
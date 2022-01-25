// Utility Package //
package com.example.plush;

// Libraries //
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PatientLoginScreen extends AppCompatActivity { // PatientLoginScreen w/ action activities

    Button button; // button variable: login button

    /* Initialize Page Activity (Patient Login Screen) */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Create Page //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_login_screen);

        /* Login Button: redirect user to PatientUnitScreen (StaffPlushUnitScreen (temp)) */
        button = findViewById(R.id.loginbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PatientLoginScreen.this, PatientPlushHomeScreen.class);
                startActivity(intent);
            }
        });
    }
}
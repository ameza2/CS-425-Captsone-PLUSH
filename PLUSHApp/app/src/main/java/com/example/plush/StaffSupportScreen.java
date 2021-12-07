// Utility Package //
package com.example.plush;

// Libraries //
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/* Android Widgets */
import android.widget.Button;

public class StaffSupportScreen extends AppCompatActivity { // StaffSupportScreen w/ action activities

    Button supportButton; // button variable: supportButton (submit support request and redirect user to home screen)

    /* Initialize Page Activity (Staff Support Screen) */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_support_screen);

        supportButton = findViewById(R.id.submitButton);

        /* Support Button: used to submit support request and redirect user to home screen */
        supportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StaffSupportScreen.this, StaffHomeScreen.class);
                startActivity(intent); // page redirection (StaffHomeScreen)
            }
        });
    }
}
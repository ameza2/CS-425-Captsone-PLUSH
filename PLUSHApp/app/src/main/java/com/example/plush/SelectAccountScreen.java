// Utility Package //
package com.example.plush;

// Libraries //
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectAccountScreen extends AppCompatActivity { // SelectAccountScreen w/ action activities

    Button StaffButton; // button initialization: staff
    Button PatientButton; // button initialization: patient

    /* Initialize Page Activity (Account Selection Screen) */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StaffButton = (Button)findViewById(R.id.GoToStaff); // fetch staff button
        PatientButton = (Button)findViewById(R.id.GoToPatient); // fetch patient button

        /* Button Options */
        StaffButton.setOnClickListener(new View.OnClickListener() { // execute on staff button click
            public void onClick(View v) {
                Intent intent = new Intent(SelectAccountScreen.this, StaffLoginScreen.class);
                startActivity(intent); // redirect page (StaffLoginScreen)
            }
        });
        PatientButton.setOnClickListener(new View.OnClickListener() { // execute on patient button click
            public void onClick(View v) {
                Intent intent = new Intent(SelectAccountScreen.this, PatientLoginScreen.class);
                startActivity(intent); // redirect page (PatientLoginScreen)
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}
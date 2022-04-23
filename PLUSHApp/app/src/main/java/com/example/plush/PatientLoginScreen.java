// Utility Package //
package com.example.plush;

// Libraries //
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.plush.data.DataApplication;
import com.example.plush.data.DataUser;

public class PatientLoginScreen extends AppPLUSHActivity { // PatientLoginScreen w/ action activities

    Button button; // button variable: login button
    EditText plushIDEditText; // Text entry for patient login

    /* Initialize Page Activity (Patient Login Screen) */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Create Page //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_login_screen);


        plushIDEditText = (EditText) findViewById(R.id.editTextTextPersonName2);

        /* Login Button: redirect user to PatientUnitScreen (StaffPlushUnitScreen (temp)) */
        button = findViewById(R.id.loginbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(thisApplication.checkBearExists(plushIDEditText.getText().toString())) {

                    // Get user and unit

                    thisApplication.currentUnit = plushIDEditText.getText().toString();

                    for(DataUser user : thisApplication.userDatabase.values()){
                        if(user.assignedUnits.containsKey(plushIDEditText.getText().toString())){
                            thisApplication.currentUser = user.username;
                            break;
                        }
                    }

                    // Start activity
                    Intent intent = new Intent(PatientLoginScreen.this, PatientPlushHomeScreen.class);
                    startActivity(intent);
                }
                else{
                    plushIDEditText.setText("");
                }
            }
        });

        thisApplication.currentUnit = "";
        thisApplication.currentUser = "";
    }

    @Override
    protected void onResume(){
        super.onResume();
        thisApplication.currentUnit = "";
        thisApplication.currentUser = "";
        thisApplication.connectedThread2.disconnectUnit();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(PatientLoginScreen.this, SelectAccountScreen.class);
        startActivity(intent);
    }
}
// Utility Package //
package com.example.plush;

// Libraries //
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/* Android Widgets */
import android.widget.Button;
import android.widget.EditText;

import android.util.Log;

public class StaffSupportScreen extends AppCompatActivity { // StaffSupportScreen w/ action activities

    Button supportButton; // button variable: supportButton (submit support request and redirect user to home screen)
    EditText supportName;
    EditText supportEmail;
    EditText supportDesc;

    /* Initialize Page Activity (Staff Support Screen) */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_support_screen);

        supportButton = findViewById(R.id.submitButton);
        supportName = findViewById(R.id.textName);
        supportEmail = findViewById(R.id.textEmail);
        supportDesc = findViewById(R.id.textDescription);

        /* Support Button: used to submit support request and redirect user to home screen */
        //Referencing solution from https://stackoverflow.com/questions/8994488/android-button-onclick-submit-to-email
        supportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"PLUSHassistance@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "PLUSH Support Request");
                email.putExtra(Intent.EXTRA_TEXT, supportName.getText().toString() + "\n"
                        + supportEmail.getText().toString() + "\n\n"
                        + supportDesc.getText().toString());

                //need this to prompts email client only
                email.setType("message/rfc822");

                //Should launch the user's email app allowing the user to choose which app to send the email.
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });
    }
}
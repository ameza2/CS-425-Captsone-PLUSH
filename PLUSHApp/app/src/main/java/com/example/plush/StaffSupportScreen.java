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
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaffSupportScreen extends AppCompatActivity { // StaffSupportScreen w/ action activities

    private static final String regex = "^(.+)@(.+)$";

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

                boolean emptyName = supportName.getText().toString().isEmpty();
                boolean emptyEmail = supportEmail.getText().toString().isEmpty();
                boolean emptyDesc = supportDesc.getText().toString().isEmpty();

                if ((emptyName && emptyEmail) | (emptyName && emptyDesc) | (emptyEmail && emptyDesc) | (emptyName && emptyEmail && emptyDesc)){
                    Toast.makeText(getApplicationContext(), "Invalid Form Submission: Missing multiple fields.", Toast.LENGTH_LONG).show(); // deactivation prompt
                    //Log.d("Error [1]: ", "Empty Text Field");
                }
                else if (emptyName) { // Input Text Validation: Required Fields
                    Toast.makeText(getApplicationContext(), "Invalid Form Submission: Missing Name.", Toast.LENGTH_LONG).show(); // deactivation prompt
                    //Log.d("Error [2]: ", "Empty Text Field");
                }
                else if (emptyEmail){
                    Toast.makeText(getApplicationContext(), "Invalid Form Submission: Missing Email Address.", Toast.LENGTH_LONG).show(); // deactivation prompt
                    //Log.d("Error [3]: ", "Empty Text Field");
                }
                else if (emptyDesc) {
                    Toast.makeText(getApplicationContext(), "Invalid Form Submission: Missing Message Description.", Toast.LENGTH_LONG).show(); // deactivation prompt
                    //Log.d("Error [4]: ", "Empty Text Field");
                }
                else {

                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(supportEmail.getText().toString());

                    if (!matcher.matches()) {
                        Toast.makeText(getApplicationContext(), "Invalid Email Address", Toast.LENGTH_LONG).show();
                        //Log.d("Error [5]: ", "Invalid Email Address");
                    }
                    else {
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
                }
            }
        });
    }
}
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
import android.widget.Toast;

/* Android Debugger */
import android.util.Log;

/* Regular Expression: Input Validation */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaffSupportScreen extends AppPLUSHActivity { // StaffSupportScreen w/ action activities

    /*

    Modified Version of OWASP Validation Regex Repository

    Local Part Restrictions:
        - Allows numeric values from 0 to 9
        - Allows Uppercase and Lowercase characters
        - Allows Symbols: underscore “_”, hyphen “-”, dot “.”, and plus "+" : no pipe (|) or single quote (') -> prevents SQL Injections
        - Dot "." not allowed at the start and end
        - Consecutive Dots ".." not allowed

    Domain Part Restrictions
        - Allows numeric values from 0 to 9 *
        - Allows Uppercase and Lowercase characters
        - Allows Symbols: hyphen "-" and dot "." : no pipe (|) or single quote (') -> prevents SQL Injections
        - Dot “.” not allowed at the start and end
        - Hyphen “-” not allowed at end (maybe have this start too)
        - Consecutive Dots ".." not allowed
    */

    private static final String regex = "^[a-zA-Z0-9_+-]+(?:\\.[a-zA-Z0-9_+-]+)*@" // local part regular expression
            + "(?:[^-.]+[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"; // domain part regular expression

    Button supportButton; // button variable: supportButton (submit support request and redirect user to home screen)
    EditText supportName; // textbox variable: supportName (store user name)
    EditText supportEmail; // textbox variable: supportEmail (store user email)
    EditText supportDesc; // textbox variable: supportDesc (store support message contents)

    /* Initialize Page Activity (Staff Support Screen) */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_support_screen);

        // Fetch Button and Textbox States //

        supportButton = findViewById(R.id.submitButton);
        supportName = findViewById(R.id.textName);
        supportEmail = findViewById(R.id.textEmail);
        supportDesc = findViewById(R.id.textDescription);

        /* Support Button: used to submit support request and redirect user to home screen */
        // Referencing solution from https://stackoverflow.com/questions/8994488/android-button-onclick-submit-to-email //
        supportButton.setOnClickListener(new View.OnClickListener() { // form submission activity
            public void onClick(View v) {

                // Input Validation Checks //

                boolean emptyName = supportName.getText().toString().isEmpty(); // boolean: check for empty 'Name' field
                boolean emptyEmail = supportEmail.getText().toString().isEmpty(); // boolean: check for empty 'Email' field
                boolean emptyDesc = supportDesc.getText().toString().isEmpty(); // boolean: check for empty 'Message' field

                if ((emptyName && emptyEmail) | (emptyName && emptyDesc) | (emptyEmail && emptyDesc) | (emptyName && emptyEmail && emptyDesc)) { // if statement: if multiple input fields are empty, promopt error message to user
                    Toast.makeText(getApplicationContext(), "Invalid Form Submission: Missing multiple fields.", Toast.LENGTH_LONG).show(); // error message confirmation
                    //Log.d("Error [1]: ", "Empty Text Field");
                }
                else if (emptyName) { // else if statement: else if 'Name' field is empty, prompt error message
                    Toast.makeText(getApplicationContext(), "Invalid Form Submission: Missing Name.", Toast.LENGTH_LONG).show(); // error message confirmation
                    //Log.d("Error [2]: ", "Empty Text Field");
                }
                else if (emptyEmail) { // else if statement: else if 'Email' field is empty, prompt error message
                    Toast.makeText(getApplicationContext(), "Invalid Form Submission: Missing Email Address.", Toast.LENGTH_LONG).show(); // error message confirmation
                    //Log.d("Error [3]: ", "Empty Text Field");
                }
                else if (emptyDesc) { // else if statement: else if 'Message' field is empty, prompt error message
                    Toast.makeText(getApplicationContext(), "Invalid Form Submission: Missing Message Description.", Toast.LENGTH_LONG).show(); // error message confirmation
                    //Log.d("Error [4]: ", "Empty Text Field");
                }
                else { // else statement: else if input fields passed validation check, proceed with form submission process

                    // Email Validation Algorithm using Regular Expressions //

                    Pattern pattern = Pattern.compile(regex); // compile regular expression
                    Matcher matcher = pattern.matcher(supportEmail.getText().toString()); // check regular expression against email input

                    if (!matcher.matches()) { // if statement: if email input does not pass email validation, prompt error message
                        Toast.makeText(getApplicationContext(), "Invalid Email Address", Toast.LENGTH_LONG).show(); // error message confirmation (invalid email)
                        //Log.d("Error [5]: ", "Invalid Email Address");
                    }
                    else { // else statement: else if email input passes email validation, submit form using email sharing feature
                        Intent email = new Intent(Intent.ACTION_SEND); // android share action
                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"PLUSHassistance@gmail.com"}); // target email address (PLUSH)
                        email.putExtra(Intent.EXTRA_SUBJECT, "PLUSH Support Request"); // email subject line (SUPPORT)
                        email.putExtra(Intent.EXTRA_TEXT, supportName.getText().toString() + "\n" // email content (include user name and email for future contact)
                                + supportEmail.getText().toString() + "\n\n"
                                + supportDesc.getText().toString());

                        email.setType("message/rfc822"); // prompts email client only

                        startActivity(Intent.createChooser(email, "Choose an Email client :")); // launch user's email app allowing the user to choose which app to send the email
                    }
                }
            }
        });
    }
}
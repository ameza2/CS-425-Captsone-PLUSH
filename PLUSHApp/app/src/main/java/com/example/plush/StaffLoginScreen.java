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

/* DataApplication File */
import com.example.plush.data.DataApplication;

public class StaffLoginScreen extends AppPLUSHActivity {

    Button LoginButton; // button variable: login button (redirect user to home page if credentials pass)
    EditText UsernameEditText; // text variable: used to store user username
    EditText PasswordEditText; // text variable: used to store user password

    /* Initialize Page Activity (Staff Login Screen) */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_login_screen);

        LoginButton = (Button)findViewById(R.id.LoginButton);
        UsernameEditText = (EditText) findViewById(R.id.editTextTextPersonName);
        PasswordEditText = (EditText) findViewById(R.id.editTextTextPassword);


        /* Login Button: fetch username and password string, then conduct a credential check.
        If username and password are valid entries (same hash value (TBD)), redirect staff user to home screen */
        LoginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(thisApplication.checkCredentials(UsernameEditText.getText().toString(), PasswordEditText.getText().toString())) {
                    thisApplication.currentUser = UsernameEditText.getText().toString();

                    // SECURITY ALGORITHM //
                    //PasswordEditText = createSHAHash(PasswordEditText.getText().toString());
                    thisApplication.onStaffSide = true;
                    Intent intent = new Intent(StaffLoginScreen.this, StaffHomeScreen.class);
                    startActivity(intent); // redirect page (StaffHomeScreen)
                }
                else {
                    UsernameEditText.setText(""); // clear username field
                    PasswordEditText.setText(""); // clear password field
                }
            }
        });
    }
}
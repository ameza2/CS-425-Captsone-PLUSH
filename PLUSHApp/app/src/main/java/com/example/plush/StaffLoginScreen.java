package com.example.plush;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.plush.data.DataApplication;

public class StaffLoginScreen extends AppCompatActivity {

    Button LoginButton;
    EditText UsernameEditText;
    EditText PasswordEditText;

    DataApplication thisApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_login_screen);

        LoginButton = (Button)findViewById(R.id.LoginButton);
        UsernameEditText = (EditText) findViewById(R.id.editTextTextPersonName);
        PasswordEditText = (EditText) findViewById(R.id.editTextTextPassword);

        thisApplication = (DataApplication)getApplication();

        LoginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(thisApplication.checkCredentials(UsernameEditText.getText().toString(), PasswordEditText.getText().toString())){
                    thisApplication.currentUser = UsernameEditText.getText().toString();
                    Intent intent = new Intent(StaffLoginScreen.this, StaffHomeScreen.class);
                    startActivity(intent);
                }
                else{
                    UsernameEditText.setText("");
                    PasswordEditText.setText("");
                }
            }
        });




    }

}
package com.example.plush;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plush.data.DataApplication;

public class AppPLUSHActivity extends AppCompatActivity {

    protected DataApplication thisApplication;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisApplication = (DataApplication) this.getApplicationContext();
        if(!(this instanceof StaffHomeScreen)) {
            ActionBar actionBar = getSupportActionBar();
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FF3A9B80"));
            actionBar.setBackgroundDrawable(colorDrawable);
        }

    }
    protected void onResume() {
        super.onResume();
        thisApplication.setCurrActivity(this);
    }
    protected void onPause() {
        clearReferences();
        super.onPause();
    }
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences(){
        Activity currActivity = thisApplication.getCurrActivity();
        if (this.equals(currActivity))
            thisApplication.setCurrActivity(null);
    }
}

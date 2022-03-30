package com.example.plush;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plush.data.DataApplication;

public class AppPLUSHActivity extends AppCompatActivity {

    protected DataApplication thisApplication;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisApplication = (DataApplication) this.getApplicationContext();
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

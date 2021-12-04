package com.example.plush;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.plush.data.DataApplication;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class StaffHomeScreen extends AppCompatActivity {

    ScrollView unitListScrollView;
    Button AddButton;
    Button RemoveButton;
    DataApplication thisApplication;

    //Hamburger menu declarations
    DrawerLayout hamburgerLayout;
    ActionBarDrawerToggle actionBarHamburgerToggle;
    Toolbar toolbar;
    NavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_home_screen);

        //Hamburger Menu
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        hamburgerLayout = findViewById(R.id.hamburger);
        navigationView = findViewById(R.id.navigationView);

        actionBarHamburgerToggle = new ActionBarDrawerToggle(this, hamburgerLayout, toolbar, R.string.open, R.string.close);
        hamburgerLayout.addDrawerListener(actionBarHamburgerToggle);
        actionBarHamburgerToggle.setDrawerIndicatorEnabled(true);
        actionBarHamburgerToggle.syncState();


        unitListScrollView = (ScrollView)findViewById(R.id.scrollview);

        AddButton = (Button)findViewById(R.id.addButton);
        AddButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(StaffHomeScreen.this, StaffAddUnitScreen.class);
                startActivity(intent);
            }
        });

        RemoveButton = (Button)findViewById(R.id.removeButton);
        RemoveButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(StaffHomeScreen.this, StaffRemoveUnitScreen.class);
                startActivity(intent);
            }
        });

        thisApplication = (DataApplication)getApplication();

        // Load unit data
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(params);

        ArrayList<Button> buttonList = new ArrayList<>();
        int j = 0;

        for(String i: thisApplication.currUserData().assignedUnits.keySet()) {
            String rn = thisApplication.currUserData().assignedUnits.get(i).room;
            String un = thisApplication.currUserData().assignedUnits.get(i).id;


            buttonList.add(new Button(this));
            Button button = buttonList.get(j);
            button.setText("ROOM " + rn + "\nPLUSH #" + un);
            button.setTextSize(1, 30);
            button.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            button.setLayoutParams(params);
            linearLayout.addView(button);

            j++;
        }

        unitListScrollView.addView(linearLayout);
        navigationView.getParent().requestLayout();



    }
}

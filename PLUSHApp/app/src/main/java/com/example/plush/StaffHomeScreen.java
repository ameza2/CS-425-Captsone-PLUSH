package com.example.plush;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
    ArrayList<Button> buttonList = new ArrayList<>();
    ArrayList<String> unitIDList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_home_screen);

        //Hamburger Menu
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        hamburgerLayout = findViewById(R.id.hamburger);
        navigationView = findViewById(R.id.navigationView);
        //Updated the actionbar to override the ondraweropened and ondrawer closed methods.
        //this allows for us to detect when the sidebar is opened so we can stop displaying the plush buttons and redisplay when closed.
        actionBarHamburgerToggle = new ActionBarDrawerToggle(this, hamburgerLayout, toolbar, R.string.open, R.string.close){
            @Override
            public void onDrawerOpened(View v){
                super.onDrawerOpened(v);
                findViewById(R.id.scrollview).setVisibility(View.INVISIBLE);
                findViewById(R.id.addButton).setVisibility(View.INVISIBLE);
                findViewById(R.id.removeButton).setVisibility(View.INVISIBLE);
            }
            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
                findViewById(R.id.scrollview).setVisibility(View.VISIBLE);
                findViewById(R.id.addButton).setVisibility(View.VISIBLE);
                findViewById(R.id.removeButton).setVisibility(View.VISIBLE);
            }
        };
        hamburgerLayout.addDrawerListener(actionBarHamburgerToggle);
        actionBarHamburgerToggle.setDrawerIndicatorEnabled(true);
        actionBarHamburgerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerContent(navigationView);

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
        //Important as add/edit unit use the same screen
        thisApplication.currentUnit = "";

        thisApplication.connectedThread2.disconnectUnit();

        // Load unit data
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(params);


        int j = 0;

        for(String i: thisApplication.currUserData().assignedUnits.keySet()) {
            String rn = thisApplication.currUserData().assignedUnits.get(i).room;
            String un = thisApplication.currUserData().assignedUnits.get(i).id;


            buttonList.add(new Button(this));
            unitIDList.add(un);

            Button button = buttonList.get(j);
            button.setText("ROOM " + rn + "\nPLUSH #" + un);
            button.setTextSize(1, 30);
            button.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            button.setLayoutParams(params);
            linearLayout.addView(button);
            button.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Intent intent = new Intent(StaffHomeScreen.this, StaffPlushUnitScreen.class);
                    int ind = buttonList.indexOf(button);
                    thisApplication.currentUnit = unitIDList.get(ind);
                    startActivity(intent);
                }
            });
            j++;
        }

        unitListScrollView.addView(linearLayout);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                hamburgerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

    }
    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Intent intent;
        switch(menuItem.getItemId()) {
            case R.id.hamSettings:
                intent = new Intent(StaffHomeScreen.this, StaffSettingsScreen.class);
                break;
            case R.id.hamFeedback:
                intent = new Intent(StaffHomeScreen.this, StaffFeedbackScreen.class);
                break;
            case R.id.hamSupport:
                intent = new Intent(StaffHomeScreen.this, StaffSupportScreen.class);
                break;
            case R.id.hamLogout:
                intent = new Intent(StaffHomeScreen.this, SelectAccountScreen.class);
                break;
            default:
                intent = new Intent(StaffHomeScreen.this, StaffSettingsScreen.class);
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        startActivity(intent);
        hamburgerLayout.closeDrawers();

    }

    // Back button caused a few bugs on this screen, so I disabled it.
    @Override
    public void onBackPressed(){
    }
}

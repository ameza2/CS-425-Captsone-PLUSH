package com.example.plush;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plush.data.DataApplication;

import java.util.ArrayList;
import java.util.List;

public class StaffHomeScreen extends AppCompatActivity {

    ScrollView unitListScrollView;

    DataApplication thisApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_home_screen);

        unitListScrollView = (ScrollView)findViewById(R.id.scrollview);

        thisApplication = (DataApplication)getApplication();

        // Load unit data
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(params);

        ArrayList<Button> buttonList = new ArrayList<>();
        int j = 0;

        for(int i: thisApplication.currUserData().assignedUnits.keySet()) {
            String rn = Integer.toString(thisApplication.currUserData().assignedUnits.get(i).room);
            String un = Integer.toString(thisApplication.currUserData().assignedUnits.get(i).id);


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


    }
}

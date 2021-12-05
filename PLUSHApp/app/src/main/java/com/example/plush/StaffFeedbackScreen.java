package com.example.plush;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class StaffFeedbackScreen extends AppCompatActivity {

    TextView tvFeedback;
    RatingBar ratingStars;
    Button feedbackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_feedback_screen);

        tvFeedback = findViewById(R.id.tvFeedback);
        ratingStars = findViewById(R.id.ratingBar);
        feedbackButton = findViewById(R.id.feedbackButton);

        ratingStars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating == 0){
                    tvFeedback.setText("Very Dissatisfied");
                }
                else if(rating == 1){
                    tvFeedback.setText("Dissatisfied");
                }
                else if(rating == 2){
                    tvFeedback.setText("Ok");
                }
                else if(rating == 3){
                    tvFeedback.setText("Decent");
                }
                else if(rating == 4){
                    tvFeedback.setText("Satisfied");
                }
                else if(rating == 5){
                    tvFeedback.setText("Very Satisfied");
                }
                else{

                }
            }
        });

        feedbackButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(StaffFeedbackScreen.this, StaffHomeScreen.class);
                startActivity(intent);
            }
        });

    }


}
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
import android.widget.RatingBar;
import android.widget.TextView;

public class StaffFeedbackScreen extends AppPLUSHActivity { // StaffFeedbackScreen w/ action activities

    /* Variable Initialization (feedback options) */
    TextView tvFeedback;
    RatingBar ratingStars;
    Button feedbackButton;
    EditText feedbackContent;

    int stars;

    /* Initialize Page Activity (Staff Feedback Screen) */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_feedback_screen);

        tvFeedback = findViewById(R.id.tvFeedback);
        ratingStars = findViewById(R.id.ratingBar);
        feedbackButton = findViewById(R.id.feedbackButton);
        feedbackContent = findViewById(R.id.textName);

        ratingStars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() { // star bar options (used to record user satisfaction)
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) { // rating options (1-5)
                if (rating == 0) { // if statement: if rating == 0: user is "very dissatisfied"
                    tvFeedback.setText("Very Dissatisfied"); // rating confirmation
                }
                else if (rating == 1) { // else if statement: if rating == 1: user is "dissatisfied"
                    tvFeedback.setText("Dissatisfied"); // rating confirmation
                }
                else if (rating == 2) { // else if statement: if rating == 2: user is "ok"
                    tvFeedback.setText("Ok"); // rating confirmation
                }
                else if (rating == 3) { // else if statement: if rating == 3: user is "Decent"
                    tvFeedback.setText("Decent"); // rating confirmation
                }
                else if (rating == 4) { // else if statement: if rating == 4: user is "Satisfied"
                    tvFeedback.setText("Satisfied"); // rating confirmation
                }
                else if (rating == 5) { // else if statement: if rating == 5: user is "Very Satisfied"
                    tvFeedback.setText("Very Satisfied"); // rating confirmation
                }
                else{ // else statement: else if rating is not available, wait for user input
                    // DO NOTHING //
                }

                stars = (int)Math.ceil(rating);
            }
        });

        /* Submit Feedback Button: submit feedback entry for product maintenance (TO DO: must be stored/sent somewhere) */
        //Referencing solution from https://stackoverflow.com/questions/8994488/android-button-onclick-submit-to-email
        feedbackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"PLUSHassistance@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "PLUSH Feedback: " + stars + "/5 star rating");
                email.putExtra(Intent.EXTRA_TEXT, feedbackContent.getText().toString());

                //need this to prompts email client only
                email.setType("message/rfc822");

                //Should launch the user's email app allowing the user to choose which app to send the email.
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });

    }
}
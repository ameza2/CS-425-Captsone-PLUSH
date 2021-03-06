// Utility Package //
package com.example.plush;

// Libraries //
import androidx.appcompat.app.AppCompatActivity;
import com.example.plush.data.DataApplication;

import android.content.Intent;
import android.os.Bundle;

/* Android Widgets */
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/* Json Objects */
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* File Manipulation */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import android.widget.Toast;

public class StaffAddUnitScreen extends AppPLUSHActivity { // StaffAddUnitScreen w/ action activities

    EditText IDEditText; // text variable: used to store PLUSH PID
    EditText RoomEditText; // text variable: used to store patient room/bed number
    EditText AgeEditText; // text variable: used to store patient's age
    Button AddUnitButton; // button variable: addUnit button (append PLUSH unit to account)
    RadioGroup sexGroup; // button group variable: used to identify patient sex from group of buttons
    RadioButton sexButton; // button group variable: used to store patient sex

    /* Initialize Page Activity (Add PLUSH Unit Screen) */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_add_unit_screen);

        IDEditText = (EditText) findViewById(R.id.editUnitID);
        RoomEditText = (EditText) findViewById(R.id.editRoomNumber);
        AgeEditText = (EditText) findViewById(R.id.editDate1);
        AddUnitButton = (Button) findViewById(R.id.buttonAddSchedule);
        sexGroup = (RadioGroup) findViewById(R.id.radioGroup);

        int sexID = sexGroup.getCheckedRadioButtonId(); // fetch sex option from button input
        sexButton = findViewById(sexID);


        /* Add Unit Button: Create a string using PLUSH properties, and append PLUSH unit to user's database entry */
        AddUnitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                boolean emptyID = IDEditText.getText().toString().isEmpty();
                boolean emptyRoom = RoomEditText.getText().toString().isEmpty();

                int emptySex = sexGroup.getCheckedRadioButtonId(); // empty value == -1

                //Log.d("Empty ID? ", "" + emptyID);
                //Log.d("Empty Room? ", "" + emptyRoom);
                //Log.d("Empty Sex? ", "" + emptySex);

                if ((emptyID && emptyRoom) | (emptyID && (emptySex == -1)) | (emptyRoom && (emptySex == -1)) | (emptyID && emptyRoom && (emptySex == -1))){
                    Toast.makeText(getApplicationContext(), "Invalid Form Submission: Missing multiple fields.", Toast.LENGTH_LONG).show(); // deactivation prompt
                    //Log.d("Error [1]: ", "Empty Text Field");
                }
                else if (emptyID) { // Input Text Validation: Required Fields
                    Toast.makeText(getApplicationContext(), "Invalid Form Submission: Missing PLUSH Unit ID.", Toast.LENGTH_LONG).show(); // deactivation prompt
                    //Log.d("Error [2]: ", "Empty Text Field");
                }
                else if (emptyRoom){
                    Toast.makeText(getApplicationContext(), "Invalid Form Submission: Missing Room/Bed Number.", Toast.LENGTH_LONG).show(); // deactivation prompt
                    //Log.d("Error [3]: ", "Empty Text Field");
                }
                else if (emptySex == -1) {
                    Toast.makeText(getApplicationContext(), "Invalid Form Submission: Missing Patient Sex.", Toast.LENGTH_LONG).show(); // deactivation prompt
                    //Log.d("Error [4]: ", "Empty Text Field");
                }
                else {
                    //Log.d("Success:", "Valid Text Fields");

                    thisApplication.currUserData().addUnit(IDEditText.getText().toString(), RoomEditText.getText().toString(), Integer.parseInt(AgeEditText.getText().toString()), sexGroup.getCheckedRadioButtonId());

                    /* Update JSON File */
                    try {
                        JSONArray inputJSONArray = thisApplication.inputJSON.getJSONArray("userlist");
                        for (int i = 0; i < inputJSONArray.length(); i++) {
                            if (inputJSONArray.getJSONObject(i).getString("username").equals(thisApplication.currentUser)) {

                                /* Add unit properties to array */
                                JSONArray unitJSONArray = inputJSONArray.getJSONObject(i).getJSONArray("units");
                                JSONObject toPut = new JSONObject();
                                JSONArray empty = new JSONArray();
                                toPut.put("id", IDEditText.getText().toString());
                                toPut.put("room", RoomEditText.getText().toString());
                                toPut.put("age", Integer.parseInt(AgeEditText.getText().toString()));
                                toPut.put("sex", sexGroup.getCheckedRadioButtonId());
                                toPut.put("hugSensitivity", 4);
                                toPut.put("musicVolume", 50);
                                toPut.put("musicSong", -1);
                                toPut.put("hugSchedule", empty);
                                toPut.put("musicSchedule", empty);
                                toPut.put("otherSchedule", empty);
                                unitJSONArray.put(toPut);

                                /* Save new string to user database */
                                File f = new File(thisApplication.getFilesDir(), "userdatabase.json");
                                OutputStream outputStream = new FileOutputStream(f);
                                byte outputBytes[] = thisApplication.inputJSON.toString().getBytes(StandardCharsets.UTF_8);
                                outputStream.write(outputBytes);
                                outputStream.close();
                            }
                        }
                    } catch (JSONException | FileNotFoundException e) { // error-handling statement
                        e.printStackTrace();
                    } catch (IOException e) { // error-handling statement
                        e.printStackTrace();
                    }

                    /* After JSON Update, Return to Home Page w/ Updated PLUSH Unit */
                    Intent intent = new Intent(StaffAddUnitScreen.this, StaffHomeScreen.class);
                    startActivity(intent); // redirect page (StaffHomeScreen)

                }
            }
        });
    }
}
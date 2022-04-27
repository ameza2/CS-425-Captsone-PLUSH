// Utility Package //
package com.example.plush;

// Libraries //

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.plush.data.DataPlushUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class StaffEditUnitScreen extends AppPLUSHActivity { // StaffAddUnitScreen w/ action activities

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
        setContentView(R.layout.activity_staff_edit_unit_screen);

        IDEditText = (EditText) findViewById(R.id.editUnitID);
        RoomEditText = (EditText) findViewById(R.id.editRoomNumber);
        AgeEditText = (EditText) findViewById(R.id.editDate1);
        AddUnitButton = (Button) findViewById(R.id.buttonAddSchedule);
        sexGroup = (RadioGroup) findViewById(R.id.radioGroup);

        int sexID = sexGroup.getCheckedRadioButtonId(); // fetch sex option from button input
        sexButton = findViewById(sexID);

        IDEditText.setText(thisApplication.currUnitData().id);
        RoomEditText.setText(thisApplication.currUnitData().room);
        AgeEditText.setText(String.valueOf(thisApplication.currUnitData().age));

        sexGroup.check(thisApplication.currUnitData().sex);

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


                    /* Since the data uses a hashmap, have to replace old */
                    String oldID = thisApplication.currUnitData().id;

                    String newID = IDEditText.getText().toString();
                    String newRoom = RoomEditText.getText().toString();
                    int newAge = Integer.parseInt(AgeEditText.getText().toString());
                    int newSex = sexGroup.getCheckedRadioButtonId();

                    // If no ID Changes were made
                    if(oldID.equals(newID)){
                        thisApplication.currUnitData().room = newRoom;
                        thisApplication.currUnitData().age = newAge;
                        thisApplication.currUnitData().sex = newSex;

                        try {
                            JSONArray inputJSONArray = thisApplication.inputJSON.getJSONArray("userlist");
                            for (int i = 0; i < inputJSONArray.length(); i++) {
                                if (inputJSONArray.getJSONObject(i).getString("username").equals(thisApplication.currentUser)) {

                                    /* Edit unit properties */
                                    JSONArray unitJSONArray = inputJSONArray.getJSONObject(i).getJSONArray("units");
                                    for(int j = 0; j < unitJSONArray.length(); j++){
                                        if(unitJSONArray.getJSONObject(j).getString("id").equals(thisApplication.currentUnit)){
                                            unitJSONArray.getJSONObject(j).put("room", newRoom);
                                            unitJSONArray.getJSONObject(j).put("age", newAge);
                                            unitJSONArray.getJSONObject(j).put("sex", newSex);
                                        }
                                    }

                                    /* Save new string to user database */
                                    File f = new File(thisApplication.getFilesDir(), "userdatabase.json");
                                    OutputStream outputStream = new FileOutputStream(f);
                                    byte outputBytes[] = thisApplication.inputJSON.toString().getBytes(StandardCharsets.UTF_8);
                                    outputStream.write(outputBytes);
                                    outputStream.close();
                                }
                            }

//                    DataApplication.connectedThread2.send(100 + progress, thisApplication.currentUnit);
                            /* After JSON Update, Return to Home Page w/ Updated PLUSH Unit */
                            Intent intent = new Intent(StaffEditUnitScreen.this, StaffPlushUnitScreen.class);
                            startActivity(intent); // redirect page (StaffPlushUnitScreen)

                        } catch (JSONException | FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    // If the ID was modified
                    else{
                        thisApplication.getCurrActivity().runOnUiThread(new Runnable() {
                                                       @Override
                                                       public void run() {
                                                           // Taken from: https://stackoverflow.com/questions/26097513/android-simple-alert-dialog
                                                           AlertDialog alertDialog = new AlertDialog.Builder(thisApplication.getCurrActivity()).create();
                                                           alertDialog.setTitle("Warning");
                                                           alertDialog.setMessage("Changing the unit ID will disconnect it from the current unit. Are you sure you want to modify the ID?");

                                                           alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                                   new DialogInterface.OnClickListener() {
                                                                       public void onClick(DialogInterface dialog, int which) {

                                                                           DataPlushUnit newUnit = thisApplication.currUnitData();
                                                                           thisApplication.currUserData().assignedUnits.remove(oldID);
                                                                           newUnit.id = newID;
                                                                           newUnit.room = newRoom;
                                                                           newUnit.age = newAge;
                                                                           newUnit.sex = newSex;
                                                                           thisApplication.currUserData().assignedUnits.put(newID, newUnit);

                                                                           /* Update JSON File */
                                                                           try {
                                                                               JSONArray inputJSONArray = thisApplication.inputJSON.getJSONArray("userlist");
                                                                               for (int i = 0; i < inputJSONArray.length(); i++) {
                                                                                   if (inputJSONArray.getJSONObject(i).getString("username").equals(thisApplication.currentUser)) {

                                                                                       /* Edit unit properties */
                                                                                       JSONArray unitJSONArray = inputJSONArray.getJSONObject(i).getJSONArray("units");
                                                                                       for (int j = 0; j < unitJSONArray.length(); j++) {
                                                                                           if (unitJSONArray.getJSONObject(j).getString("id").equals(oldID)) {
                                                                                               unitJSONArray.getJSONObject(j).put("id", newID);
                                                                                               unitJSONArray.getJSONObject(j).put("room", newRoom);
                                                                                               unitJSONArray.getJSONObject(j).put("age", newAge);
                                                                                               unitJSONArray.getJSONObject(j).put("sex", newSex);
                                                                                           }
                                                                                       }

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

                                                                           dialog.dismiss();

                                                                           /* After JSON Update, Return to Home Page w/ Updated PLUSH Unit */
                                                                           thisApplication.currentUnit = newID;
                                                                           thisApplication.connectedThread2.disconnectUnit();
                                                                           Intent intent = new Intent(StaffEditUnitScreen.this, StaffPlushUnitScreen.class);
                                                                           startActivity(intent); // redirect page (StaffPlushUnitScreen)
                                                                       }
                                                                   });
                                                           alertDialog.show();
                                                       }
                                                   }
                        );
                    }

                    if(false) { // 200 IQ commenting out
                        thisApplication.currUserData().assignedUnits.remove(oldID);
                        //String newID = IDEditText.getText().toString();
                        //String newRoom = RoomEditText.getText().toString();
                        //thisApplication.currUserData().addUnit(newID, newRoom);
                        thisApplication.currentUnit = newID;

                        /* Update JSON File */
                        try {
                            JSONArray inputJSONArray = thisApplication.inputJSON.getJSONArray("userlist");
                            for (int i = 0; i < inputJSONArray.length(); i++) {
                                if (inputJSONArray.getJSONObject(i).getString("username").equals(thisApplication.currentUser)) {

                                    /* Edit unit properties */
                                    JSONArray unitJSONArray = inputJSONArray.getJSONObject(i).getJSONArray("units");
                                    for (int j = 0; j < unitJSONArray.length(); j++) {
                                        if (unitJSONArray.getJSONObject(j).getString("id").equals(oldID)) {
                                            unitJSONArray.getJSONObject(j).put("id", newID);
                                            unitJSONArray.getJSONObject(j).put("room", newRoom);
                                        }
                                    }

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
                    }

                }
            }
        });
    }
}
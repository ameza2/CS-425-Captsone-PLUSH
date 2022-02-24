package com.example.plush.data;

import android.app.Application;
import android.content.res.AssetManager;
import android.icu.util.Output;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class DataApplication extends Application {

    public static ConnectedThread2 connectedThread2;
    public static CreateConnectThread2 createConnectThread2;

    public HashMap<String, DataUser> userDatabase;
    public String currentUser;
    public String currentUnit;
    public JSONObject inputJSON;

    @Override
    public void onCreate() {
        super.onCreate();

        // Create thread
        createConnectThread2 = new CreateConnectThread2();
        createConnectThread2.start();

        // Read in json file
        String inputString = "";
        try {
            File f = new File(getFilesDir(), "userdatabase.json");
            if(!f.exists()) {
                f.createNewFile();
            }
            InputStream inputStream = new FileInputStream(f);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line + System.lineSeparator());
            }
            inputString = stringBuilder.toString();
            inputStream.close();

            if(stringBuilder.length() <= 5){ // Weird bug, temp solution
                InputStream inputStream2 = getAssets().open("userdatabase.json");
                BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(inputStream2));
                StringBuilder stringBuilder2 = new StringBuilder();

                String line2;
                while((line2 = bufferedReader2.readLine()) != null){
                    stringBuilder2.append(line2 + System.lineSeparator());
                }

                inputString = stringBuilder2.toString();

                inputStream2.close();

                OutputStream outputStream = new FileOutputStream(f);
                byte outputBytes[] = inputString.getBytes(StandardCharsets.UTF_8);
                outputStream.write(outputBytes);
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            inputJSON = new JSONObject(inputString);
            JSONArray inputJSONArray = inputJSON.getJSONArray("userlist");
            userDatabase = new HashMap<String, DataUser>();
            for(int i = 0; i < inputJSONArray.length(); i++){

                String user = inputJSONArray.getJSONObject(i).getString("username");
                String pass = inputJSONArray.getJSONObject(i).getString("password");
                userDatabase.put(user, new DataUser(user, pass));

                DataUser currUser = userDatabase.get(user);

                if(inputJSONArray.getJSONObject(i).has("units")){
                    JSONArray inputUnitArray = inputJSONArray.getJSONObject(i).getJSONArray("units");
                    for(int j = 0; j < inputUnitArray.length(); j++){
                        String id = inputUnitArray.getJSONObject(j).getString("id");
                        String room = inputUnitArray.getJSONObject(j).getString("room");
                        int hug = inputUnitArray.getJSONObject(j).getInt("hugSensitivity");
                        int vol = inputUnitArray.getJSONObject(j).getInt("musicVolume");
                        currUser.assignedUnits.put(id, new DataPlushUnit(id, room, hug, vol));
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Checks to see if user exists; used for login
    public boolean checkCredentials(String inUsername, String inPassword){
        return userDatabase.get(inUsername) != null && inPassword.equals(userDatabase.get(inUsername).password);
    }

    // Checks to see if a unit with the ID exists
    public boolean checkBearExists(String bearID){
        for(DataUser user : userDatabase.values()){
            for(DataPlushUnit plush : user.assignedUnits.values()){
                if(plush.id.equals(bearID)){
                    return true;
                }
            }
        }
        return false;
    }

    // Grabs the current user
    public DataUser currUserData(){
        return userDatabase.get(currentUser);
    }

    // Grabs the current unit
    public DataPlushUnit currUnitData(){return userDatabase.get(currentUser).assignedUnits.get(currentUnit); }


    // Stuff copy and pasted from website: https://iotdesignpro.com/projects/create-android-app-with-android-studio-to-control-led-over-wifi-using-nodemcu
    public class CreateConnectThread2 extends Thread {
        public CreateConnectThread2() {

        }
        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            Log.e("Status", "Device connected");
            connectedThread2 = new ConnectedThread2();
            connectedThread2.run();
        }
    }

    public class ConnectedThread2 extends Thread {
        public ConnectedThread2() {
            Log.e("Status", "Device connected");
        }
        public void run() {
            Log.e("Status", "Device running");
        }
        public void send(int cmdText, String ipaddress) {
            Log.e("Status", "Sending data "+cmdText);
            String url = "https://"+ipaddress+"/post";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("Status", response.trim());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Status", error.toString());
                        }
                    }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params= new HashMap<String, String>();
                    String value = String.valueOf(cmdText);
                    params.put("data",value);
                    Log.e("Status", value);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(DataApplication.this);
            requestQueue.add(stringRequest);
        }
    }
}

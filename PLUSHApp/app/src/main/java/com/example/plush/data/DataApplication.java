package com.example.plush.data;

import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.icu.util.Output;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.plush.StaffMusicScreen;
import com.example.plush.StaffPlushUnitScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/* Security Library */
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

public class DataApplication extends Application {

    public static ConnectedThread2 connectedThread2;
    public static CreateConnectThread2 createConnectThread2;

    public HashMap<String, DataUser> userDatabase;
    public String currentUser;
    public String currentUnit;
    public JSONObject inputJSON;
    public DataSchedule scheduler;
    boolean alertRecieved = false; // Determines if the application recieved an alert
    public boolean firstTime = false; // Determines whether the conneciton popup should be displayed.
    public boolean onStaffSide = false; // True if a staff member is logged in.


    // ACTIVITY CODE: Will allow the application to do things with the current activity
    private Activity currActivity = null;
    public Activity getCurrActivity(){return currActivity;}
    public void setCurrActivity(Activity a){currActivity = a;}


    @Override
    public void onCreate() {
        super.onCreate();

        scheduler = new DataSchedule();

        // Needed for UDP to work
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

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
                        int age = inputUnitArray.getJSONObject(j).getInt("age");
                        int sex = inputUnitArray.getJSONObject(j).getInt("sex");
                        int hug = inputUnitArray.getJSONObject(j).getInt("hugSensitivity");
                        int vol = inputUnitArray.getJSONObject(j).getInt("musicVolume");
                        int song = inputUnitArray.getJSONObject(j).getInt("musicSong");

                        JSONArray hugArray = inputUnitArray.getJSONObject(j).getJSONArray("hugSchedule");
                        JSONArray musicArray = inputUnitArray.getJSONObject(j).getJSONArray("musicSchedule");
                        JSONArray otherArray = inputUnitArray.getJSONObject(j).getJSONArray("otherSchedule");
                        ArrayList<String> h = convertToArrayList(hugArray);
                        ArrayList<String> m = convertToArrayList(musicArray);
                        ArrayList<String> o = convertToArrayList(otherArray);

                        currUser.assignedUnits.put(id, new DataPlushUnit(id, room, age, sex, hug, vol, song, h, m, o));
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> convertToArrayList(JSONArray j){
        ArrayList<String> s = new ArrayList<>();
        for(int i = 0; i < j.length(); i++){
            try {
                s.add(j.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return s;
    }

    public void saveNewSchedule(String scheduleToSave){
        ArrayList<String> arrayToUse;
        switch (scheduleToSave){
            case "hugSchedule":
                arrayToUse = currUnitData().hugSchedule;
                break;
            case "musicSchedule":
                arrayToUse = currUnitData().musicSchedule;
                break;
            case "otherSchedule":
                arrayToUse = currUnitData().otherSchedule;
                break;
            default:
                return;
        }

        JSONArray jsonToUse = new JSONArray();
        for(int i = 0; i < arrayToUse.size(); i++){
            jsonToUse.put(arrayToUse.get(i));
        }

        try {
            JSONArray inputJSONArray = inputJSON.getJSONArray("userlist");
            for (int i = 0; i < inputJSONArray.length(); i++) {
                if (inputJSONArray.getJSONObject(i).getString("username").equals(currentUser)) {

                    /* Edit unit properties */
                    JSONArray unitJSONArray = inputJSONArray.getJSONObject(i).getJSONArray("units");
                    for(int j = 0; j < unitJSONArray.length(); j++){
                        if(unitJSONArray.getJSONObject(j).getString("id").equals(currentUnit)){
                            unitJSONArray.getJSONObject(j).put(scheduleToSave, jsonToUse);
                        }
                    }

                    /* Save new string to user database */
                    File f = new File(getFilesDir(), "userdatabase.json");
                    OutputStream outputStream = new FileOutputStream(f);
                    byte outputBytes[] = inputJSON.toString().getBytes(StandardCharsets.UTF_8);
                    outputStream.write(outputBytes);
                    outputStream.close();
                }
            }
        } catch (JSONException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static byte[] createSHAHash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    private static String toHexString(final byte[] messageDigest) {
        BigInteger bigint = new BigInteger(1, messageDigest);
        String hexText = bigint.toString(16);
        while (hexText.length() < 32) {
            hexText = "0".concat(hexText);
        }

        return hexText.toString();
    }

    // Checks to see if user exists; used for login
    public boolean checkCredentials(String inUsername, String inPassword){
        //Log.d("Username: ", inUsername);
        //Log.d("Password: ", inPassword);

        String hashedString = null;

        try {
            hashedString = toHexString(createSHAHash(inPassword));

            //Log.d("Hashed Password:", hashedString);
            //Log.d("Stored Password:", userDatabase.get(inUsername).password);
        }
        catch (NoSuchAlgorithmException e) {
            System.err.println("Error: Invalid Digest Algorithm");
        }

        return userDatabase.get(inUsername) != null && (hashedString.equals(userDatabase.get(inUsername).password) | hashedString.toUpperCase().equals(userDatabase.get(inUsername).password));
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

        public ReentrantLock lock = new ReentrantLock();
        private int connectAttempts = 0;
        private String msgToSend;
        private String ipToSend;
        private int portToSend;
        public String currentIP = "";

        private ArrayList<String> msgQueue = new ArrayList<>();

        public ConnectedThread2() {
            Log.e("Status", "Device connected");
        }

        public void run() {
            Log.e("Status", "Device running");

            while(true){
                synchronized (lock){
                    if (connectAttempts != 0) {
                        try {
                            byte[] data = ("CONN:" + msgToSend).getBytes();
                            byte[] dataRecieved = new byte[256];

                            InetAddress addr = InetAddress.getByName(ipToSend);

                            DatagramPacket request = new DatagramPacket(data, data.length, addr, portToSend);
                            DatagramPacket recieved = new DatagramPacket(dataRecieved, dataRecieved.length);

                            DatagramSocket socket = new DatagramSocket();
                            socket.setSoTimeout(1000);

                            socket.send(request);
                            socket.receive(recieved);

                            String s = new String(recieved.getData(), "UTF-8");
                            s = s.substring(0, s.indexOf(0));
                            Log.e("Message", s);
                            connectAttempts = 0;
                            currentIP = s;

                        } catch (SocketTimeoutException ste) {
                            ste.printStackTrace();
                            connectAttempts--;
                            Log.e("Connection", "Attempt failed, " + connectAttempts + " attempts remain.");
                        } catch (SocketException se) {
                            se.printStackTrace();
                            connectAttempts--;
                            Log.e("Connection", "Attempt failed, " + connectAttempts + " attempts remain.");
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                            connectAttempts--;
                            Log.e("Connection", "Attempt failed, " + connectAttempts + " attempts remain.");
                        }
                    } else if (!currentIP.equals("") && !currentUnit.equals("")) {
                        if (!msgQueue.isEmpty()) {
                            try {
                                byte[] data = msgQueue.get(0).getBytes();
                                byte[] dataRecieved = new byte[256];

                                InetAddress addr = InetAddress.getByName(currentIP);

                                DatagramPacket request = new DatagramPacket(data, data.length, addr, portToSend);
                                DatagramPacket recieved = new DatagramPacket(dataRecieved, dataRecieved.length);

                                DatagramSocket socket = new DatagramSocket();
                                socket.setSoTimeout(200);

                                socket.send(request);
                                socket.receive(recieved);

                                String s = new String(recieved.getData(), "UTF-8");
                                s = s.substring(0, s.indexOf(0));
                                Log.e("Message", s);
                                msgQueue.remove(0);

                            } catch (SocketTimeoutException ste) {
                                ste.printStackTrace();
                                Log.e("Connection", "Attempt failed, " + connectAttempts + " attempts remain.");
                            } catch (SocketException se) {
                                se.printStackTrace();
                                Log.e("Connection", "Attempt failed, " + connectAttempts + " attempts remain.");
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                                Log.e("Connection", "Attempt failed, " + connectAttempts + " attempts remain.");
                            }
                        }
                        else {
                            try {
                                String x = "UPDT: HS-" + Integer.toString(currUnitData().hugSensitivity) + "/MV-" + Integer.toString(currUnitData().musicVolume)
                                        + "/AL-" + Integer.toString((alertRecieved ? 1 : 0)) + "/MU-" + (currUnitData().musicSong == -1 ? "*" : Integer.toString(currUnitData().musicSong));
                                byte[] data = x.getBytes();
                                byte[] dataRecieved = new byte[256];

                                InetAddress addr = InetAddress.getByName(currentIP);

                                DatagramPacket request = new DatagramPacket(data, data.length, addr, portToSend);
                                DatagramPacket recieved = new DatagramPacket(dataRecieved, dataRecieved.length);

                                DatagramSocket socket = new DatagramSocket();
                                socket.setSoTimeout(200);

                                socket.send(request);
                                socket.receive(recieved);

                                String s = new String(recieved.getData(), "UTF-8");
                                s = s.substring(0, s.indexOf(0));
                                //Log.e("Message", s);

                                // PARSING
                                String newHug = "";
                                String newVol = "";
                                String newAlr = "";
                                int t = 0;
                                for(int i = 0; i < s.length(); i++){
                                    char c = s.charAt(i);
                                    if(c == '/'){
                                        t += 1;
                                    }
                                    if(c == '\0'){
                                        break;
                                    }
                                    if(Character.isDigit(c)){
                                        switch (t){
                                            case 0:
                                                newHug += c;
                                                break;
                                            case 1:
                                                newVol += c;
                                                break;
                                            case 2:
                                                newAlr += c;
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }

                                int nH = Integer.parseInt(newHug);
                                int nV = Integer.parseInt(newVol);
                                int nA = Integer.parseInt(newAlr);

                                if(nH != currUnitData().hugSensitivity) {

                                    currUnitData().hugSensitivity = nH;
                                    JSONArray inputJSONArray = inputJSON.getJSONArray("userlist");
                                    for (int i = 0; i < inputJSONArray.length(); i++) {
                                        if (inputJSONArray.getJSONObject(i).getString("username").equals(currentUser)) {

                                            /* Edit unit properties */
                                            JSONArray unitJSONArray = inputJSONArray.getJSONObject(i).getJSONArray("units");
                                            for (int j = 0; j < unitJSONArray.length(); j++) {
                                                if (unitJSONArray.getJSONObject(j).getString("id").equals(currentUnit)) {
                                                    unitJSONArray.getJSONObject(j).put("hugSensitivity", nH);
                                                }
                                            }

                                            /* Save new string to user database */
                                            File f = new File(getFilesDir(), "userdatabase.json");
                                            OutputStream outputStream = new FileOutputStream(f);
                                            byte outputBytes[] = inputJSON.toString().getBytes(StandardCharsets.UTF_8);
                                            outputStream.write(outputBytes);
                                            outputStream.close();
                                        }
                                    }

                                    if(currActivity instanceof StaffPlushUnitScreen){
                                        currActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ((StaffPlushUnitScreen) currActivity).sensitivityText.setText("Hug Sensitivity: " + String.valueOf(currUnitData().hugSensitivity));
                                                ((StaffPlushUnitScreen) currActivity).sensitivityBar.setProgress(currUnitData().hugSensitivity);
                                            }
                                        });
                                    }
                                }
                                if(nV != currUnitData().musicVolume){
                                    JSONArray inputJSONArray = inputJSON.getJSONArray("userlist");
                                    for (int i = 0; i < inputJSONArray.length(); i++) {
                                        if (inputJSONArray.getJSONObject(i).getString("username").equals(currentUser)) {

                                            currUnitData().musicVolume = nV;
                                            /* Edit unit properties */
                                            JSONArray unitJSONArray = inputJSONArray.getJSONObject(i).getJSONArray("units");
                                            for (int j = 0; j < unitJSONArray.length(); j++) {
                                                if (unitJSONArray.getJSONObject(j).getString("id").equals(currentUnit)) {
                                                    unitJSONArray.getJSONObject(j).put("musicVolume", nV);
                                                }
                                            }

                                            /* Save new string to user database */
                                            File f = new File(getFilesDir(), "userdatabase.json");
                                            OutputStream outputStream = new FileOutputStream(f);
                                            byte outputBytes[] = inputJSON.toString().getBytes(StandardCharsets.UTF_8);
                                            outputStream.write(outputBytes);
                                            outputStream.close();
                                        }
                                    }

                                    if(currActivity instanceof StaffMusicScreen) {
                                        currActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ((StaffMusicScreen)currActivity).musicVolumeText.setText("Music Volume: " + String.valueOf(currUnitData().musicVolume + 1));
                                                ((StaffMusicScreen)currActivity).volumeBar.setProgress(currUnitData().musicVolume);
                                            }
                                        });
                                    }
                                }

                                if((alertRecieved && nA == 0) || (!alertRecieved && nA != 0)){// nA != (alertRecieved ? 1 : 0)){
                                    if(nA >= 1) {
                                        Log.e("ALERT", "Alert recieved!");

                                        if (onStaffSide) {
                                            currActivity.runOnUiThread(new Runnable() {
                                                                           @Override
                                                                           public void run() {
                                                                               // Taken from: https://stackoverflow.com/questions/26097513/android-simple-alert-dialog
                                                                               AlertDialog alertDialog = new AlertDialog.Builder(currActivity).create();
                                                                               alertDialog.setTitle("Alert");
                                                                               if (nA == 2) {
                                                                                   alertDialog.setMessage("The patient has pressed the release button.");
                                                                               } else {
                                                                                   alertDialog.setMessage("The patient has called for a nurse.");
                                                                               }
                                                                               alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                                                       new DialogInterface.OnClickListener() {
                                                                                           public void onClick(DialogInterface dialog, int which) {
                                                                                               send("ACKA:0");
                                                                                               dialog.dismiss();
                                                                                           }
                                                                                       });
                                                                               alertDialog.show();
                                                                           }
                                                                       }
                                            );
                                        }
                                    }
                                    alertRecieved = (nA >= 1);

                                }

                                lock.wait(200);

                            } catch (SocketTimeoutException ste) {
                                ste.printStackTrace();
                                Log.e("Connection", "Attempt failed, " + connectAttempts + " attempts remain.");
                            } catch (SocketException se) {
                                se.printStackTrace();
                                Log.e("Connection", "Attempt failed, " + connectAttempts + " attempts remain.");
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                                Log.e("Connection", "Attempt failed, " + connectAttempts + " attempts remain.");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (JSONException e){

                            }
                        }
                    }
                }
            }
        }

        public void send(String cmdText) {
            synchronized (lock) {
                msgQueue.add(cmdText);
            }
        }

        public void sendUDP(String cmdText, String ipaddress, int port){

            synchronized (lock){
                currentIP = "";
                connectAttempts = 5;
                msgToSend = cmdText;
                ipToSend = ipaddress;
                portToSend = port;
                msgQueue.clear();
            }
        }

        public void disconnectUnit(){
            synchronized (lock){
                currentIP = "";
                msgQueue.clear();
            }
        }

        public int checkIfFinitshed(){
            int stat = 0;
            lock.lock();
            try {
                if(connectAttempts == 0){
                    if(currentIP.equals("")){
                        stat = -1;
                    }
                    else{
                        stat = 1;
                    }
                }
            } finally {
                lock.unlock();
            }
            return stat;
        }
    }
}

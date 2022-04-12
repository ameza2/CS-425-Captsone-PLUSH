package com.example.plush.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class DataSchedule {


    public class ListedSchedule{
        public Timer timer;
        public String timeString;
        public String command;

        ListedSchedule(String time, String comm){
            timeString = time;
            command = comm;
            timer = new Timer();
            Timer t = new Timer();

            int mo = Integer.parseInt(timeString.split("/")[0]);
            int da = Integer.parseInt(timeString.split("/")[1]);
            int ye = Integer.parseInt(timeString.split("/")[2].split(",")[0]);
            int hr = Integer.parseInt(timeString.split(",")[1].split(":")[0]);
            int mn = Integer.parseInt(timeString.split(":")[1].split(" ")[0]);
            int am = timeString.split(" ")[1].equals("AM") ? 0 : 12;

            Calendar cal = Calendar.getInstance();
            cal.set(ye, mo - 1, da, hr + am, mn);
            Log.e("TIME", cal.getTime().toString());
            Date date = cal.getTime();



            t.schedule(
                    new TimerTask()
                    {
                        public void run()
                        {
                            Log.e("SCHEDULE", command);
                            DataApplication.connectedThread2.send(command);
                        }
                    },
                    date);      // run task on date
        }
    }

    ArrayList<ListedSchedule> hugTimers;
    ArrayList<ListedSchedule> musicTimers;
    ArrayList<ListedSchedule> otherTimers;

    DataSchedule(){
        hugTimers = new ArrayList<>();
        musicTimers = new ArrayList<>();
        otherTimers = new ArrayList<>();
    }

    public void AddSchedule(String dateAndTime, int type){
        switch(type){
            case 0: // hug
                hugTimers.add(new ListedSchedule(dateAndTime, "HUGP:1"));
                break;
            case 1: // music
                musicTimers.add(new ListedSchedule(dateAndTime, "PMUS:1"));
                break;
            case 2: // other
                // Nothing here yet
                //otherTimers.add(new ListedSchedule(dateAndTime));
                break;
            default:
                break;
        }
    }

    public void RemoveSchedule(String dateAndTime, int type){
        switch(type){
            case 0: // hug
                for(int i = 0; i < hugTimers.size(); i++){
                    if(dateAndTime == hugTimers.get(i).timeString){
                        hugTimers.get(i).timer.cancel();
                        hugTimers.remove(i);
                        break;
                    }
                }
                break;
            case 1: // music
                for(int i = 0; i < musicTimers.size(); i++){
                    if(dateAndTime == musicTimers.get(i).timeString){
                        musicTimers.get(i).timer.cancel();
                        musicTimers.remove(i);
                        break;
                    }
                }
                break;
            case 2: // other
                // Nothing here yet
                break;
            default:
                break;
        }
    }

    public void RemoveAll(){
        for(int i = 0; i < hugTimers.size(); i++){
            hugTimers.get(i).timer.cancel();
        }
        for(int i = 0; i < musicTimers.size(); i++){
            musicTimers.get(i).timer.cancel();
        }
        hugTimers = new ArrayList<>();
        musicTimers = new ArrayList<>();
    }

    public boolean IsEmpty(){
        return (hugTimers.size() == 0 && musicTimers.size() == 0);
    }
}

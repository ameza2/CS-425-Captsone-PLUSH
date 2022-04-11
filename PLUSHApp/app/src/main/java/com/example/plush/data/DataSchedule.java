package com.example.plush.data;

import java.util.ArrayList;
import java.util.Timer;

public class DataSchedule {

    ArrayList<Timer> hugTimers;
    ArrayList<Timer> musicTimers;
    ArrayList<Timer> otherTimers;

    DataSchedule(){
        hugTimers = new ArrayList<>();
        musicTimers = new ArrayList<>();
        otherTimers = new ArrayList<>();
    }

    void AddSchedule(String dateAndTime, int type){
        Timer t = new Timer();
        switch(type){
            case 0: // hug
                break;
            case 1: // music
                break;
            case 2: // other
                break;
            default:
                break;
        }
    }

    void RemoveSchedule(String dateAndTime, int type){
        switch(type){
            case 0: // hug
                break;
            case 1: // music
                break;
            case 2: // other
                break;
            default:
                break;
        }
    }
}

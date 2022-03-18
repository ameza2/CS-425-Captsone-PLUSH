package com.example.plush.data;

import java.util.ArrayList;
import java.util.HashMap;

public class DataPlushUnit {
    DataPlushUnit(String i, String r){
        id = i;
        room = r;

        hugSensitivity = 4;
        musicVolume = 50;
        hugSchedule = new ArrayList<>();
        musicSchedule = new ArrayList<>();
        otherSchedule = new ArrayList<>();
    }

    DataPlushUnit(String i, String r, int h, int m, ArrayList<String> hug, ArrayList<String> music, ArrayList<String> other){
        id = i;
        room = r;

        hugSensitivity = h;
        musicVolume = m;
        hugSchedule = new ArrayList<>(hug);
        musicSchedule = new ArrayList<>(music);
        otherSchedule = new ArrayList<>(other);
    }

    public String id;
    public String room;

    public ArrayList<String> hugSchedule;
    public ArrayList<String> musicSchedule;
    public ArrayList<String> otherSchedule;

    public int hugSensitivity;
    public int musicVolume;
}

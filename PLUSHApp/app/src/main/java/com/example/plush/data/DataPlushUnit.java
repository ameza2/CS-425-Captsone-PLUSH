package com.example.plush.data;

import java.util.ArrayList;
import java.util.HashMap;

public class DataPlushUnit {
    DataPlushUnit(String i, String r, int a, int s){
        id = i;
        room = r;

        hugSensitivity = 4;
        musicVolume = 50;
        age = a;
        sex = s;
        musicSong = -1;
        hugSchedule = new ArrayList<>();
        musicSchedule = new ArrayList<>();
        otherSchedule = new ArrayList<>();
    }

    DataPlushUnit(String i, String r, int ag, int se, int h, int m, int s, ArrayList<String> hug, ArrayList<String> music, ArrayList<String> other){
        id = i;
        room = r;
        age = ag;
        sex = se;

        hugSensitivity = h;
        musicVolume = m;
        musicSong = s;
        hugSchedule = new ArrayList<>(hug);
        musicSchedule = new ArrayList<>(music);
        otherSchedule = new ArrayList<>(other);
    }

    public String id;
    public String room;
    public int age;
    public int sex;
    public ArrayList<String> hugSchedule;
    public ArrayList<String> musicSchedule;
    public ArrayList<String> otherSchedule;

    public int hugSensitivity;
    public int musicVolume;
    public int musicSong;
}

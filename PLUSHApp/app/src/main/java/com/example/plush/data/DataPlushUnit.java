package com.example.plush.data;

public class DataPlushUnit {
    DataPlushUnit(String i, String r){
        id = i;
        room = r;

        hugSensitivity = 4;
        musicVolume = 50;
    }

    DataPlushUnit(String i, String r, int h, int m){
        id = i;
        room = r;

        hugSensitivity = h;
        musicVolume = m;
    }

    public String id;
    public String room;

    public int hugSensitivity;
    public int musicVolume;
}

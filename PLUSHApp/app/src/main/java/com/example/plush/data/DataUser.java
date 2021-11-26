package com.example.plush.data;

import java.util.HashMap;

public class DataUser {

    DataUser(String un, String pw){
        username = un;
        password = pw;
        assignedUnits = new HashMap<>();
    }

    public String username;
    public String password;
    public HashMap<Integer, DataPlushUnit> assignedUnits;
}

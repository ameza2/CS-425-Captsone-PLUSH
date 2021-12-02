package com.example.plush.data;

import java.util.HashMap;

public class DataUser {

    DataUser(String un, String pw){
        username = un;
        password = pw;
        assignedUnits = new HashMap<>();
    }

    public void addUnit(String id, String rn){
        assignedUnits.put(id, new DataPlushUnit(id, rn));
    }
    public void removeUnit(String id){assignedUnits.remove(id);}

    public String username;
    public String password;
    public HashMap<String, DataPlushUnit> assignedUnits;
}

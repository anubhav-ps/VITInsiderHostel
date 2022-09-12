package com.anubhav.vitinsiderhostel.models;

import java.util.ArrayList;

public class TravelNetworkList {
    private ArrayList<String> list;

    public TravelNetworkList() {
    }

    public TravelNetworkList(ArrayList<String> list) {
        this.list = list;
    }

    public ArrayList<String> getList() {
        return list;
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }
}

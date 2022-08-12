package com.anubhav.vitinsiderhostel.models;

import java.util.List;

public class RoomTenants {
    private List<String> list;

    public RoomTenants() {
    }

    public RoomTenants(List<String> list) {
        this.list = list;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}

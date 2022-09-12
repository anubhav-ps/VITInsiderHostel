package com.anubhav.vitinsiderhostel.models;

public class PNR_Result {
    private boolean status;
    private PNR_Data data;

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public PNR_Data getData() {
        return data;
    }

    public void setData(PNR_Data data) {
        this.data = data;
    }
}

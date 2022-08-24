package com.anubhav.vitinsiderhostel.enums;

import androidx.annotation.NonNull;

public enum Urgency {
    LOW("LOW", "No Action Required"),
    HIGH("HIGH", "Needs Immediate Action");

    private final String status;
    private final String description;


    Urgency(String status, String description) {
        this.status = status;
        this.description = description;
    }

    @NonNull
    @Override
    public String toString() {
        return this.status;
    }

    public String getDescription() {
        return this.description;
    }


}

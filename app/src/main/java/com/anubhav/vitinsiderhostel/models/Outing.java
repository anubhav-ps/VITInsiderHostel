package com.anubhav.vitinsiderhostel.models;


import java.util.Date;

public class Outing {

    private Date timeStamp;

    private String date;
    private String day;
    private String month;
    private String year;
    private String status;

    private boolean FRESHERS;
    private boolean SOPHOMORES;
    private boolean JUNIORS;
    private boolean SENIORS;

    private int duration;

    public Outing() {
        this.FRESHERS = false;
        this.SOPHOMORES = false;
        this.JUNIORS = false;
        this.SENIORS = false;
        this.duration = 4;
    }

    public Outing(Date timeStamp, String date, String day, String month, String year, String status, boolean FRESHERS, boolean SOPHOMORES, boolean JUNIORS, boolean SENIORS, int duration) {
        this.timeStamp = timeStamp;
        this.date = date;
        this.day = day;
        this.month = month;
        this.year = year;
        this.status = status;
        this.FRESHERS = FRESHERS;
        this.SOPHOMORES = SOPHOMORES;
        this.JUNIORS = JUNIORS;
        this.SENIORS = SENIORS;
        this.duration = duration;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isFRESHERS() {
        return FRESHERS;
    }

    public void setFRESHERS(boolean FRESHERS) {
        this.FRESHERS = FRESHERS;
    }

    public boolean isSOPHOMORES() {
        return SOPHOMORES;
    }

    public void setSOPHOMORES(boolean SOPHOMORES) {
        this.SOPHOMORES = SOPHOMORES;
    }

    public boolean isJUNIORS() {
        return JUNIORS;
    }

    public void setJUNIORS(boolean JUNIORS) {
        this.JUNIORS = JUNIORS;
    }

    public boolean isSENIORS() {
        return SENIORS;
    }

    public void setSENIORS(boolean SENIORS) {
        this.SENIORS = SENIORS;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}

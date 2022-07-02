package com.anubhav.vitinsiderhostel.models;

import com.anubhav.vitinsiderhostel.enums.OutingStatus;

public class Outing {

    private String date;
    private String day;
    private String month;
    private String year;
    private String status;
    private String openTime;
    private String closeTime;
    private String duration;

    public Outing() {
        status = OutingStatus.NOT_ALLOWED.toString();
        openTime = "00:00";
        closeTime = "00:00";
        duration = "0";
    }

    public Outing(String date, String day, String month, String year, String status, String openTime, String closeTime, String duration) {
        this.date = date;
        this.day = day;
        this.month = month;
        this.year = year;
        this.status = status;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.duration = duration;
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

    public String getOpenTime() {
        return openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public String getDuration() {
        return duration;
    }

}

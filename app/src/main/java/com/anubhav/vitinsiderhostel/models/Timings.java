package com.anubhav.vitinsiderhostel.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Timings {


    public Timings() {
    }


    public static Date getMinCheckOutTime(Date date) {
        final String minCheckOut = " 07:00:00 ";
        return parseTimeToDate(date, minCheckOut);
    }

    public static Date getMaxCheckOutTime(Date date) {
        final String maxCheckOut = " 17:00:00 ";
        return parseTimeToDate(date, maxCheckOut);
    }


    public static Date getMaxCheckInTime(Date date) {
        final String maxCheckIn = " 18:00:00 ";
        return parseTimeToDate(date, maxCheckIn);
    }

    public static Date getMinCheckInTime(Date date) {
        final String minCheckIn = " 08:00:00 ";
        return parseTimeToDate(date, minCheckIn);
    }




    //working fine
    public static Date toIST_TimeStamp(Date date) {

        DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
        TimeZone istTimeZone = TimeZone.getTimeZone("IST");
        formatter.setTimeZone(istTimeZone);
        String stringTimeStamp = formatter.format(date);
        try {
            return formatter.parse(stringTimeStamp);
        } catch (ParseException e) {
            return null;
        }

    }

    //working fine
    public static Date parseTimeToDate(Date referenceDate, String time) {

        final String firstHalf = "EEE MMM dd";
        final String lastHalf = "yyyy";

        String stringTime;
        SimpleDateFormat firstHalfFormatter = new SimpleDateFormat(firstHalf, Locale.ENGLISH);
        SimpleDateFormat lastHalfFormatter = new SimpleDateFormat(lastHalf, Locale.ENGLISH);
        stringTime = firstHalfFormatter.format(referenceDate) + time + lastHalfFormatter.format(referenceDate);

        final String istPattern = "EEE MMM dd HH:mm:ss yyyy";
        SimpleDateFormat formatToDate = new SimpleDateFormat(istPattern, Locale.ENGLISH);

        try {
            return formatToDate.parse(stringTime);
        } catch (ParseException e) {
            return null;
        }

    }

    public static Date getVisitDateTimeStamp(String dateString) {
        final String pattern = "dd MMM yyyy";
        DateFormat formatter = new SimpleDateFormat(pattern, Locale.ENGLISH);

        try {
            Date date = formatter.parse(dateString);
            date = parseTimeToDate(date, " 00:00:00 ");
            return toIST_TimeStamp(date);
        } catch (ParseException e) {
            return null;
        }


    }


    public static String formatToDateString(Date date) {

        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        return formatter.format(date);

    }

    public static String formatToTimeString(Date date) {

        DateFormat formatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        return formatter.format(date);

    }


}

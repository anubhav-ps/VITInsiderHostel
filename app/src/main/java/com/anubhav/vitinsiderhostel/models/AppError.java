package com.anubhav.vitinsiderhostel.models;

import com.anubhav.vitinsiderhostel.enums.ErrorCode;
import com.anubhav.vitinsiderhostel.enums.Mod;
import com.anubhav.vitinsiderhostel.enums.TicketStatus;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;

import java.util.Date;

public class AppError {

    private String errorCode;
    private String errorMessage;
    private String reporter;
    private String userType;

    private String status;
    private String userContactNumber;
    private Timestamp timestamp;
    private static boolean flag = false;

    public AppError() {
    }


    public AppError(String errorCode, String reporter) {
        this.errorCode = errorCode;
        this.reporter = reporter;
        this.timestamp = new Timestamp(new Date());
        this.errorMessage = null;
        this.userType = "STUDENT";
        this.userContactNumber = null;
        this.status = TicketStatus.BOOKED.toString();
    }

    public AppError(String errorMessage, String reporter, String userContactNumber, Timestamp timestamp) {
        this.errorMessage = errorMessage;
        this.reporter = reporter;
        this.userContactNumber = userContactNumber;
        this.timestamp = timestamp;

        this.errorCode = ErrorCode.OTH.getErrorCode();
        this.userType = "STUDENT";
        this.status = TicketStatus.BOOKED.toString();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserContactNumber() {
        return userContactNumber;
    }

    public void setUserContactNumber(String userContactNumber) {
        this.userContactNumber = userContactNumber;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public static boolean isFlag() {
        return flag;
    }

    public static void setFlag(boolean flag) {
        AppError.flag = flag;
    }
}


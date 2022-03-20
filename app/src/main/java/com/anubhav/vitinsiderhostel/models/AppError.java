package com.anubhav.vitinsiderhostel.models;

import com.anubhav.vitinsiderhostel.enums.ErrorCode;
import com.google.firebase.Timestamp;

public class AppError {

    private String errorCode;
    private String reporter;
    private String userType;
    private Timestamp timestamp;

    public AppError() {
    }

    public AppError(ErrorCode errorCode, String reporter, String userType, Timestamp timestamp) {
        this.errorCode = errorCode.toString();
        this.reporter = reporter;
        this.userType = userType;
        this.timestamp = timestamp;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}


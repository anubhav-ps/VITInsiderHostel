package com.anubhav.vitinsiderhostel.models;

import com.anubhav.vitinsiderhostel.enums.ErrorCode;
import com.anubhav.vitinsiderhostel.enums.TicketStatus;
import com.google.firebase.Timestamp;

public class AppError {

    private String errorCode;
    private String errorMessage;
    private String reporter;
    private String userType;


    private String status;
    private String userContactNumber;
    private Timestamp timestamp;

    public AppError() {
    }


    public AppError(String errorCode, String reporter, Timestamp timestamp) {
        this.errorCode = errorCode;
        this.reporter = reporter;
        this.timestamp = timestamp;

        this.errorMessage = null;
        this.userType = "STUDENT";
        this.userContactNumber = null;
        this.status = TicketStatus.BOOKED.toString();
    }

    public AppError(String errorMessage, String reporter,String userContactNumber, Timestamp timestamp) {
        this.errorMessage = errorMessage;
        this.reporter = reporter;
        this.userContactNumber = userContactNumber;
        this.timestamp = timestamp;

        this.errorCode = ErrorCode.OTH.getErrorCode();
        this.userType = "STUDENT";
        this.status = TicketStatus.BOOKED.toString();
    }

}


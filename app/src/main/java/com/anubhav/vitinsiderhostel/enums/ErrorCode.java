package com.anubhav.vitinsiderhostel.enums;

public enum ErrorCode {

    RA001("RA001","User not a hosteler!"),
    RA002("RA002","Account couldn't be created!"),
    RA003("RA003","Room details couldn't be fetched!"),
    RA004("RA004","User details couldn't be uploaded!"),
    RA005("RA005","Tenant details couldn't be updated!"),
    RA006("RA006","Verification link couldn't not be sent!"),

    LA001("LA001","User not a hosteler anymore"),

    EPF001("EPF001","Updating user section failed!"),
    EPF002("EPF002","Updating tenant section failed!"),


    DF001("DF001","User Details couldn't be erased!"),
    DF002("DF002","User Account ID couldn't be deleted!"),

    ORF001("ORF001","Outing Status Link Couldn't Be Created"),
    ORF002("ORF002","Outing Form Couldn't Be Raised"),

    RF001("",""),
    RF002("",""),
    RF003("",""),


    OTH("NO CODE"," ");

    final String errorCode;
    final String errorMessage;

    ErrorCode(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "Error "+this.errorCode+" ,"+this.errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}





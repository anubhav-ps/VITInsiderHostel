package com.anubhav.vitinsiderhostel.models;

import com.google.firebase.Timestamp;

public class IssuedOREK {
    private String docId;
    private String studentName;
    private String studentRegisterNumber;
    private String checkOutTime;
    private String checkInTime;
    private Timestamp hasCheckedOut;
    private Timestamp hasCheckedIn;
    private Timestamp issuedOn;
    private String visitDate;
    private String validity;

    public IssuedOREK() {
    }

    public IssuedOREK(String docId, String studentName, String studentRegisterNumber, String checkOutTime, String checkInTime, Timestamp hasCheckedOut, Timestamp hasCheckedIn, Timestamp issuedOn, String visitDate, String validity) {
        this.docId = docId;
        this.studentName = studentName;
        this.studentRegisterNumber = studentRegisterNumber;
        this.checkOutTime = checkOutTime;
        this.checkInTime = checkInTime;
        this.hasCheckedOut = hasCheckedOut;
        this.hasCheckedIn = hasCheckedIn;
        this.issuedOn = issuedOn;
        this.visitDate = visitDate;
        this.validity = validity;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentRegisterNumber() {
        return studentRegisterNumber;
    }

    public void setStudentRegisterNumber(String studentRegisterNumber) {
        this.studentRegisterNumber = studentRegisterNumber;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Timestamp getHasCheckedOut() {
        return hasCheckedOut;
    }

    public void setHasCheckedOut(Timestamp hasCheckedOut) {
        this.hasCheckedOut = hasCheckedOut;
    }

    public Timestamp getHasCheckedIn() {
        return hasCheckedIn;
    }

    public void setHasCheckedIn(Timestamp hasCheckedIn) {
        this.hasCheckedIn = hasCheckedIn;
    }

    public Timestamp getIssuedOn() {
        return issuedOn;
    }

    public void setIssuedOn(Timestamp issuedOn) {
        this.issuedOn = issuedOn;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }
}

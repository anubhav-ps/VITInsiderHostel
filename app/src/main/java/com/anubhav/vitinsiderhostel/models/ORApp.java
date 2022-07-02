package com.anubhav.vitinsiderhostel.models;

import com.google.firebase.Timestamp;

public class ORApp {

    private String studentName;
    private String studentRegisterNumber;
    private String studentMailId;
    private String studentContactNumber;
    private String studentRoomDetails;
    private String parentNumber;
    private String visitLocation;
    private String visitPurpose;
    private String visitDate;
    private String checkOut;
    private String checkIn;

    private String oraDocId;
    private String oraStatus;
    private Timestamp uploadTimestamp;

    public ORApp() {
    }

    public ORApp(String studentName, String studentRegisterNumber, String studentMailId, String studentContactNumber, String studentRoomDetails, String parentNumber, String visitLocation, String visitPurpose, String visitDate, String checkOut, String checkIn, String oraDocId, String oraStatus, Timestamp uploadTimestamp) {
        this.studentName = studentName;
        this.studentRegisterNumber = studentRegisterNumber;
        this.studentMailId = studentMailId;
        this.studentContactNumber = studentContactNumber;
        this.studentRoomDetails = studentRoomDetails;
        this.parentNumber = parentNumber;
        this.visitLocation = visitLocation;
        this.visitPurpose = visitPurpose;
        this.visitDate = visitDate;
        this.checkOut = checkOut;
        this.checkIn = checkIn;
        this.oraDocId = oraDocId;
        this.oraStatus = oraStatus;
        this.uploadTimestamp = uploadTimestamp;
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

    public String getStudentMailId() {
        return studentMailId;
    }

    public void setStudentMailId(String studentMailId) {
        this.studentMailId = studentMailId;
    }

    public String getStudentContactNumber() {
        return studentContactNumber;
    }

    public void setStudentContactNumber(String studentContactNumber) {
        this.studentContactNumber = studentContactNumber;
    }

    public String getStudentRoomDetails() {
        return studentRoomDetails;
    }

    public void setStudentRoomDetails(String studentRoomDetails) {
        this.studentRoomDetails = studentRoomDetails;
    }

    public String getParentNumber() {
        return parentNumber;
    }

    public void setParentNumber(String parentNumber) {
        this.parentNumber = parentNumber;
    }

    public String getVisitLocation() {
        return visitLocation;
    }

    public void setVisitLocation(String visitLocation) {
        this.visitLocation = visitLocation;
    }

    public String getVisitPurpose() {
        return visitPurpose;
    }

    public void setVisitPurpose(String visitPurpose) {
        this.visitPurpose = visitPurpose;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getOraDocId() {
        return oraDocId;
    }

    public void setOraDocId(String oraDocId) {
        this.oraDocId = oraDocId;
    }

    public String getOraStatus() {
        return oraStatus;
    }

    public void setOraStatus(String oraStatus) {
        this.oraStatus = oraStatus;
    }

    public Timestamp getUploadTimestamp() {
        return uploadTimestamp;
    }

    public void setUploadTimestamp(Timestamp uploadTimestamp) {
        this.uploadTimestamp = uploadTimestamp;
    }
}

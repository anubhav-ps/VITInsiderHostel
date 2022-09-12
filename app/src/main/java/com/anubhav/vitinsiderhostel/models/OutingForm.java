package com.anubhav.vitinsiderhostel.models;

import com.anubhav.vitinsiderhostel.enums.OutingFormStatus;
import com.google.firebase.Timestamp;

public class OutingForm {

    private String studentRegisterNumber;
    private String studentMailId;
    private String studentContactNumber;
    private String studentRoomDetails;
    private String studentBlock;
    private String parentNumber;

    private String visitLocation;
    private String visitPurpose;
    private String visitDate;


    private String checkOut;
    private String checkIn;

    private Timestamp timestamp;

    private String status;
    private String formId;
    private String code;


    public OutingForm() {
    }

    public OutingForm(String studentRegisterNumber, String studentMailId, String studentContactNumber, String studentRoomDetails, String studentBlock, String parentNumber, String visitLocation, String visitPurpose, String visitDate, String checkOut, String checkIn, Timestamp timestamp, String status, String formId, String code) {
        this.studentRegisterNumber = studentRegisterNumber;
        this.studentMailId = studentMailId;
        this.studentContactNumber = studentContactNumber;
        this.studentRoomDetails = studentRoomDetails;
        this.studentBlock = studentBlock;
        this.parentNumber = parentNumber;
        this.visitLocation = visitLocation;
        this.visitPurpose = visitPurpose;
        this.visitDate = visitDate;
        this.checkOut = checkOut;
        this.checkIn = checkIn;
        this.timestamp = timestamp;
        this.status = status;
        this.formId = formId;
        this.code = code;
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

    public String getStudentBlock() {
        return studentBlock;
    }

    public void setStudentBlock(String studentBlock) {
        this.studentBlock = studentBlock;
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

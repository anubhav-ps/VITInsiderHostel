package com.anubhav.vitinsiderhostel.models;

import com.google.firebase.Timestamp;

public class OutingForm {


    private String studentName;
    private String studentRegisterNumber;
    private String studentMailId;
    private String studentContactNumber;
    private String hostelBlock;
    private String hostelRoomNumber;
    private String visitLocation;
    private String visitPurpose;
    private String visitDescription;
    private Timestamp visitDate;
    private String visitDateStr;
    private Timestamp checkIn;
    private String checkInStr;
    private Timestamp checkOut;
    private String checkOutStr;
    private Timestamp timeStamp;
    private String userId;
    private String docId;
    private String chiefWarden;
    private String proctor;
    private String parent;
    private String status;
    private String code;
    private String proctorMailId;

    public OutingForm() {
    }

    public OutingForm(String studentName, String studentRegisterNumber, String studentMailId, String studentContactNumber, String hostelBlock, String hostelRoomNumber, String visitLocation, String visitPurpose, String visitDescription, Timestamp visitDate, String visitDateStr, Timestamp checkIn, String checkInStr, Timestamp checkOut, String checkOutStr, Timestamp timeStamp, String userId, String docId, String chiefWarden, String proctor, String parent, String status, String code, String proctorMailId) {
        this.studentName = studentName;
        this.studentRegisterNumber = studentRegisterNumber;
        this.studentMailId = studentMailId;
        this.studentContactNumber = studentContactNumber;
        this.hostelBlock = hostelBlock;
        this.hostelRoomNumber = hostelRoomNumber;
        this.visitLocation = visitLocation;
        this.visitPurpose = visitPurpose;
        this.visitDescription = visitDescription;
        this.visitDate = visitDate;
        this.visitDateStr = visitDateStr;
        this.checkIn = checkIn;
        this.checkInStr = checkInStr;
        this.checkOut = checkOut;
        this.checkOutStr = checkOutStr;
        this.timeStamp = timeStamp;
        this.userId = userId;
        this.docId = docId;
        this.chiefWarden = chiefWarden;
        this.proctor = proctor;
        this.parent = parent;
        this.status = status;
        this.code = code;
        this.proctorMailId = proctorMailId;
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

    public String getHostelBlock() {
        return hostelBlock;
    }

    public void setHostelBlock(String hostelBlock) {
        this.hostelBlock = hostelBlock;
    }

    public String getHostelRoomNumber() {
        return hostelRoomNumber;
    }

    public void setHostelRoomNumber(String hostelRoomNumber) {
        this.hostelRoomNumber = hostelRoomNumber;
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

    public String getVisitDescription() {
        return visitDescription;
    }

    public void setVisitDescription(String visitDescription) {
        this.visitDescription = visitDescription;
    }

    public Timestamp getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Timestamp visitDate) {
        this.visitDate = visitDate;
    }

    public Timestamp getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Timestamp checkIn) {
        this.checkIn = checkIn;
    }

    public Timestamp getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Timestamp checkOut) {
        this.checkOut = checkOut;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getChiefWarden() {
        return chiefWarden;
    }

    public void setChiefWarden(String chiefWarden) {
        this.chiefWarden = chiefWarden;
    }

    public String getProctor() {
        return proctor;
    }

    public void setProctor(String proctor) {
        this.proctor = proctor;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProctorMailId() {
        return proctorMailId;
    }

    public void setProctorMailId(String proctorMailId) {
        this.proctorMailId = proctorMailId;
    }

    public String getVisitDateStr() {
        return visitDateStr;
    }

    public void setVisitDateStr(String visitDateStr) {
        this.visitDateStr = visitDateStr;
    }

    public String getCheckInStr() {
        return checkInStr;
    }

    public void setCheckInStr(String checkInStr) {
        this.checkInStr = checkInStr;
    }

    public String getCheckOutStr() {
        return checkOutStr;
    }

    public void setCheckOutStr(String checkOutStr) {
        this.checkOutStr = checkOutStr;
    }
}

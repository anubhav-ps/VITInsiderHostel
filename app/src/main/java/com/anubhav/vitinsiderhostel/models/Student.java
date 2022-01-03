package com.anubhav.vitinsiderhostel.models;

public class Student {

    private String studentUserId;
    private String studentName;
    private String studentMailID;
    private String studentMobileNumber;
    private char studentGender;
    private String studentBranch;
    private String studentNativeLanguage;
    private String studentRoomNo;
    private boolean studentAcRoom;
    private int studentRoomBeds;



    public Student() {
    }

    public Student(String studentUserId, String studentName, String studentMailID,
                   String studentMobileNumber, char studentGender, String studentBranch,
                   String studentNativeLanguage, String studentRoomNo, boolean studentAcRoom, int studentRoomBeds) {

        this.studentUserId = studentUserId;
        this.studentName = studentName;
        this.studentMailID = studentMailID;
        this.studentMobileNumber = studentMobileNumber;
        this.studentGender = studentGender;
        this.studentBranch = studentBranch;
        this.studentNativeLanguage = studentNativeLanguage;
        this.studentRoomNo = studentRoomNo;
        this.studentAcRoom = studentAcRoom;
        this.studentRoomBeds = studentRoomBeds;

    }

    public String getStudentUserId() {
        return studentUserId;
    }

    public void setStudentUserId(String studentUserId) {
        this.studentUserId = studentUserId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentMailID() {
        return studentMailID;
    }

    public void setStudentMailID(String studentMailID) {
        this.studentMailID = studentMailID;
    }

    public String getStudentMobileNumber() {
        return studentMobileNumber;
    }

    public void setStudentMobileNumber(String studentMobileNumber) {
        this.studentMobileNumber = studentMobileNumber;
    }

    public char getStudentGender() {
        return studentGender;
    }

    public void setStudentGender(char studentGender) {
        this.studentGender = studentGender;
    }

    public String getStudentBranch() {
        return studentBranch;
    }

    public void setStudentBranch(String studentBranch) {
        this.studentBranch = studentBranch;
    }

    public String getStudentNativeLanguage() {
        return studentNativeLanguage;
    }

    public void setStudentNativeLanguage(String studentNativeLanguage) {
        this.studentNativeLanguage = studentNativeLanguage;
    }

    public String getStudentRoomNo() {
        return studentRoomNo;
    }

    public void setStudentRoomNo(String studentRoomNo) {
        this.studentRoomNo = studentRoomNo;
    }

    public boolean isStudentAcRoom() {
        return studentAcRoom;
    }

    public void setStudentAcRoom(boolean studentAcRoom) {
        this.studentAcRoom = studentAcRoom;
    }

    public int getStudentRoomBeds() {
        return studentRoomBeds;
    }

    public void setStudentRoomBeds(int studentRoomBeds) {
        this.studentRoomBeds = studentRoomBeds;
    }


}

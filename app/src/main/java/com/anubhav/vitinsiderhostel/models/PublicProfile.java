package com.anubhav.vitinsiderhostel.models;

public class PublicProfile {

    private int avatar;
    private String userName;
    private String branch;
    private String userMailID;
    private String interests;

    public PublicProfile() {
    }

    public PublicProfile(int avatar, String userName, String branch, String userMailID, String interests) {
        this.avatar = avatar;
        this.userName = userName;
        this.branch = branch;
        this.userMailID = userMailID;
        this.interests = interests;
    }


    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getUserMailID() {
        return userMailID;
    }

    public void setUserMailID(String userMailID) {
        this.userMailID = userMailID;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }
}


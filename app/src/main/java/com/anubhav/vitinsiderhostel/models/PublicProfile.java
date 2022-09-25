package com.anubhav.vitinsiderhostel.models;

public class PublicProfile {

    private int avatar;
    private String name;
    private String branch;
    private String userMailId;
    private String nativeState;
    private String bio;
    private int color;

    public PublicProfile() {
    }

    public PublicProfile(int avatar, String name, String branch, String userMailId, String nativeState, String bio, int color) {
        this.avatar = avatar;
        this.name = name;
        this.branch = branch;
        this.userMailId = userMailId;
        this.nativeState = nativeState;
        this.bio = bio;
        this.color = color;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getUserMailId() {
        return userMailId;
    }

    public void setUserMailId(String userMailId) {
        this.userMailId = userMailId;
    }

    public String getNativeState() {
        return nativeState;
    }

    public void setNativeState(String nativeState) {
        this.nativeState = nativeState;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}


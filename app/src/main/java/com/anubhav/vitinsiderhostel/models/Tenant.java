package com.anubhav.vitinsiderhostel.models;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties

public class Tenant {

    private String tenantMailID;

    private String tenantUserName;

    private String tenantContactNumber;

    private String tenantNativeLanguage;

    private String tenantBranch;

    private int tenantAvatar;

    public Tenant() {
    }

    public Tenant(String tenantMailID) {
        this.tenantMailID = tenantMailID;
    }

    public Tenant(String tenantUserName, String tenantMailID,int tenantAvatar, String tenantContactNumber, String tenantNativeLanguage, String tenantBranch) {
        this.tenantUserName = tenantUserName;
        this.tenantMailID = tenantMailID;
        this.tenantAvatar = tenantAvatar;
        this.tenantContactNumber = tenantContactNumber;
        this.tenantNativeLanguage = tenantNativeLanguage;
        this.tenantBranch = tenantBranch;
    }

    public String getTenantUserName() {
        return tenantUserName;
    }

    public void setTenantUserName(String tenantUserName) {
        this.tenantUserName = tenantUserName;
    }

    public String getTenantMailID() {
        return tenantMailID;
    }

    public void setTenantMailID(String tenantMailID) {
        this.tenantMailID = tenantMailID;
    }

    public String getTenantContactNumber() {
        return tenantContactNumber;
    }

    public void setTenantContactNumber(String tenantContactNumber) {
        this.tenantContactNumber = tenantContactNumber;
    }

    public String getTenantNativeLanguage() {
        return tenantNativeLanguage;
    }

    public void setTenantNativeLanguage(String tenantNativeLanguage) {
        this.tenantNativeLanguage = tenantNativeLanguage;
    }

    public String getTenantBranch() {
        return tenantBranch;
    }

    public void setTenantBranch(String tenantBranch) {
        this.tenantBranch = tenantBranch;
    }

    public int getTenantAvatar() {
        return tenantAvatar;
    }

    public void setTenantAvatar(int tenantAvatar) {
        this.tenantAvatar = tenantAvatar;
    }
}

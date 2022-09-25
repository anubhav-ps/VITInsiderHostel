package com.anubhav.vitinsiderhostel.models;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties

public class Tenant {

    private String tenantMailID;

    private String tenantName;

    private String tenantContactNumber;

    private String tenantNativeState;

    private String tenantBranch;

    private String tenantMess;

    private int tenantAvatar;

    public Tenant() {
    }


    public Tenant(String tenantMailID) {
        this.tenantMailID = tenantMailID;
    }

    public Tenant(String tenantMailID, String tenantName, String tenantContactNumber, String tenantNativeState, String tenantBranch, int tenantAvatar, String tenantMess) {
        this.tenantMailID = tenantMailID;
        this.tenantName = tenantName;
        this.tenantContactNumber = tenantContactNumber;
        this.tenantNativeState = tenantNativeState;
        this.tenantBranch = tenantBranch;
        this.tenantAvatar = tenantAvatar;
        this.tenantMess = tenantMess;
    }

    public String getTenantMailID() {
        return tenantMailID;
    }

    public void setTenantMailID(String tenantMailID) {
        this.tenantMailID = tenantMailID;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantContactNumber() {
        return tenantContactNumber;
    }

    public void setTenantContactNumber(String tenantContactNumber) {
        this.tenantContactNumber = tenantContactNumber;
    }

    public String getTenantNativeState() {
        return tenantNativeState;
    }

    public void setTenantNativeState(String tenantNativeState) {
        this.tenantNativeState = tenantNativeState;
    }

    public String getTenantBranch() {
        return tenantBranch;
    }

    public void setTenantBranch(String tenantBranch) {
        this.tenantBranch = tenantBranch;
    }

    public String getTenantMess() {
        return tenantMess;
    }

    public void setTenantMess(String tenantMess) {
        this.tenantMess = tenantMess;
    }

    public int getTenantAvatar() {
        return tenantAvatar;
    }

    public void setTenantAvatar(int tenantAvatar) {
        this.tenantAvatar = tenantAvatar;
    }
}

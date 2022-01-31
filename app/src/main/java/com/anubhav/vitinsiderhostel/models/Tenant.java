package com.anubhav.vitinsiderhostel.models;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
@Entity(tableName = "TenantData")
public class Tenant {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "tenant_mail_id")
    private String tenantMailID;
    @ColumnInfo(name = "tenant_user_name")
    private String tenantUserName;
    @ColumnInfo(name = "tenant_contact_num")
    private String tenantContactNumber;
    @ColumnInfo(name = "tenant_native_language")
    private String tenantNativeLanguage;
    @ColumnInfo(name = "tenant_branch")
    private String tenantBranch;

    public Tenant() {
    }

    public Tenant(String tenantUserName, String tenantMailID, String tenantContactNumber, String tenantNativeLanguage, String tenantBranch) {
        this.tenantUserName = tenantUserName;
        this.tenantMailID = tenantMailID;
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

}

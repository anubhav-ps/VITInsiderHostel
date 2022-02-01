package com.anubhav.vitinsiderhostel.modeldao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.anubhav.vitinsiderhostel.models.Tenant;

import java.util.List;

@Dao
public interface TenantModelDao {

    @Insert
    void insertTenantInstance(Tenant tenantData);

    @Update
    void updateTenantInstance(Tenant tenant);

    @Query("Delete FROM  TenantData")
    void deleteAllTenantInstance();

    @Delete
    void deleteTenantInstance(Tenant tenantData);

    @Query("SELECT  * FROM TenantData WHERE tenant_mail_id = :tenantMailId")
    LiveData<Tenant> peekTenantInstance(String tenantMailId);

    @Query("SELECT * FROM TENANTDATA ")
    LiveData<List<Tenant>> retrieveAllTenantInstances();

}

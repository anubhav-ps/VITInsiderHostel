package com.anubhav.vitinsiderhostel.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.anubhav.vitinsiderhostel.modeldao.TenantModelDao;
import com.anubhav.vitinsiderhostel.modeldao.UserModelDao;
import com.anubhav.vitinsiderhostel.models.Tenant;
import com.anubhav.vitinsiderhostel.models.User;

import java.util.List;

public class LocalRepository {


    // dao
    private final UserModelDao userModelDao;
    private final TenantModelDao tenantModelDao;

    private List<User> allUsers;
    private LiveData<List<Tenant>> allTenants;

    // constructor
    public LocalRepository(Application application) {

        LocalRoomDatabase localRoomDatabase = LocalRoomDatabase.getDatabase(application);

        userModelDao = localRoomDatabase.userModelDao();
        tenantModelDao = localRoomDatabase.tenantModelDao();
        allTenants = tenantModelDao.retrieveAllTenantInstances();

    }


    public void insertCurrentUser(User user) {
        LocalRoomDatabase.databaseExecutor.execute(() -> userModelDao.insertUserInstance(user));
        // new InsertInBg(userModelDao).execute(user);
    }

    public List<User> retrieveAllUsers() {
        return userModelDao.retrieveAllUserInstance();
    }

    public void deleteAllUsers() {
        LocalRoomDatabase.databaseExecutor.execute(userModelDao::deleteWholeUserInstance);
    }

    public void insertTenant(Tenant tenant) {
        LocalRoomDatabase.databaseExecutor.execute(() -> tenantModelDao.insertTenantInstance(tenant));
    }

    public LiveData<List<Tenant>> retrieveAllTenants() {
        return allTenants;
    }

    public void deleteAllTenants() {
        LocalRoomDatabase.databaseExecutor.execute(tenantModelDao::deleteAllTenantInstance);
    }

}

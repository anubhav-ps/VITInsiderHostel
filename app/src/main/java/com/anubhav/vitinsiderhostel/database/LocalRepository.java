package com.anubhav.vitinsiderhostel.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.anubhav.vitinsiderhostel.modeldao.TenantModelDao;
import com.anubhav.vitinsiderhostel.modeldao.UserModelDao;
import com.anubhav.vitinsiderhostel.models.User;

import java.util.List;

public class LocalRepository {


    // dao
    private final UserModelDao userModelDao;
    private final TenantModelDao tenantModelDao;

    private final LiveData<List<User>> allUsers;

    // constructor
    public LocalRepository(Application application) {

        LocalRoomDatabase localRoomDatabase = LocalRoomDatabase.getDatabase(application);

        userModelDao = localRoomDatabase.userModelDao();
        tenantModelDao = localRoomDatabase.tenantModelDao();
        allUsers = userModelDao.retrieveAllUserInstance();
    }


    public void insertCurrentUser(User user) {
        LocalRoomDatabase.databaseExecutor.execute(() -> userModelDao.insertUserInstance(user));
        // new InsertInBg(userModelDao).execute(user);
    }

    public LiveData<List<User>> retrieveAllUsers() {
        return allUsers;
    }

    public void deleteAllUsers(){
        LocalRoomDatabase.databaseExecutor.execute(userModelDao::deleteWholeUserInstance);
    }

}

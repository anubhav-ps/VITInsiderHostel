package com.anubhav.vitinsiderhostel.modeldao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.anubhav.vitinsiderhostel.models.User;

import java.util.List;

@Dao
public interface UserModelDao {

    @Insert
    void insertUserInstance(User user);

    @Update
    void updateUserInstance(User user);

    @Query("Delete FROM  AppUserData")
    void deleteWholeUserInstance();

    @Delete
    void deleteUserInstance(User user);

    @Query("SELECT  * FROM AppUserData WHERE  app_user_id = :userId")
    User peekUserInstance(String userId);

    @Query("SELECT * FROM AppUserData ")
    LiveData<List<User>> retrieveAllUserInstance();

}

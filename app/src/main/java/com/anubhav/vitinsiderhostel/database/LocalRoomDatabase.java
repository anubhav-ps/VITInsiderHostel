package com.anubhav.vitinsiderhostel.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.anubhav.vitinsiderhostel.modeldao.TenantModelDao;
import com.anubhav.vitinsiderhostel.modeldao.UserModelDao;
import com.anubhav.vitinsiderhostel.models.Tenant;
import com.anubhav.vitinsiderhostel.models.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {User.class, Tenant.class}, version = 1, exportSchema = false)
public abstract class LocalRoomDatabase extends RoomDatabase {


    //singleton database instance
    private static volatile LocalRoomDatabase INSTANCE;

    private static final int THREAD_POOL = 3;

    public static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(THREAD_POOL);

    public static LocalRoomDatabase getDatabase(Context context) {

        if (INSTANCE == null) {
            synchronized (LocalRoomDatabase.class) {
                INSTANCE = Room.databaseBuilder(context, LocalRoomDatabase.class, "INSIDER_HOSTEL_DB").fallbackToDestructiveMigration().build();
            }
        }
        return INSTANCE;
    }

    // getters of the dao interface
    public abstract UserModelDao userModelDao();

    public abstract TenantModelDao tenantModelDao();



}

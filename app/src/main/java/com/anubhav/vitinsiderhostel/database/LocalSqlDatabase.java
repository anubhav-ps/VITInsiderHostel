package com.anubhav.vitinsiderhostel.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.anubhav.vitinsiderhostel.enums.NotifyStatus;
import com.anubhav.vitinsiderhostel.interfaces.iOnNotifyDbProcess;
import com.anubhav.vitinsiderhostel.models.Tenant;
import com.anubhav.vitinsiderhostel.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LocalSqlDatabase extends SQLiteOpenHelper {

    private static final String TENANT_TABLE = "TENANT_TABLE";
    private static final String TENANT_MAIL_ID = "MAIL_ID";
    private static final String TENANT_AVATAR = "AVATAR";
    private static final String TENANT_NAME = "NAME";
    private static final String TENANT_CONTACT_NUMBER = "CONTACT_NUMBER";
    private static final String TENANT_BRANCH = "BRANCH";
    private static final String TENANT_NATIVE_LANGUAGE = "NATIVE_LANGUAGE";


    private static final String USER_TABLE = "USER_TABLE";
    private static final String USER_ID = "USER_ID";

    private static final String USER_MAIL_ID = "MAIL_ID";
    private static final String STUDENT_REGISTER_NUMBER = "REGISTER_NUMBER";
    private static final String USER_NAME = "NAME";
    private static final String USER_CONTACT_NUMBER = "CONTACT_NUMBER";
    private static final String USER_TYPE = "USER_TYPE";
    private static final String STUDENT_BLOCK = "BLOCK";
    private static final String STUDENT_BRANCH = "BRANCH";
    private static final String STUDENT_NATIVE_LANGUAGE = "NATIVE_LANGUAGE";
    private static final String ROOM_NO = "ROOM_NO";
    private static final String ROOM_TYPE = "ROOM_TYPE";
    private static final String IS_ADMIN = "IS_ADMIN";
    private static final String USER_AVATAR = "AVATAR";


    private static ExecutorService executors;
    private static List<Tenant> tenantList;
    private static List<Tenant> updateTenantList;
    private final int N = 2;
    iOnNotifyDbProcess notify;
    private int totalTenants;

    //constructors
    public LocalSqlDatabase(@Nullable Context context) {
        super(context, "INSIDER_HOSTEL_DB", null, 1);
        assert context != null;
    }

    public LocalSqlDatabase(@Nullable Context context, iOnNotifyDbProcess notify) {
        super(context, "INSIDER_HOSTEL_DB", null, 1);

        this.notify = notify;

        tenantList = new ArrayList<>();
        updateTenantList = new ArrayList<>();

        executors = Executors.newFixedThreadPool(N);
    }

    //executor methods
    public static ExecutorService getExecutors() {
        return executors;
    }

    public static void stopExecutors() {
        executors.shutdown();
    }

    // on create methods
    @Override
    public void onCreate(SQLiteDatabase db) {
        createUserTable(db);
        createTenantTable(db);
    }

    private void createUserTable(SQLiteDatabase db) {
        final String createTableStatement
                = "CREATE TABLE " + USER_TABLE +
                " (" +
                USER_ID + " TEXT PRIMARY KEY, " +
                USER_MAIL_ID + " TEXT , " +
                STUDENT_REGISTER_NUMBER + " TEXT , " +
                USER_AVATAR + " INT, " +
                USER_NAME + " TEXT , " +
                USER_CONTACT_NUMBER + " TEXT ," +
                USER_TYPE + " TEXT ," +
                STUDENT_BLOCK + " TEXT ," +
                STUDENT_BRANCH + " TEXT ," +
                STUDENT_NATIVE_LANGUAGE + " TEXT ," +
                ROOM_NO + " TEXT ," +
                ROOM_TYPE + " TEXT ," +
                IS_ADMIN + " BOOL )";

        db.execSQL(createTableStatement);

    }

    private void createTenantTable(SQLiteDatabase db) {
        final String createTableStatement
                = "CREATE TABLE " + TENANT_TABLE +
                " ( " +
                TENANT_MAIL_ID + " TEXT PRIMARY KEY, " +
                TENANT_AVATAR + " INT , " +
                TENANT_NAME + " TEXT , " +
                TENANT_CONTACT_NUMBER + " TEXT ," +
                TENANT_BRANCH + " TEXT , " +
                TENANT_NATIVE_LANGUAGE +
                " TEXT )";

        db.execSQL(createTableStatement);
    }

    //upgrade table method
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TENANT_TABLE);
        onCreate(db);
    }

    //update users

    //user methods
    //insert users
    public boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(USER_ID, user.getUser_Id());
        cv.put(USER_MAIL_ID, user.getUserMailID());
        cv.put(STUDENT_REGISTER_NUMBER, user.getStudentRegisterNumber());
        cv.put(USER_AVATAR, user.getAvatar());
        cv.put(USER_CONTACT_NUMBER, user.getUserContactNumber());
        cv.put(USER_NAME, user.getUserName());
        cv.put(USER_TYPE, user.getUserType());
        cv.put(STUDENT_BLOCK, user.getStudentBlock());
        cv.put(STUDENT_BRANCH, user.getStudentBranch());
        cv.put(STUDENT_NATIVE_LANGUAGE, user.getStudentNativeLanguage());
        cv.put(ROOM_NO, user.getRoomNo());
        cv.put(ROOM_TYPE, user.getRoomType());
        cv.put(IS_ADMIN, user.getAdmin());

        long result = db.insert(USER_TABLE, null, cv);
        db.close();
        return result != -1;
    }

    //read users
    public User getCurrentUser() {

        User user = User.getInstance();
        final String query = "SELECT * FROM " + USER_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            user.setUser_Id(cursor.getString(0));
            user.setUserMailID(cursor.getString(1));
            user.setStudentRegisterNumber(cursor.getString(2));
            user.setAvatar(cursor.getInt(3));
            user.setUserName(cursor.getString(4));
            user.setUserContactNumber(cursor.getString(5));
            user.setUserType(cursor.getString(6));
            user.setStudentBlock(cursor.getString(7));
            user.setStudentBranch(cursor.getString(8));
            user.setStudentNativeLanguage(cursor.getString(9));
            user.setRoomNo(cursor.getString(10));
            user.setRoomType(cursor.getString(11));
            user.setAdmin(cursor.getInt(12) == 1);
        }

        cursor.close();
        db.close();
        return user;

    }


    //tenant methods
    //insert tenants

    public void updateUserInBackground(User user) {
        Future<String> result = executors.submit(runParallelUserUpdate(user), "Done");
        executors.submit(hasTaskCompleted(result, NotifyStatus.USER_UPDATED));
    }

    private Runnable runParallelUserUpdate(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        return () -> {
            ContentValues cv = new ContentValues();

            cv.put(USER_NAME, user.getUserName());
            cv.put(USER_AVATAR, user.getAvatar());
            cv.put(USER_CONTACT_NUMBER, user.getUserContactNumber());
            cv.put(STUDENT_NATIVE_LANGUAGE, user.getStudentNativeLanguage());

            db.update(USER_TABLE, cv, "USER_ID = ?", new String[]{user.getUser_Id()});
            db.close();
        };
    }

    //read tenants
    public List<Tenant> getTenants() {

        List<Tenant> tenants = new ArrayList<>();

        final String query = "SELECT * FROM " + TENANT_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                tenants.add(
                        new Tenant(
                                cursor.getString(2),
                                cursor.getString(0),
                                cursor.getInt(1),
                                cursor.getString(3),
                                cursor.getString(5),
                                cursor.getString(4)
                        ));

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tenants;
    }

    public void addTenantInBackground(Tenant tenant) {
        tenantList.add(tenant);
        if (tenantList.size() == totalTenants) {
            if (deleteAllTenants()) {
                Future<String> result = executors.submit(runParallelTenantAddition(), "Done");
                executors.submit(hasTaskCompleted(result, NotifyStatus.ROOM_MATE_COMPLETE_DATA_DOWNLOADED));
            }
        }
    }

    private Runnable runParallelTenantAddition() {

        SQLiteDatabase db = this.getWritableDatabase();
        return () -> {
            for (Tenant tenant : tenantList) {
                ContentValues cv = new ContentValues();
                cv.put(TENANT_MAIL_ID, tenant.getTenantMailID());
                cv.put(TENANT_AVATAR, tenant.getTenantAvatar());
                cv.put(TENANT_NAME, tenant.getTenantUserName());
                cv.put(TENANT_CONTACT_NUMBER, tenant.getTenantContactNumber());
                cv.put(TENANT_BRANCH, tenant.getTenantBranch());
                cv.put(TENANT_NATIVE_LANGUAGE, tenant.getTenantNativeLanguage());
                db.insert(TENANT_TABLE, null, cv);
            }
            db.close();
        };
    }

    public void updateTenantInBackground(Tenant tenant) {
        updateTenantList.add(tenant);
        if (updateTenantList.size() == totalTenants) {
            Future<String> result = executors.submit(runParallelTenantUpdation(), "Done");
            executors.submit(hasTaskCompleted(result, NotifyStatus.ROOM_MATE_COMPLETE_DATA_DOWNLOADED));
        }
    }

    private Runnable runParallelTenantUpdation() {
        SQLiteDatabase db = this.getWritableDatabase();
        return () -> {
            for (Tenant tenant : updateTenantList) {
                ContentValues cv = new ContentValues();
                cv.put(TENANT_AVATAR, tenant.getTenantAvatar());
                cv.put(TENANT_NAME, tenant.getTenantUserName());
                cv.put(TENANT_CONTACT_NUMBER, tenant.getTenantContactNumber());
                cv.put(TENANT_NATIVE_LANGUAGE, tenant.getTenantNativeLanguage());
                db.update(TENANT_TABLE, cv, "MAIL_ID = ?", new String[]{tenant.getTenantMailID()});
            }
            db.close();
        };
    }

    public boolean isTenantPresent(Tenant tenant) {
        Cursor cursor = this.getReadableDatabase().query(
                TENANT_TABLE,
                null,
                TENANT_MAIL_ID + " = '" + tenant.getTenantMailID() + "'",
                null, null, null, null);
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    //delete tables
    public boolean deleteCurrentUser() {
        final String query = "delete from " + USER_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }

    public boolean deleteAllTenants() {
        final String query = "delete from " + TENANT_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return false;
        }

        cursor.close();
        db.close();
        return true;
    }

    private Runnable hasTaskCompleted(Future<String> result, NotifyStatus notifyStatus) {
        return () -> {
            while (!result.isDone()) {

            }
            if (notifyStatus == NotifyStatus.ROOM_MATE_COMPLETE_DATA_DOWNLOADED) {
                tenantList.clear();
                updateTenantList.clear();
                notify.notifyCompleteDataDownload();
            } else if (notifyStatus == NotifyStatus.USER_UPDATED) {
                notify.notifyUserUpdated();
            }
        };
    }

    //field setters
    public void setTotalTenants(int totalTenants) {
        this.totalTenants = totalTenants;
    }
}
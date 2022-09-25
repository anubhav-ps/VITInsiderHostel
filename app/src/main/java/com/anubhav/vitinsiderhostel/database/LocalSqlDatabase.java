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

    private static final String TENANT_MAIL_ID = "TENANT_MAIL_ID";
    private static final String TENANT_NAME = "TENANT_NAME";
    private static final String TENANT_CONTACT_NUMBER = "TENANT_CONTACT_NUMBER";
    private static final String TENANT_NATIVE_STATE = "TENANT_NATIVE_STATE";
    private static final String TENANT_BRANCH = "TENANT_BRANCH";
    private static final String TENANT_MESS = "TENANT_MESS";
    private static final String TENANT_AVATAR = "TENANT_AVATAR";


    private static final String USER_TABLE = "USER_TABLE";

    private static final String USER_UID = "USER_UID";
    private static final String USER_MAIL_ID = "MAIL_ID";
    private static final String STUDENT_REGISTER_NUMBER = "STUDENT_REGISTER_NUMBER";
    private static final String STUDENT_NAME = "STUDENT_NAME";
    private static final String USER_NAME = "USER_NAME";
    private static final String STUDENT_BRANCH = "STUDENT_BRANCH";
    private static final String USER_CONTACT_NUMBER = "USER_CONTACT_NUMBER";
    private static final String STUDENT_NATIVE_STATE = "STUDENT_NATIVE_STATE";
    private static final String NATIVE_STATE_CHANGES = "NATIVE_STATE_CHANGES";
    private static final String USER_TYPE = "USER_TYPE";
    private static final String AVATAR = "AVATAR";
    private static final String PARENT_MAIL_ID = "PARENT_MAIL_ID";
    private static final String STUDENT_BLOCK = "STUDENT_BLOCK";
    private static final String ROOM_NO = "ROOM_NO";
    private static final String BEDS = "BEDS";
    private static final String AC = "AC";
    private static final String MESS = "MESS";
    private static final String HAS_PUBLIC_PROFILE = "HAS_PUBLIC_PROFILE";
    private static final String PUBLIC_BIO = "PUBLIC_BIO";
    private static final String PUBLIC_COLOR = "PUBLIC_COLOR";
    private static final String PRIVATE_PROFILE_ID = "PRIVATE_PROFILE_ID";
    private static final String FCM_TOKEN = "FCM_TOKEN";

    private static final int DB_Version = 10;
    private static ExecutorService executors;
    private static List<Tenant> tenantList;
    private static List<Tenant> updateTenantList;
    private final int N = 2;
    iOnNotifyDbProcess notify;
    private int totalTenants;

    //constructors
    public LocalSqlDatabase(@Nullable Context context) {
        super(context, "INSIDER_HOSTEL_DB", null, DB_Version);
        assert context != null;
    }

    public LocalSqlDatabase(@Nullable Context context, iOnNotifyDbProcess notify) {
        super(context, "INSIDER_HOSTEL_DB", null, DB_Version);

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
                USER_UID + " TEXT PRIMARY KEY , " +
                USER_MAIL_ID + " TEXT , " +
                STUDENT_REGISTER_NUMBER + " TEXT , " +
                STUDENT_NAME + " TEXT , " +
                USER_NAME + " TEXT , " +
                STUDENT_BRANCH + " TEXT , " +
                USER_CONTACT_NUMBER + " TEXT , " +
                STUDENT_NATIVE_STATE + " TEXT , " +
                NATIVE_STATE_CHANGES + " INT , " +
                USER_TYPE + " TEXT , " +
                AVATAR + " INT , " +
                PARENT_MAIL_ID + " TEXT , " +
                STUDENT_BLOCK + " TEXT , " +
                ROOM_NO + " TEXT , " +
                BEDS + " INT , " +
                AC + " BOOL , " +
                MESS + " TEXT , " +
                HAS_PUBLIC_PROFILE + " BOOL , " +
                PUBLIC_BIO + " TEXT , " +
                PUBLIC_COLOR + " INT , " +
                PRIVATE_PROFILE_ID + " TEXT , " +
                FCM_TOKEN + " TEXT )";

        db.execSQL(createTableStatement);

    }

    private void createTenantTable(SQLiteDatabase db) {
        final String createTableStatement
                = "CREATE TABLE " + TENANT_TABLE +
                " ( " +
                TENANT_MAIL_ID + " TEXT PRIMARY KEY, " +
                TENANT_NAME + " TEXT , " +
                TENANT_CONTACT_NUMBER + " TEXT , " +
                TENANT_NATIVE_STATE + " TEXT , " +
                TENANT_BRANCH + " TEXT , " +
                TENANT_AVATAR + " INT , " +
                TENANT_MESS + " TEXT )";

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

        cv.put(USER_UID, user.getUser_UID());
        cv.put(USER_MAIL_ID, user.getUserMailId());
        cv.put(STUDENT_REGISTER_NUMBER, user.getStudentRegisterNumber());
        cv.put(STUDENT_NAME, user.getStudentName());
        cv.put(USER_NAME, user.getUserName());
        cv.put(STUDENT_BRANCH, user.getStudentBranch());
        cv.put(USER_CONTACT_NUMBER, user.getUserContactNumber());
        cv.put(STUDENT_NATIVE_STATE, user.getStudentNativeState());
        cv.put(NATIVE_STATE_CHANGES, user.getNativeStateChanges());
        cv.put(USER_TYPE, user.getUserType());
        cv.put(AVATAR, user.getAvatar());
        cv.put(PARENT_MAIL_ID, user.getParentMailId());
        cv.put(STUDENT_BLOCK, user.getStudentBlock());
        cv.put(ROOM_NO, user.getRoomNo());
        cv.put(BEDS, user.getBeds());
        cv.put(AC, user.getAc());
        cv.put(MESS, user.getMess());
        cv.put(HAS_PUBLIC_PROFILE, user.getHasPublicProfile());
        cv.put(PUBLIC_BIO, user.getPublicBio());
        cv.put(PUBLIC_COLOR, user.getPublicColor());
        cv.put(PRIVATE_PROFILE_ID, user.getPrivateProfileId());
        cv.put(FCM_TOKEN, user.getFcmToken());

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
            user.setUser_UID(cursor.getString(0));
            user.setUserMailId(cursor.getString(1));
            user.setStudentRegisterNumber(cursor.getString(2));
            user.setStudentName(cursor.getString(3));
            user.setUserName(cursor.getString(4));
            user.setStudentBranch(cursor.getString(5));
            user.setUserContactNumber(cursor.getString(6));
            user.setStudentNativeState(cursor.getString(7));
            user.setNativeStateChanges(cursor.getInt(8));
            user.setUserType(cursor.getString(9));
            user.setAvatar(cursor.getInt(10));
            user.setParentMailId(cursor.getString(11));
            user.setStudentBlock(cursor.getString(12));
            user.setRoomNo(cursor.getString(13));
            user.setBeds(cursor.getInt(14));
            user.setAc(cursor.getInt(15) == 1);
            user.setMess(cursor.getString(16));
            user.setHasPublicProfile(cursor.getInt(17) == 1);
            user.setPublicBio(cursor.getString(18));
            user.setPublicColor(cursor.getInt(19));
            user.setPrivateProfileId(cursor.getString(20));
            user.setFcmToken(cursor.getString(21));
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
            cv.put(AVATAR, user.getAvatar());
            cv.put(USER_CONTACT_NUMBER, user.getUserContactNumber());
            cv.put(STUDENT_NATIVE_STATE, user.getStudentNativeState());
            cv.put(NATIVE_STATE_CHANGES, user.getNativeStateChanges());
            cv.put(HAS_PUBLIC_PROFILE, user.getHasPublicProfile());
            cv.put(PRIVATE_PROFILE_ID, user.getPrivateProfileId());
            cv.put(PRIVATE_PROFILE_ID, user.getPublicBio());
            cv.put(PUBLIC_COLOR, user.getPublicColor());
            cv.put(FCM_TOKEN,user.getFcmToken());

            db.update(USER_TABLE, cv, "USER_UID = ?", new String[]{user.getUser_UID()});
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
                tenants.add(new Tenant(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getInt(5),
                        cursor.getString(6)));

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
                cv.put(TENANT_NAME, tenant.getTenantName());
                cv.put(TENANT_CONTACT_NUMBER, tenant.getTenantContactNumber());
                cv.put(TENANT_NATIVE_STATE, tenant.getTenantNativeState());
                cv.put(TENANT_BRANCH, tenant.getTenantBranch());
                cv.put(TENANT_AVATAR, tenant.getTenantAvatar());
                cv.put(TENANT_MESS, tenant.getTenantMess());

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
                cv.put(TENANT_CONTACT_NUMBER, tenant.getTenantContactNumber());
                cv.put(TENANT_NATIVE_STATE, tenant.getTenantNativeState());
                cv.put(TENANT_MESS, tenant.getTenantMess());
                db.update(TENANT_TABLE, cv, "TENANT_MAIL_ID = ?", new String[]{tenant.getTenantMailID()});
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
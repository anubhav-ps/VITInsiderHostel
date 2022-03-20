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
    private static final String TENANT_NAME = "NAME";
    private static final String TENANT_CONTACT_NUMBER = "CONTACT_NUMBER";
    private static final String TENANT_BRANCH = "BRANCH";
    private static final String TENANT_NATIVE_LANGUAGE = "NATIVE_LANGUAGE";


    private static final String USER_TABLE = "USER_TABLE";
    private static final String USER_ID = "USER_ID";
    private static final String DOC_ID = "DOC_ID";
    private static final String USER_MAIL_ID = "MAIL_ID";
    private static final String USER_NAME = "NAME";
    private static final String USER_CONTACT_NUMBER = "CONTACT_NUMBER";
    private static final String USER_TYPE = "USER_TYPE";
    private static final String STUDENT_BLOCK = "BLOCK";
    private static final String STUDENT_BRANCH = "BRANCH";
    private static final String STUDENT_NATIVE_LANGUAGE = "NATIVE_LANGUAGE";
    private static final String ROOM_NO = "ROOM_NO";
    private static final String ROOM_TYPE = "ROOM_TYPE";
    private static final String IS_ADMIN = "IS_ADMIN";
    public static int tenantCollectionSize;
    public static ExecutorService executors;
    private static int totalTenants;
    private static List<Tenant> tenantList;
    private static List<Tenant> updatedTenantList;
    private final int N = 2;

    iOnNotifyDbProcess notify;

    public LocalSqlDatabase(@Nullable Context context) {
        super(context, "INSIDER_HOSTEL_SQL_DB", null, 1);
    }

    public LocalSqlDatabase(@Nullable Context context, int tenants, iOnNotifyDbProcess notify) {
        super(context, "INSIDER_HOSTEL_SQL_DB", null, 1);

        this.notify = notify;

        totalTenants = tenants;

        tenantList = new ArrayList<>();
        updatedTenantList = new ArrayList<>();

        executors = Executors.newFixedThreadPool(N);
    }

    public static void stopExecutors() {
        executors.shutdown();
    }

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
                DOC_ID + " TEXT , " +
                USER_MAIL_ID + " TEXT , " +
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
                TENANT_NAME + " TEXT , " +
                TENANT_CONTACT_NUMBER + " TEXT ," +
                TENANT_BRANCH + " TEXT , " +
                TENANT_NATIVE_LANGUAGE +
                " TEXT )";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addTenant(Tenant tenant) {
        tenantList.add(tenant);
        if (tenantList.size() == totalTenants) {
            Future<String> result = executors.submit(runParallelTenantInsertion(), "Done");
            executors.submit(hasTaskCompleted(result, NotifyStatus.ROOM_MATE_LIST_DOWNLOADED));
        }
    }

    private Runnable runParallelTenantInsertion() {
        SQLiteDatabase db = this.getWritableDatabase();
        return () -> {
            for (Tenant tenant : tenantList) {
                ContentValues cv = new ContentValues();
                cv.put(TENANT_MAIL_ID, tenant.getTenantMailID());
                cv.put(TENANT_NAME, tenant.getTenantUserName());
                cv.put(TENANT_CONTACT_NUMBER, tenant.getTenantContactNumber());
                cv.put(TENANT_BRANCH, tenant.getTenantBranch());
                cv.put(TENANT_NATIVE_LANGUAGE, tenant.getTenantNativeLanguage());
                db.insert(TENANT_TABLE, null, cv);
            }
        };
    }

    public void updateTenant(Tenant tenant) {
        updatedTenantList.add(tenant);
        if (updatedTenantList.size() == tenantCollectionSize) {
            Future<String> result = executors.submit(runParallelTenantUpdates(), "Done");
            executors.submit(hasTaskCompleted(result, NotifyStatus.ROOM_MATE_COMPLETE_DATA_DOWNLOADED));
        }
    }

    private Runnable runParallelTenantUpdates() {
        SQLiteDatabase db = this.getWritableDatabase();
        return () -> {
            for (Tenant tenant : updatedTenantList) {
                ContentValues cv = new ContentValues();
                cv.put(TENANT_MAIL_ID, tenant.getTenantMailID());
                cv.put(TENANT_NAME, tenant.getTenantUserName());
                cv.put(TENANT_CONTACT_NUMBER, tenant.getTenantContactNumber());
                cv.put(TENANT_BRANCH, tenant.getTenantBranch());
                cv.put(TENANT_NATIVE_LANGUAGE, tenant.getTenantNativeLanguage());
                db.update(TENANT_TABLE, cv, "MAIL_ID = ?", new String[]{tenant.getTenantMailID()});
            }
            db.close();
        };
    }

    private Runnable hasTaskCompleted(Future<String> result, NotifyStatus notifyStatus) {
        return () -> {
            while (!result.isDone()) {

            }
            tenantList.clear();
            updatedTenantList.clear();
            if (notifyStatus == NotifyStatus.ROOM_MATE_LIST_DOWNLOADED) {
                notify.onNotifyCompleteListDownload(notifyStatus);
            } else if (notifyStatus == NotifyStatus.ROOM_MATE_COMPLETE_DATA_DOWNLOADED) {
                notify.onNotifyCompleteDataDownload(notifyStatus);
            }
        };
    }

    public List<Tenant> getTenants() {

        List<Tenant> tenants = new ArrayList<>();

        final String query = "SELECT * FROM " + TENANT_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                tenants.add(
                        new Tenant(
                                cursor.getString(1),
                                cursor.getString(0),
                                cursor.getString(2),
                                cursor.getString(4),
                                cursor.getString(3)
                        ));

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tenants;
    }

    public boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(USER_ID, user.getUser_Id());
        cv.put(DOC_ID, user.getDoc_Id());
        cv.put(USER_MAIL_ID, user.getUserMailID());
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
        return result != -1;
    }

    public boolean updateUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(USER_NAME, user.getUserName());
        cv.put(USER_CONTACT_NUMBER, user.getUserContactNumber());
        cv.put(STUDENT_NATIVE_LANGUAGE, user.getStudentNativeLanguage());
        cv.put(STUDENT_BRANCH, user.getStudentBranch());

        db.update(USER_TABLE, cv, "USER_ID = ?", new String[]{user.getUser_Id()});
        db.close();

        return true;
    }

    public User getCurrentUser() {

        User user = User.getInstance();
        final String query = "SELECT * FROM " + USER_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            user.setUser_Id(cursor.getString(0));
            user.setDoc_Id(cursor.getString(1));
            user.setUserMailID(cursor.getString(2));
            user.setUserName(cursor.getString(3));
            user.setUserContactNumber(cursor.getString(4));
            user.setUserType(cursor.getString(5));
            user.setStudentBlock(cursor.getString(6));
            user.setStudentBranch(cursor.getString(7));
            user.setStudentNativeLanguage(cursor.getString(8));
            user.setRoomNo(cursor.getString(9));
            user.setRoomType(cursor.getString(10));
            user.setAdmin(cursor.getInt(11) == 1);
        }

        cursor.close();
        db.close();
        return user;

    }

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
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }


}
package com.hp.advantage.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hp.advantage.data.User;

import java.util.ArrayList;

public class UserDBHelper {
    // User
    static final String userTable = "user";
    static final String user_id = "user_id";
    static final String first_name = "first_name";
    static final String last_name = "last_name";
    static final String email = "email";
    static final String gender = "gender";
    static final String create_date = "create_date";
    static final String last_login = "last_login";
    static final String phone_unique_id = "phone_unique_id";
    static final String facebook_id = "facebook_id";
    static final String facebook_user_name = "facebook_user_name";
    static final String birthday = "birthday";
    static final String zipcode = "zipcode";
    static final String phone = "phone";
    static final String city_id = "city_id";
    static final String state_id = "state_id";
    static final String country_id = "country_id";
    static final String user_guid = "user_guid";
    static final String facebook_expires_in = "facebook_expires_in";
    static final String facebook_access_token = "facebook_access_token";
    static final String gcm_registration_id = "gcm_registration_id";
    static final String gcm_registration_date = "gcm_registration_date";
    static final String plus_account_name = "plus_account_name";
    static final String can_place_bids = "can_place_bids";

    private DatabaseHelper dbHelper;

    public UserDBHelper(DatabaseHelper databaseHelper) {
        dbHelper = databaseHelper;
    }

    public void CreateTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + userTable + " (" + user_id + " INTEGER NOT NULL, "
                + first_name + " TEXT,"
                + last_name + " TEXT,"
                + email + " TEXT,"
                + gender + " INTEGER,"
                + create_date + " TEXT,"
                + last_login + " TEXT,"
                + phone_unique_id + " TEXT,"
                + facebook_id + " TEXT,"
                + facebook_user_name + " TEXT,"
                + birthday + " TEXT,"
                + zipcode + " TEXT,"
                + phone + " TEXT,"
                + city_id + " TEXT, "
                + state_id + " TEXT,"
                + country_id + " TEXT,"
                + user_guid + " TEXT,"
                + facebook_expires_in + " TEXT,"
                + facebook_access_token + " TEXT,"
                + gcm_registration_id + " TEXT, "
                + gcm_registration_date + " TEXT, "
                + plus_account_name + " TEXT , "
                + can_place_bids + " INTEGER ,PRIMARY KEY ( " + user_id + "))");
    }

    public void DropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + userTable);
    }

    // User
    public void AddUser(User usr) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(user_id, usr.user_id);
        cv.put(first_name, usr.first_name);
        cv.put(last_name, usr.last_name);
        cv.put(email, usr.email);
        cv.put(gender, usr.gender);
        if (usr.create_date != null) {
            cv.put(create_date, DatabaseHelper.iso8601Format.format(usr
                    .create_date));
        }
        if (usr.last_login != null) {
            cv.put(last_login, DatabaseHelper.iso8601Format.format(usr
                    .last_login));
        }
        cv.put(phone_unique_id, usr.phone_unique_id);
        cv.put(facebook_id, usr.facebook_id);
        cv.put(facebook_user_name, usr.facebook_user_name);
        if (usr.birthday != null) {
            cv.put(birthday, DatabaseHelper.iso8601Format.format(usr
                    .birthday));
        }
        cv.put(zipcode, usr.zipcode);
        cv.put(phone, usr.phone);
        cv.put(city_id, usr.city_id);
        cv.put(state_id, usr.state_id);
        cv.put(country_id, usr.country_id);
        cv.put(user_guid, usr.user_guid);
        cv.put(facebook_expires_in, usr.facebook_expires_in);
        cv.put(facebook_access_token, usr.facebook_access_token);
        cv.put(gcm_registration_id, usr.gcm_registration_id);
        if (usr.gcm_registration_date != null) {
            cv.put(gcm_registration_date, DatabaseHelper.iso8601Format.format(usr
                    .gcm_registration_date));
        }
        cv.put(plus_account_name, usr.plus_account_name);
        cv.put(can_place_bids, usr.can_place_bids);
        db.insert(userTable, user_id, cv);
        db.close();
    }

    public int getUserCount() {
        SQLiteDatabase db = dbHelper.GetReadableDB();
        Cursor cur = db.rawQuery("Select * from " + userTable, null);
        int x = cur.getCount();
        cur.close();
        return x;
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = dbHelper.GetReadableDB();
        Cursor cur = db.rawQuery("SELECT * FROM " + userTable, null);
        return cur;
    }

    public User GetUser(int userID) {
        SQLiteDatabase db = dbHelper.GetReadableDB();
        User user = null;
        Cursor cur = db
                .rawQuery("Select * from " + userTable + " WHERE userID=" + userID, null);
        if (cur.moveToFirst()) {
            user = CreateUserFromCursor(cur);
        }
        cur.close();
        return user;
    }

    private User CreateUserFromCursor(Cursor cur) {
        User localUser = new User();


        localUser.user_id = (cur.getInt(0));
        localUser.first_name = (cur.getString(1));
        localUser.last_name = (cur.getString(2));
        localUser.email = (cur.getString(3));
        localUser.gender = (cur.getInt(4));

        try {
            localUser.create_date = (DatabaseHelper.iso8601Format.parse(cur
                    .getString(5)));
            localUser.last_login = (DatabaseHelper.iso8601Format.parse(cur
                    .getString(6)));
            localUser.birthday = (DatabaseHelper.iso8601Format.parse(cur
                    .getString(10)));
            localUser.gcm_registration_date = (DatabaseHelper.iso8601Format.parse(cur
                    .getString(20)));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }

        localUser.phone_unique_id = (cur.getString(7));
        localUser.facebook_id = (cur.getString(8));
        localUser.facebook_user_name = (cur.getString(9));
        localUser.zipcode = (cur.getString(11));
        localUser.phone = (cur.getString(12));
        localUser.city_id = (cur.getString(13));
        localUser.state_id = (cur.getString(14));
        localUser.country_id = (cur.getString(15));
        localUser.user_guid = (cur.getString(16));
        localUser.facebook_expires_in = (cur.getLong(17));
        localUser.facebook_access_token = (cur.getString(18));
        localUser.gcm_registration_id = (cur.getString(19));
        localUser.plus_account_name = (cur.getString(21));
        localUser.can_place_bids = (cur.getInt(22));


        return localUser;
    }

    public int UpdateUser(User usr) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(user_id, usr.user_id);
        cv.put(first_name, usr.first_name);
        cv.put(last_name, usr.last_name);
        cv.put(email, usr.email);
        cv.put(gender, usr.gender);
        if (usr.create_date != null) {
            cv.put(create_date, DatabaseHelper.iso8601Format.format(usr
                    .create_date));
        }
        if (usr.last_login != null) {
            cv.put(last_login, DatabaseHelper.iso8601Format.format(usr
                    .last_login));
        }
        cv.put(phone_unique_id, usr.phone_unique_id);
        cv.put(facebook_id, usr.facebook_id);
        cv.put(facebook_user_name, usr.facebook_user_name);
        if (usr.birthday != null) {
            cv.put(birthday, DatabaseHelper.iso8601Format.format(usr
                    .birthday));
        }
        cv.put(zipcode, usr.zipcode);
        cv.put(phone, usr.phone);
        cv.put(city_id, usr.city_id);
        cv.put(state_id, usr.state_id);
        cv.put(country_id, usr.country_id);
        cv.put(user_guid, usr.user_guid);
        cv.put(facebook_expires_in, usr.facebook_expires_in);
        cv.put(facebook_access_token, usr.facebook_access_token);
        cv.put(gcm_registration_id, usr.gcm_registration_id);
        if (usr.gcm_registration_date != null) {
            cv.put(gcm_registration_date, DatabaseHelper.iso8601Format.format(usr
                    .gcm_registration_date));
        }
        cv.put(plus_account_name, usr.plus_account_name);
        cv.put(can_place_bids, usr.can_place_bids);
        int retVal = db
                .update(userTable, cv, user_id + "=?", new String[]{String
                        .valueOf(usr.user_id)});
        db.close();
        return retVal;
    }

    public void DeleteUser(User usr) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(userTable, user_id + "=?", new String[]{String.valueOf(usr
                .user_id)});
        db.close();
    }

    public ArrayList<User> GetUsers() {
        ArrayList<User> localArrayList = new ArrayList<User>();
        Cursor localCursor = getAllUsers();
        if (localCursor.moveToFirst()) {
            boolean bool2;
            do {
                User localUser = CreateUserFromCursor(localCursor);
                if (localUser != null) {
                    localArrayList.add(localUser);
                }
                bool2 = localCursor.moveToNext();
            }
            while (bool2);
        }
        if ((localCursor != null) && (!localCursor.isClosed())) {
            localCursor.close();
        }
        return localArrayList;
    }
}

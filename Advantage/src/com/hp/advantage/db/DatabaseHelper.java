package com.hp.advantage.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hp.advantage.utils.LogManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    static final String dbName = "advantage.db";
    static final SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private static final int DATABASE_VERSION = 9;
    private SQLiteDatabase dbReadable;

    private UserDBHelper UserDB;

    public DatabaseHelper(Context context) {
        super(context, dbName, null, DATABASE_VERSION);

        UserDB = new UserDBHelper(this);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogManager.Info("Creating DB");
        UserDB.CreateTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogManager.Info("Upgrading DB");

        //if (oldVersion < 19)
        {
            db.execSQL("CREATE TEMPORARY TABLE " + UserDBHelper.userTable + "_backup (" + UserDBHelper.user_id + " INTEGER PRIMARY KEY, " + UserDBHelper.user_guid + " TEXT);");

            //insert data from old table into temp table
            db.execSQL("INSERT INTO " + UserDBHelper.userTable + "_backup SELECT " + UserDBHelper.user_id + ", " + UserDBHelper.user_guid + " FROM " + UserDBHelper.userTable);

            //drop the old table now that your data is safe in the temporary one
            db.execSQL("DROP TABLE IF EXISTS " + UserDBHelper.userTable);

            //recreate the table
            UserDB.CreateTable(db);

            //fill it up using null for missing items
            db.execSQL("INSERT INTO " + UserDBHelper.userTable + " SELECT 	" +
                    UserDBHelper.user_id + "," + // userID
                    "''," + // firstName
                    "''," + // lastName
                    "''," + // email
                    "'" + DatabaseHelper.iso8601Format.format(new Date(0)) + "'," + //"null,"+ // createDate
                    "'" + DatabaseHelper.iso8601Format.format(new Date(0)) + "'," + //"null,"+ // lastLogin
                    "''," + // phoneUniqueID
                    "''," + // facebookAccessToken
                    "''," + // facebookExpiredIN
                    "''," + // facebookUserName
                    "''," + // gender
                    "''," + // address
                    "''," + // zipCode
                    "''," + // phone
                    "''," + // city
                    "''," + // state
                    "''," + // country
                    UserDBHelper.user_guid + "," +  // userGUID
                    "'true'," + // isSupported
                    "'', " + // supportedText
                    "'', " + // gcmRegistrationID
                    "'" + DatabaseHelper.iso8601Format.format(new Date(0)) + "', " + // GcmRegistrationDate
                    "'' " + // plusAccountName
                    "FROM " + UserDBHelper.userTable + "_backup");

            //then drop the temporary table
            db.execSQL("DROP TABLE IF EXISTS " + UserDBHelper.userTable + "_backup");
        }
    }

    public SQLiteDatabase GetReadableDB() {
        if ((dbReadable == null) || (!dbReadable.isOpen())) {
            dbReadable = this.getReadableDatabase();
        }
        return dbReadable;
    }

    public void CloseDB() {
        if (dbReadable != null) {
            dbReadable.close();
        }
    }

    public UserDBHelper GetUserDBHelper() {
        return UserDB;
    }
}

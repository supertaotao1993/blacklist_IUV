/* Copyright Statement:
 *  newly created for blacklist ops by wenpd on 2017/03/20 
 */

package com.gome.blacklist;

import com.gome.blacklist.BlacklistData.BlacklistTable;
import com.gome.blacklist.BlacklistData.RecordlistTable;
import com.gome.blacklist.BlacklistData.WhitelistTable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * BlacklistDatabseHelper.
 */
public class BlacklistDatabseHelper extends SQLiteOpenHelper {
    private static BlacklistDatabseHelper sInstance = null;
	private static final String DATABASE_NAME = "blacklist.db";
	private static final int DB_VERSION = 1;
	
    private BlacklistDatabseHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }
	
    static synchronized BlacklistDatabseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new BlacklistDatabseHelper(context);
        }
        return sInstance;
    }
	
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + BlacklistTable.TABLE_NAME + " ("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BlacklistTable.PHONE_NUMBER + " VARCHAR(40), "
            + BlacklistTable.DISPLAY_NAME + " VARCHAR(40), "
            + BlacklistTable.USER_MODE + " INTEGER DEFAULT 0"
            + ");");
        db.execSQL("CREATE TABLE " + WhitelistTable.TABLE_NAME + " ("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + WhitelistTable.PHONE_NUMBER + " VARCHAR(40), "
            + WhitelistTable.DISPLAY_NAME + " VARCHAR(40), "
            + WhitelistTable.USER_MODE + " INTEGER DEFAULT 0"
            + ");");
	
        db.execSQL("CREATE TABLE " + RecordlistTable.TABLE_NAME + " ("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RecordlistTable.PHONE_NUMBER + " VARCHAR(40), "
            + RecordlistTable.DISPLAY_NAME + " VARCHAR(40), "
            + RecordlistTable.TYPE + " INTEGER DEFAULT 0,"
            + RecordlistTable.PDU + " TEXT,"
            + RecordlistTable.BODY + " TEXT,"
            + RecordlistTable.FORMAT + " VARCHAR(40), "
            + RecordlistTable.SUBID + " INTEGER, "
            + RecordlistTable.READ + " INTEGER DEFAULT 0, "
            + RecordlistTable.LOCATION + " VARCHAR(40), "
            + RecordlistTable.TIME + " INTEGER, "
            + RecordlistTable.USER_MODE + " INTEGER DEFAULT 0"
            + ");");
    }
	
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BlacklistTable.TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + WhitelistTable.TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + RecordlistTable.TABLE_NAME + ";");
        onCreate(db);
        return;
    }
}


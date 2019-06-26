package com.example.astroweather2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "LOCATION";

    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String CITY_ID = "city_id";
    public static final String LATI = "latitude";
    public static final String LONGI = "longitude";

    static final String DB_NAME = "FAVOURITE_LOCATIONS.DB";

    static final int DB_VERSION = 1;

    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT UNIQUE NOT NULL, "
            + CITY_ID + " TEXT NOT NULL, " + LATI + " TEXT NOT NULL, "
            + LONGI + " TEXT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

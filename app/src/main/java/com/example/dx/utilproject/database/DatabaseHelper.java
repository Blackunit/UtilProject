package com.example.dx.utilproject.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by admin on 2017/10/23.
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    public static final String TABLE_NAME ="user";
    public static int version=1;
    private static final String CREATE_TEST=String.format(
            "create table %s(" +
            "id integer primary key autoincrement," +
            "name text," +
            "age text)"
            , TABLE_NAME);;
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TEST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

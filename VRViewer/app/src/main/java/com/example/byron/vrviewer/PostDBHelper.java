package com.example.byron.vrviewer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.byron.vrviewer.DatabaseContract.posts_table;

/**
 * Created by Byron on 11/28/2016.
 */

public class PostDBHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "Scores.db";
    private static final int DATABASE_VERSION = 2;
    public PostDBHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String CreateScoresTable = "CREATE TABLE " + DatabaseContract.POSTS_TABLE + " ("
                + posts_table._ID + " INTEGER PRIMARY KEY,"
                + posts_table.TITLE + " TEXT NOT NULL,"
                + posts_table.USERNAME + " INTEGER NOT NULL,"
                + posts_table.IMAGE_LINK + " TEXT NOT NULL"
                + " );";
        db.execSQL(CreateScoresTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //Remove old values when upgrading.
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.POSTS_TABLE);
    }
}

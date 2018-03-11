package com.example.android.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lsitec219.franco on 19/12/17.
 */

public class PopularMoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "moviesDb.db";

    private static final int VERSION = 2;


    PopularMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE = "CREATE TABLE "  + PopularMoviesContract.TABLE_NAME + " (" +
                PopularMoviesContract._ID                + " INTEGER PRIMARY KEY, " +
                PopularMoviesContract.COLUMN_ORIGINAL_TITLE + " TEXT, " +
                PopularMoviesContract.COLUMN_POSTER_PATH + " TEXT, " +
                PopularMoviesContract.COLUMN_BACKDROP_PATH + " TEXT, " +
                PopularMoviesContract.COLUMN_SYNOPSIS + " TEXT, " +
                PopularMoviesContract.COLUMN_RATING + " TEXT, " +
                PopularMoviesContract.COLUMN_RELEASE_DATE + " TEXT, " +
                PopularMoviesContract.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PopularMoviesContract.TABLE_NAME);
        onCreate(db);
    }
}

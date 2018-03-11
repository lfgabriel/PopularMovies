package com.example.android.popularmovies;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by lsitec219.franco on 19/12/17.
 */

public class PopularMoviesContract implements BaseColumns {

    public static final String AUTHORITY = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "movies";
    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_MOVIES).build();


    //TABLE NAME
    public static final String TABLE_NAME = "movies";


    //COLUMNS
    public static final String COLUMN_ORIGINAL_TITLE = "original_title";
    public static final String COLUMN_POSTER_PATH = "poster_path";
    public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
    public static final String COLUMN_SYNOPSIS = "overview";
    public static final String COLUMN_RATING = "vote_average";
    public static final String COLUMN_RELEASE_DATE = "release_date";
    public static final String COLUMN_TIMESTAMP = "timestamp";
}

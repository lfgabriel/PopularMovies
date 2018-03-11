/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.popularmovies.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.example.android.popularmovies.DetailActivity;
import com.example.android.popularmovies.MainActivity;
import com.example.android.popularmovies.Movie;
import com.example.android.popularmovies.Review;
import com.example.android.popularmovies.Trailer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility functions to handle OpenWeatherMap JSON data.
 */
public final class JsonMoviesUtils {

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * with movies.
     * <p/>
     *
     * @param moviesJsonStr JSON response from server
     *
     * @return Array of Strings with movies
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static List<Movie> getMovieArrayFromJson(Context context, String moviesJsonStr)
            throws JSONException {

        /* List of needed information. */
        final String TMD_RESULTS = "results";

        /* All temperatures are children of the "temp" object */
        final String TMD_MOVIE_ID = "id";
        final String TMD_MOVIE_ORIGINAL_TITLE = "original_title";
        final String TMD_MOVIE_POSTER_THUMBNAIL = "poster_path";
        final String TMD_MOVIE_BACKDROP_PATH = "backdrop_path";
        final String TMD_MOVIE_SYNOPSIS = "overview";
        final String TMD_MOVIE_RATING = "vote_average";
        final String TMD_MOVIE_RELEASE_DATE = "release_date";
        final String TMD_MOVIE_PATH_TO_IMAGE = "http://image.tmdb.org/t/p/w185/";

        final String TMD_STATUS_CODE = "status_code";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);

        /* Is there an error? */
        if (moviesJson.has(TMD_STATUS_CODE)) {
            int errorCode = moviesJson.getInt(TMD_STATUS_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* invalid movie */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray movieArray = moviesJson.getJSONArray(TMD_RESULTS);
        List<Movie> movieList = new ArrayList<Movie>();


        for (int i = 0; i < movieArray.length(); i++) {

            /* Get the JSON object representing the movie */
            JSONObject singleMovie = movieArray.getJSONObject(i);

            Movie tempMovie = new Movie();

            /* Populates Movie */
            tempMovie.setId(singleMovie.getString(TMD_MOVIE_ID));
            tempMovie.setOriginalTitle(singleMovie.getString(TMD_MOVIE_ORIGINAL_TITLE));
            tempMovie.setPosterThumbnail(singleMovie.getString(TMD_MOVIE_POSTER_THUMBNAIL));
            tempMovie.setBackdropPath(singleMovie.getString(TMD_MOVIE_BACKDROP_PATH));
            tempMovie.setSynopsis(singleMovie.getString(TMD_MOVIE_SYNOPSIS));
            tempMovie.setRating(singleMovie.getString(TMD_MOVIE_RATING));
            tempMovie.setPathToImage(TMD_MOVIE_PATH_TO_IMAGE + tempMovie.getPosterThumbnail());
            tempMovie.setReleaseDate(singleMovie.getString(TMD_MOVIE_RELEASE_DATE));

            movieList.add(tempMovie);
        }

        return movieList;
    }



    public static List<Trailer> getTrailersFromJson(Context context, String jsonTrailersResponse)
            throws JSONException {

        /* List of needed information. */
        final String TMD_RESULTS = "results";

        /* All temperatures are children of the "temp" object */
        final String TMD_TRAILER_ID = "id";
        final String TMD_TRAILER_LANG1 = "iso_639_1";
        final String TMD_TRAILER_LANG2 = "iso_3166_1";
        final String TMD_TRAILER_KEY = "key";
        final String TMD_TRAILER_NAME = "name";
        final String TMD_TRAILER_SITE = "site";
        final String TMD_TRAILER_SIZE = "size";
        final String TMD_TRAILER_TYPE = "type";

        final String TMD_STATUS_CODE = "status_code";


        JSONObject moviesJson = new JSONObject(jsonTrailersResponse);

        /* Is there an error? */
        if (moviesJson.has(TMD_STATUS_CODE)) {
            int errorCode = moviesJson.getInt(TMD_STATUS_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* invalid movie */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray trailerArray = moviesJson.getJSONArray(TMD_RESULTS);
        List<Trailer> trailerList = new ArrayList<Trailer>();

        for (int i = 0; i < trailerArray.length(); i++) {
            JSONObject singleTrailer = trailerArray.getJSONObject(i);

            Trailer tempTrailer = new Trailer();

            //Populates Trailer
            tempTrailer.setId(singleTrailer.getString(TMD_TRAILER_ID));
            tempTrailer.setLang1(singleTrailer.getString(TMD_TRAILER_LANG1));
            tempTrailer.setLang2(singleTrailer.getString(TMD_TRAILER_LANG2));
            tempTrailer.setKey(singleTrailer.getString(TMD_TRAILER_KEY));
            tempTrailer.setName(singleTrailer.getString(TMD_TRAILER_NAME));
            tempTrailer.setSite(singleTrailer.getString(TMD_TRAILER_SITE));
            tempTrailer.setSize(singleTrailer.getString(TMD_TRAILER_SIZE));
            tempTrailer.setType(singleTrailer.getString(TMD_TRAILER_TYPE));

            trailerList.add(tempTrailer);
        }

        return trailerList;
    }

    public static List<Review> getReviewsFromJson(DetailActivity detailActivity, String jsonReviewsResponse)
            throws JSONException {

        /* List of needed information. */
        final String TMD_RESULTS = "results";

        /* All temperatures are children of the "temp" object */
        final String TMD_TRAILER_ID = "id";
        final String TMD_REVIEW_AUTHOR = "author";
        final String TMD_REVIEW_CONTENT = "content";
        final String TMD_REVIEW_URL = "url";

        final String TMD_STATUS_CODE = "status_code";


        JSONObject moviesJson = new JSONObject(jsonReviewsResponse);

        /* Is there an error? */
        if (moviesJson.has(TMD_STATUS_CODE)) {
            int errorCode = moviesJson.getInt(TMD_STATUS_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* invalid movie */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray reviewArray = moviesJson.getJSONArray(TMD_RESULTS);
        List<Review> reviewList = new ArrayList<Review>();


        for (int i = 0; i < reviewArray.length(); i++) {
            JSONObject singleTrailer = reviewArray.getJSONObject(i);

            Review tempReview = new Review();

            //Populates Trailer
            tempReview.setId(singleTrailer.getString(TMD_TRAILER_ID));
            tempReview.setAuthor(singleTrailer.getString(TMD_REVIEW_AUTHOR));
            tempReview.setContent(singleTrailer.getString(TMD_REVIEW_CONTENT));
            tempReview.setUrl(singleTrailer.getString(TMD_REVIEW_URL));

            reviewList.add(tempReview);
        }

        return reviewList;
    }

    /**
     * Parse the JSON and convert it into ContentValues that can be inserted into our database.
     *
     * @param context         An application context, such as a service or activity context.
     * @param forecastJsonStr The JSON to parse into ContentValues.
     *
     * @return An array of ContentValues parsed from the JSON.
     */
}
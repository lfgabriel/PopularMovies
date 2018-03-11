package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.android.popularmovies.MoviesAdapter.MoviesAdapterOnClickHandler;
import com.example.android.popularmovies.utilities.JsonMoviesUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapterOnClickHandler, 
        LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;

    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    Context context = this;
    
    //Loader id for our application
    private static final int MOVIES_LOADER_BY_POPULARITY = 273;
    private static final int MOVIES_LOADER_BY_RATING = 963;
    private static final int MOVIES_LOADER_BY_FAVORITES = 230;

    private SQLiteDatabase mDatabase;

    private Cursor cursor;

    PopularMoviesDbHelper popularMoviesDbHelper = new PopularMoviesDbHelper(this);

    List<Movie> movieList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //INITIALIZE LAYOUT ITEMS
        mRecyclerView = (RecyclerView) findViewById (R.id.recyclerview_popularmovies);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        //LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMoviesAdapter = new MoviesAdapter(context, this);
        mRecyclerView.setAdapter(mMoviesAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        //PREFERENCE BETWEEN SHOWING THE MOVIES BY POPULARITY OR RATING
        String order = getPreferenceOfListing();

        //DISPLAY LIST ACCORDING TO THE PREFERENCE
        if (order.equals("popularity"))
            getSupportLoaderManager().initLoader(MOVIES_LOADER_BY_POPULARITY, null, this);
        else if (order.equals("rating"))
            getSupportLoaderManager().initLoader(MOVIES_LOADER_BY_RATING, null, this);
        else if (order.equals("favorites"))
            getSupportLoaderManager().initLoader(MOVIES_LOADER_BY_FAVORITES, null, this);
    }

    /**
     * This method handle preferences of the user about the movie list ordering
     */

    private String getPreferenceOfListing() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.w("Preference: ", sharedPreferences.getString(getString(R.string.movie_list_ordering), "popularity"));
        return sharedPreferences.getString(getString(R.string.movie_list_ordering), "popularity");
    }

    //CHANGE PREFERENCE OF USER
    private void changePreferenceOfOrdering(String order) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(getString(R.string.movie_list_ordering), order);
        sharedPreferencesEditor.apply();
    }

    /**
    This method will display movies data in the layout
    */
    private void showMoviesData() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }


    /**
     This method will display error message in the layout
     */
    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }


    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     *
     * @param movieDetails The weather for the day that was clicked
     */
    @Override
    public void onClick(Movie movieDetails) {
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailsActivity = new Intent(context, destinationClass);
        intentToStartDetailsActivity.putExtra("movieToBeDetailed", movieDetails);
        startActivity(intentToStartDetailsActivity);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                /*
                if (args == null)
                    return;
                    */

                mLoadingIndicator.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public List<Movie> loadInBackground() {
                URL moviesURL = null;

                /* Build URL */
                if (id == MOVIES_LOADER_BY_POPULARITY)
                    moviesURL = NetworkUtils.buildUrlForPopularMovies();
                else if (id == MOVIES_LOADER_BY_RATING)
                    moviesURL = NetworkUtils.buildUrlForRatedMovies();
                else if (id == MOVIES_LOADER_BY_FAVORITES) {

                    //GET FAVORITE MOVIES FROM DATABASE
                    Uri uri = PopularMoviesContract.CONTENT_URI;
                    cursor = context.getContentResolver().query(
                            uri,
                            null,
                            null,
                            null,
                            PopularMoviesContract.COLUMN_TIMESTAMP
                    );

                    movieList = getMoviesFromCursor(cursor);
                    cursor.close();

                    //PRINT MOVIES FROM THE DATABASE
                    /*
                    for (Movie aux : movieList) {
                        Log.w("Printing movie", aux.getId());
                        Log.w("Printing movie", aux.getPathToImage());
                        Log.w("Printing movie", aux.getBackdropPath());
                        Log.w("Printing movie", aux.getSynopsis());
                        Log.w("Printing movie", aux.getRating());

                    }
                    */
                    return movieList;

                }

                try {

                    String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(moviesURL);
                    //Log.w(TAG, "Connection Done. Json: " + jsonMoviesResponse);

                    movieList = JsonMoviesUtils.getMovieArrayFromJson(MainActivity.this, jsonMoviesResponse);

                    return movieList;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }


    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movieList) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if (movieList != null) {
            showMoviesData();
            mMoviesAdapter.setMoviesData(movieList);
        }
        else {
            showErrorMessage();
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }


    public static List<Movie> getMoviesFromCursor(Cursor cursor) {

        List<Movie> movieList = new ArrayList<>();

        while (cursor.moveToNext()) {
            Movie movie = new Movie();
            int index;

            index = cursor.getColumnIndexOrThrow("_id");
            movie.setId(cursor.getString(index));
            index = cursor.getColumnIndexOrThrow("original_title");
            movie.setOriginalTitle(cursor.getString(index));
            index = cursor.getColumnIndexOrThrow("poster_path");
            movie.setPosterThumbnail(cursor.getString(index));
            index = cursor.getColumnIndexOrThrow("backdrop_path");
            movie.setBackdropPath(cursor.getString(index));
            index = cursor.getColumnIndexOrThrow("overview");
            movie.setSynopsis(cursor.getString(index));
            index = cursor.getColumnIndexOrThrow("vote_average");
            movie.setRating(cursor.getString(index));
            movie.setPathToImage(movie.getPosterThumbnail());
            index = cursor.getColumnIndexOrThrow("release_date");
            movie.setReleaseDate(cursor.getString(index));

            movieList.add(movie);
        }

        return movieList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.movie, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_order_by_popularity) {
            mMoviesAdapter.setMoviesData(null);

            changePreferenceOfOrdering("popularity");

            //GET LIST OF MOVIES ORDERED BY POPULARITY (default)
            getSupportLoaderManager().initLoader(MOVIES_LOADER_BY_POPULARITY, null, this);
            return true;
        }

        if (id == R.id.action_order_by_rating) {
            mMoviesAdapter.setMoviesData(null);

            changePreferenceOfOrdering("rating");

            //GET LIST OF MOVIES ORDERED BY RATING
            getSupportLoaderManager().initLoader(MOVIES_LOADER_BY_RATING, null, this);
            Log.w(TAG, "saiu rating");
            return true;
        }

        if (id == R.id.action_show_favorites) {
            mMoviesAdapter.setMoviesData(null);
            changePreferenceOfOrdering("favorites");

            //GET FAVORITE MOVIES FROM DATABASE
            getSupportLoaderManager().initLoader(MOVIES_LOADER_BY_FAVORITES, null, this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

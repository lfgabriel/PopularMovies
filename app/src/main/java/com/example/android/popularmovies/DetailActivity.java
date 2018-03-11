package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.utilities.JsonMoviesUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

/**
 * Created by lsitec219.franco on 11/09/17.
 */

public class DetailActivity extends AppCompatActivity implements TrailersAdapter.TrailersAdapterOnClickHandler,
        ReviewsAdapter.ReviewsAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Movie> {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private static final String POPULAR_MOVIES_SHARE_HASHTAG = "#PopularMovies";

    private Movie mMovieToBeDetailed;

    private TextView mMovieTitle;
    private ImageView mMovieDisplay;
    private TextView mMovieSynopsis;
    private TextView mMovieRating;
    private ImageView mFavoriteStar;
    private TextView mMovieReleaseDate;

    private RecyclerView mTrailersRecyclerView;
    private TrailersAdapter mTrailersAdapter;
    private LinearLayoutManager mTrailerLinearLayoutManager;
    private ScrollView mTrailersScrollView;
    private int mScrollTrailerX, mScrollTrailerY;

    private RecyclerView mReviewsRecyclerView;
    private ReviewsAdapter mReviewsAdapter;
    private LinearLayoutManager mReviewsLinearLayoutManager;
    private ScrollView mReviewsScrollView;
    private int mScrollReviewX, mScrollReviewY;


    private ProgressBar mLoadingIndicator;

    Context context = this;

    private static final int LOADER_TRAILERS_AND_REVIEWS = 155;

    private SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //GET LAYOUT ITEMS
        mMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
        mMovieDisplay = (ImageView) findViewById(R.id.iv_display_movie_image);
        mMovieSynopsis = (TextView) findViewById(R.id.tv_movie_synopsis);
        mMovieRating = (TextView) findViewById(R.id.tv_movie_rating);
        mFavoriteStar = (ImageView) findViewById(R.id.iv_favorite_star);
        mMovieReleaseDate = (TextView) findViewById(R.id.tv_movie_release_date);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator_detail_activity);

        //GET MOVIE THAT WILL BE DISPLAYED IN DETAILS
        Intent intentThatStartedThisActivity = getIntent();
        mMovieToBeDetailed = intentThatStartedThisActivity.getParcelableExtra("movieToBeDetailed");

        //POPULATES MOVIE DETAIL VIEWS
        mMovieTitle.setText(mMovieToBeDetailed.getOriginalTitle());
        Picasso.with(this).load(mMovieToBeDetailed.getPathToImage()).into(mMovieDisplay);
        mMovieSynopsis.setText(mMovieToBeDetailed.getSynopsis());
        mMovieRating.setText("Rating: " + mMovieToBeDetailed.getRating() + "/10" + "");
        mMovieReleaseDate.setText("Release date: " + mMovieToBeDetailed.getReleaseDate());

        //LOAD TRAILERS
        getSupportLoaderManager().initLoader(LOADER_TRAILERS_AND_REVIEWS, null, this);

        //INITIALIZE TRAILERS RECYCLER VIEW
        mTrailersScrollView = (ScrollView) findViewById(R.id.sv_trailers_scrollview);
        mTrailersRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_popularmovies_trailers);
        mTrailerLinearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mTrailersRecyclerView.setLayoutManager(mTrailerLinearLayoutManager);
        mTrailersRecyclerView.setHasFixedSize(true);

        mTrailersAdapter = new TrailersAdapter(context, this);
        mTrailersRecyclerView.setAdapter(mTrailersAdapter);

        //INITIALIZE REVIEWS RECYCLER VIEW
        mReviewsScrollView = (ScrollView) findViewById(R.id.sv_reviews_scrollview);
        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_popularmovies_reviews);
        mReviewsLinearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mReviewsRecyclerView.setLayoutManager(mReviewsLinearLayoutManager);
        mReviewsRecyclerView.setHasFixedSize(true);

        mReviewsAdapter = new ReviewsAdapter(context, this);
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);

        PopularMoviesDbHelper popularMoviesDbHelper = new PopularMoviesDbHelper(this);

        //SET STAR ACCORDINGLY TO USER FAVORITES
        Cursor cursor = getMovieById();

        //cursor.getCount < 1 means not found in the database
        if (cursor.getCount() < 1)
            mFavoriteStar.setImageResource(R.drawable.favorite_star);
        else
            mFavoriteStar.setImageResource(R.drawable.favorite_star_yellow);

    }

    // Display the menu and implement the sharing functionality
    private Intent createShareMovieIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("plan/text")
                .setText(mMovieToBeDetailed.getOriginalTitle() + POPULAR_MOVIES_SHARE_HASHTAG)
                .getIntent();
        return shareIntent;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareMovieIntent());
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        //SAVE SCROLLVIEW POSITIONS
        mScrollTrailerX = mTrailersScrollView.getScrollX();
        mScrollTrailerY = mTrailersScrollView.getScrollY();

        mScrollReviewX = mReviewsScrollView.getScrollX();
        mScrollReviewY = mReviewsScrollView.getScrollY();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //RECOVER SCROLLVIEW POSITIONS
        mTrailersScrollView.post(new Runnable() {
            @Override
            public void run() {
                mTrailersScrollView.scrollTo(mScrollTrailerX, mScrollTrailerY);
            }
        });

        mReviewsScrollView.post(new Runnable() {
            @Override
            public void run() {
                mReviewsScrollView.scrollTo(mScrollReviewX, mScrollReviewY);
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("TRAILERS_SCROLL_POSITION", new int[] {mTrailersScrollView.getScrollX(), mTrailersScrollView.getScrollY()});
        outState.putIntArray("REVIEWS_SCROLL_POSITION", new int[] {mReviewsScrollView.getScrollX(), mReviewsScrollView.getScrollY()});

        /*
        Log.w(TAG, "Salvou: " + mTrailersScrollView.getScrollX() + ", " + mTrailersScrollView.getScrollY());
        Log.w(TAG, "Salvou: " + mReviewsScrollView.getScrollX() + ", " + mReviewsScrollView.getScrollY()); */
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] trailersScrollPosition = savedInstanceState.getIntArray("TRAILERS_SCROLL_POSITION");
        final int[] reviewsScrollPositions = savedInstanceState.getIntArray("REVIEWS_SCROLL_POSITION");

        if (trailersScrollPosition != null) {
            mTrailersScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mTrailersScrollView.scrollTo(trailersScrollPosition[0], trailersScrollPosition[1]);
                }
            });
        }

        if (reviewsScrollPositions != null) {
            mReviewsScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mReviewsScrollView.scrollTo(reviewsScrollPositions[0], reviewsScrollPositions[1]);
                }
            });
        }

        /*
        Log.w(TAG, "Recuperou: " + trailersScrollPosition[0] + ", " + trailersScrollPosition[1]);
        Log.w(TAG, "Recuperou: " + reviewsScrollPositions[0] + ", " + reviewsScrollPositions[1]); */
    }

    @Override
    public Loader<Movie> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<Movie> (this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                mLoadingIndicator.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public Movie loadInBackground() {
                Log.w(TAG, "loadInBackground!!!  id: " + mMovieToBeDetailed.getId());
                URL trailersURL = null;
                URL reviewsURL = null;

                /* Build URL */
                trailersURL = NetworkUtils.buildUrlForTrailers(String.valueOf(mMovieToBeDetailed.getId()));
                reviewsURL = NetworkUtils.buildUrlForReviews(String.valueOf(mMovieToBeDetailed.getId()));

                    try {

                        String jsonTrailersResponse = NetworkUtils.getResponseFromHttpUrl(trailersURL);
                        String jsonReviewsResponse = NetworkUtils.getResponseFromHttpUrl(reviewsURL);

                        List<Trailer> trailerList = JsonMoviesUtils.getTrailersFromJson(DetailActivity.this, jsonTrailersResponse);
                        List<Review> reviewList = JsonMoviesUtils.getReviewsFromJson(DetailActivity.this, jsonReviewsResponse);

                        mMovieToBeDetailed.setTrailers(trailerList);
                        mMovieToBeDetailed.setReviews(reviewList);

                        return mMovieToBeDetailed;


                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
        };
    }

    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        //POPULATE THE ADAPTERS
        mTrailersAdapter.setTrailersData(data.getTrailers());
        mReviewsAdapter.setReviewsData(data.getReviews());
    }

    @Override
    public void onLoaderReset(Loader<Movie> loader) {

    }

    //HANDLE CLICK IN TRAILERS
    @Override
    public void onClick(Trailer trailerDetails) {
        String videoId = trailerDetails.getKey();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+videoId));
        intent.putExtra("VIDEO_ID", videoId);
        startActivity(intent);
    }

    @Override
    public void onClick(Review reviewDetails) {

    }

    public void onClickFavorite(View view) {
        //SEARCH FOR MOVIE IN THE DATABASE
        Cursor cursor = getMovieById();

        //cursor.getCount < 1 means not found in the database
        //in this case should add to the database

        Uri uri = PopularMoviesContract.CONTENT_URI;

        if (cursor.getCount() < 1) {

            ContentValues cv = new ContentValues();
            cv.put(PopularMoviesContract._ID, mMovieToBeDetailed.getId());
            cv.put(PopularMoviesContract.COLUMN_ORIGINAL_TITLE, mMovieToBeDetailed.getOriginalTitle());
            cv.put(PopularMoviesContract.COLUMN_POSTER_PATH, mMovieToBeDetailed.getPathToImage());
            cv.put(PopularMoviesContract.COLUMN_BACKDROP_PATH, mMovieToBeDetailed.getBackdropPath());
            cv.put(PopularMoviesContract.COLUMN_SYNOPSIS, mMovieToBeDetailed.getSynopsis());
            cv.put(PopularMoviesContract.COLUMN_RATING, mMovieToBeDetailed.getRating());
            cv.put(PopularMoviesContract.COLUMN_RELEASE_DATE, mMovieToBeDetailed.getReleaseDate());

            getContentResolver().insert(uri, cv);

            //SET STAR YELLOW
            mFavoriteStar.setImageResource(R.drawable.favorite_star_yellow);

            Toast.makeText(this, "Favorited!", Toast.LENGTH_SHORT).show();
        }

        //if the movie is favorite, should not be anymore and
        //deleted from the database
        else {
            getContentResolver().delete(uri, "_id = " + mMovieToBeDetailed.getId(), null);

            mFavoriteStar.setImageResource(R.drawable.favorite_star);

            Toast.makeText(this, "Unfavorited!", Toast.LENGTH_SHORT).show();
        }

    }

    private Cursor getMovieById() {

        Uri uri = PopularMoviesContract.CONTENT_URI.buildUpon().appendPath(mMovieToBeDetailed.getId()).build();

        Cursor cursor = getContentResolver().query(uri,
                null,
                "_ID = " + mMovieToBeDetailed.getId(),
                null,
                PopularMoviesContract.COLUMN_TIMESTAMP
                );

        return cursor;
    }
}

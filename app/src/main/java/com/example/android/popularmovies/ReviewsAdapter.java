package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Gabriel Franco on 05/09/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private List<Review> mReviewData;
    private Context context;


    private final ReviewsAdapterOnClickHandler mClickHandler;


    public interface ReviewsAdapterOnClickHandler {
        void onClick(Review reviewDetails);
    }

    public ReviewsAdapter(Context context, ReviewsAdapterOnClickHandler clickHandler) {
        this.context = context;
        mClickHandler = clickHandler;
    }



    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        public TextView mReviewTextView;

        public ReviewsAdapterViewHolder(View view) {
            super(view);
            mReviewTextView = (TextView) view.findViewById(R.id.tv_movie_review);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Review reviewDetails = mReviewData.get(adapterPosition);
            mClickHandler.onClick(reviewDetails);
        }
    }


    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new ForecastAdapterViewHolder that holds the View for each list item
     */
    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ReviewsAdapterViewHolder(view);
    }


    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the review
     * for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param reviewsAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ReviewsAdapterViewHolder reviewsAdapterViewHolder, int position) {;
        reviewsAdapterViewHolder.mReviewTextView.setText(mReviewData.get(position).getAuthor() + ": " +
                mReviewData.get(position).getContent());
    }


    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {

        if (mReviewData  == null) return 0;
        return mReviewData.size();

    }

    /**
     * This method is used to set the weather forecast on a ForecastAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new ForecastAdapter to display it.
     *
     * @param reviewsData The new weather data to be displayed.
     */
    public void setReviewsData(List<Review> reviewsData) {
        mReviewData = reviewsData;
        notifyDataSetChanged();
    }


}

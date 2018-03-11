package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Gabriel Franco on 05/09/17.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder> {

    private List<Trailer> mTrailerData;
    private Context context;


    private final TrailersAdapterOnClickHandler mClickHandler;


    public interface TrailersAdapterOnClickHandler {
        void onClick(Trailer trailerDetails);
    }

    public TrailersAdapter(Context context, TrailersAdapterOnClickHandler clickHandler) {
        this.context = context;
        mClickHandler = clickHandler;
    }



    public class TrailersAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        public final ImageView mTrailersImageView;
        public final TextView mTrailerPositionTextView;

        public TrailersAdapterViewHolder(View view) {
            super(view);
            mTrailersImageView = (ImageView) view.findViewById(R.id.iv_play_trailer);
            mTrailerPositionTextView = (TextView) view.findViewById(R.id.tv_trailer_position);
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
            Trailer trailerDetails = mTrailerData.get(adapterPosition);
            mClickHandler.onClick(trailerDetails);
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
    public TrailersAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new TrailersAdapterViewHolder(view);
    }


    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the trailer
     * for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param trailersAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(TrailersAdapterViewHolder trailersAdapterViewHolder, int position) {;
        trailersAdapterViewHolder.mTrailersImageView.setImageResource(R.drawable.play_image);
        trailersAdapterViewHolder.mTrailerPositionTextView.setText("Trailer " + (position + 1));
    }


    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {

        if (mTrailerData  == null) return 0;
        return mTrailerData.size();

    }

    /**
     * This method is used to set the weather forecast on a ForecastAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new ForecastAdapter to display it.
     *
     * @param trailersData The new weather data to be displayed.
     */
    public void setTrailersData(List<Trailer> trailersData) {
        mTrailerData = trailersData;
        notifyDataSetChanged();
    }


}

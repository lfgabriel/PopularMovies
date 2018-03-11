package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * Created by lsitec219.franco on 14/11/17.
 */

public class Movie implements Parcelable {

    String id;
    String originalTitle;                  //original_title
    String posterThumbnail;
    String backdropPath;
    String synopsis;                       //overview
    String rating;                         //vote_average
    String releaseDate;
    String pathToImage;
    List<Trailer> trailers;
    List<Review> reviews;


    public Movie() {}


    public Movie(String id, String originalTitle, String posterThumbnail, String synopsis, String rating, String releaseDate, String pathToImage,ImageView imageViewThumb) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.posterThumbnail = posterThumbnail;
        this.synopsis = synopsis;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.pathToImage = pathToImage;
    }

    public String getId() {
        return id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPosterThumbnail() {
        return posterThumbnail;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPathToImage() {
        return pathToImage;
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public void setPosterThumbnail(String posterThumbnail) {
        this.posterThumbnail = posterThumbnail;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setPathToImage(String pathToImage) {
        this.pathToImage = pathToImage;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.getId());
        out.writeString(this.getOriginalTitle());
        out.writeString(this.getPathToImage());
        out.writeString(this.getBackdropPath());
        out.writeString(this.getSynopsis());
        out.writeString(this.getRating());
        out.writeString(releaseDate);
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        id = in.readString();
        originalTitle = in.readString();
        pathToImage = in.readString();
        backdropPath = in.readString();
        synopsis = in.readString();
        rating = in.readString();
        releaseDate = in.readString();
    }
}



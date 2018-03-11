package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lsitec219.franco on 14/11/17.
 */

public class Review implements Parcelable {

    String id;
    String author;
    String content;
    String url;


    public Review() {}

    public Review(String id, String author, String content, String url) {
        this.id = id;
        this.author= author;
        this.content = content;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAuthor(String lang1) {
        this.author= lang1;
    }

    public void setContent(String lang2) {
        this.content = lang2;
    }

    public void setUrl(String key) {
        this.url = key;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.getId());
        out.writeString(this.getAuthor());
        out.writeString(this.getContent());
        out.writeString(this.getUrl());
    }

    public static final Creator<Review> CREATOR
            = new Creator<Review>() {
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    private Review(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }
}



package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

/**
 * Created by lsitec219.franco on 14/11/17.
 */

public class Trailer implements Parcelable {

    String id;
    String lang1;                  //iso_639_1
    String lang2;                  //iso_3166_1
    String key;
    String name;
    String site;
    String size;
    String type;


    public Trailer() {}

    public Trailer(String id, String lang1, String lang2, String key, String name, String site, String size, String type) {
        this.id = id;
        this.lang1 = lang1;
        this.lang2 = lang2;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getLang1() {
        return lang1;
    }

    public String getLang2() {
        return lang2;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public String getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLang1(String lang1) {
        this.lang1 = lang1;
    }

    public void setLang2(String lang2) {
        this.lang2 = lang2;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.getId());
        out.writeString(this.getLang1());
        out.writeString(this.getLang2());
        out.writeString(this.getKey());
        out.writeString(this.getName());
        out.writeString(this.getSite());
        out.writeString(this.getSize());
        out.writeString(this.getType());
    }

    public static final Creator<Trailer> CREATOR
            = new Creator<Trailer>() {
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    private Trailer(Parcel in) {
        id = in.readString();
        lang1 = in.readString();
        lang2 = in.readString();
        key = in.readString();
        name = in.readString();
        site = in.readString();
        size = in.readString();
        type = in.readString();
    }
}



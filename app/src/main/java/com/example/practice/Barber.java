package com.example.practice;

import android.os.Parcel;
import android.os.Parcelable;

public class Barber implements Parcelable {
    private String name,barberId,username,password;
    private Long Rating;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Barber() {
    }

    protected Barber(Parcel in) {
        name = in.readString();
        barberId = in.readString();
        if (in.readByte() == 0) {
            Rating = null;
        } else {
            Rating = in.readLong();
        }
    }

    public static final Creator<Barber> CREATOR = new Creator<Barber>() {
        @Override
        public Barber createFromParcel(Parcel in) {
            return new Barber(in);
        }

        @Override
        public Barber[] newArray(int size) {
            return new Barber[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRating() {
        return Rating;
    }

    public void setRating(Long rating) {
        this.Rating = rating;
    }

    public String getBarberId() {
        return barberId;
    }

    public void setBarberId(String barberId) {
        this.barberId = barberId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(barberId);
        if (Rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(Rating);
        }
    }
}

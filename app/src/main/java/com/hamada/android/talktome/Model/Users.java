package com.hamada.android.talktome.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Users implements Parcelable {

    public String user_name,user_image,user_thumb_image;
    private String user_state;



    public Users() {
    }

    public Users(String user_name, String user_image, String user_thumb_image,String user_state) {
        this.user_name = user_name;
        this.user_image = user_image;
        this.user_thumb_image=user_thumb_image;
        this.user_state=user_state;
    }

    protected Users(Parcel in) {
        user_name = in.readString();
        user_image = in.readString();
        user_thumb_image = in.readString();
        user_state = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_name);
        dest.writeString(user_image);
        dest.writeString(user_thumb_image);
        dest.writeString(user_state);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel in) {
            return new Users(in);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };

    public String getUser_state() {
        return user_state;
    }

    public void setUser_state(String user_state) {
        this.user_state = user_state;
    }

    public Users(String user_name, String user_image) {
        this.user_name = user_name;
        this.user_image = user_image;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_thumb_image() {
        return user_thumb_image;
    }

    public void setUser_thumb_image(String user_thumb_image) {
        this.user_thumb_image = user_thumb_image;
    }
}

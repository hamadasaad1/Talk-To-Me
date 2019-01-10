package com.hamada.android.talktome.Model;

public class Users {

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

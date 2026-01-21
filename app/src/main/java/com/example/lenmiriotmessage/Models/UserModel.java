package com.example.lenmiriotmessage.Models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserModel {
    public String username;
    public String userLVL;
    public boolean isOnline;

    public UserModel() {
    }

    public UserModel(String username, String userLVL, boolean isOnline) {
        this.username = username;
        this.userLVL = userLVL;
        this.isOnline = isOnline;
    }

    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public String getuserLVL() {
        return userLVL;
    }

    public void setuserLVL(String userLVL) {
        this.userLVL = userLVL;
    }

    public boolean getisOnline() {
        return isOnline;
    }

    public void setisOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }
}

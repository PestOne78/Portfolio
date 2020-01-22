package com.example.pestone.conference.Models;


public class UserProfileModel {

    private String username, email, image, isOnline, UID;

    public UserProfileModel() {
    }

    public UserProfileModel(String username, String email, String isOnline, String image, String UID) {

        this.username = username;
        this.email = email;
        this.isOnline = isOnline;
        this.image = image;
        this.UID = UID;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }
}

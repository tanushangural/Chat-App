package com.example.chatapp;

public class RequestModel {

    private String userId;
    private String username;
    private String photoname;

    public RequestModel(String userId, String username, String photoname) {
        this.userId = userId;
        this.username = username;
        this.photoname = photoname;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhotoname() {
        return photoname;
    }

    public void setPhotoname(String photoname) {
        this.photoname = photoname;
    }
}

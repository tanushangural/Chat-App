package com.example.chatapp;

public class FindFriendClass {
    private String username,photoname,userid;
    private boolean isSentRequest;

    public FindFriendClass(String username, String photoname, String userid, boolean isSentRequest) {
        this.username = username;
        this.photoname = photoname;
        this.userid = userid;
        this.isSentRequest = isSentRequest;
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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public boolean isSentRequest() {
        return isSentRequest;
    }

    public void setSentRequest(boolean sentRequest) {
        isSentRequest = sentRequest;
    }
}

package com.example.chatapp;

public class ChatlistModel {
    private String userId;
    private String username;
    private String lastMessage;
    private String unreadCount;
    private String lastmessageTime;
    private String photoname;

    public ChatlistModel(String userId, String username, String lastMessage, String unreadCount, String lastmessageTime, String photoname) {
        this.userId = userId;
        this.username = username;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
        this.lastmessageTime = lastmessageTime;
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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(String unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getLastmessageTime() {
        return lastmessageTime;
    }

    public void setLastmessageTime(String lastmessageTime) {
        this.lastmessageTime = lastmessageTime;
    }

    public String getPhotoname() {
        return photoname;
    }

    public void setPhotoname(String photoname) {
        this.photoname = photoname;
    }
}

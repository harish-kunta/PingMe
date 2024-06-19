package com.harish.hk185080.chatterbox.model;

public class ChatThread {

    String name, email, uid, threadID, imageUrl;

    public ChatThread(String uid, String name, String email) {
        this.name = name;
        this.email = email;
        this.uid = uid;
    }

    public ChatThread(String uid, String threadID) {
        this.uid = uid;
        this.threadID = threadID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String image) {
        this.email = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThreadID() {
        return threadID;
    }

    public void setThreadID(String threadID) {
        this.threadID = threadID;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String status) {
        this.uid = status;
    }
}

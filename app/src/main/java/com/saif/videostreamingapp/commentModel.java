package com.saif.videostreamingapp;

public class commentModel {
    String username, userprofile, commentText, date, time, uid;

    public commentModel() {
    }

    public commentModel(String username, String userprofile, String commentText, String date, String time, String uid) {
        this.username = username;
        this.userprofile = userprofile;
        this.commentText = commentText;
        this.date = date;
        this.time = time;
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserprofile() {
        return userprofile;
    }

    public void setUserprofile(String userprofile) {
        this.userprofile = userprofile;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

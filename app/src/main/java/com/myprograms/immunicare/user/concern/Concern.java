package com.myprograms.immunicare.user.concern;

import com.google.firebase.Timestamp;

public class Concern {
    private String fullName;
    private String email;
    private String title;
    private String desc;
    private String userId;
    private Timestamp timestamp;

    public Concern() {
    }

    public Concern(String fullName, String email, String title, String desc, String userId, Timestamp timestamp) {
        this.fullName = fullName;
        this.email = email;
        this.title = title;
        this.desc = desc;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

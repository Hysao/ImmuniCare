package com.myprograms.immunicare.calendar;

public class Reminder {
    private String title;
    private String description;
    private String date;
    private String userId;

    public Reminder() {
        // Default constructor required for Firestore
    }

    public Reminder(String title, String description, String date, String userId) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getUserId() {
        return userId;
    }
}

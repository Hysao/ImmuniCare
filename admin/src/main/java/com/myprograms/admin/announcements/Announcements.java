package com.myprograms.admin.announcements;

import com.google.firebase.Timestamp;

public class Announcements {


    private String announcementTitle;
    private String announcementDesc;
    private Timestamp announcementDate;;
    private String announcementAuthor;

    public Announcements() {
    }

    public Announcements(String title, String desc, Timestamp date, String author) {
        this.announcementTitle = title;
        this.announcementDesc = desc;
        this.announcementDate = date;
        this.announcementAuthor = author;
    }

    public String getAnnouncementTitle() {
        return announcementTitle;
    }

    public void setAnnouncementTitle(String announcementTitle) {
        this.announcementTitle = announcementTitle;
    }

    public String getAnnouncementDesc() {
        return announcementDesc;
    }

    public void setAnnouncementDesc(String announcementDesc) {
        this.announcementDesc = announcementDesc;
    }

    public Timestamp getAnnouncementDate() {
        return announcementDate;
    }

    public void setAnnouncementDate(Timestamp announcementDate) {
        this.announcementDate = announcementDate;
    }

    public String getAnnouncementAuthor() {
        return announcementAuthor;
    }

    public void setAnnouncementAuthor(String announcementAuthor) {
        this.announcementAuthor = announcementAuthor;
    }
}

package com.myprograms.admin.schedule;

import java.util.List;

public class Schedule {

    public String date;
    public String status;
    public String userEmail;
    public String userId;
    public String userName;

    public List<String> vaccines;
    public String visit;
    private String documentId;

    public Schedule() {
    }

    public Schedule(String documentId) {
        this.documentId = documentId;
    }

    public Schedule(String date, String status, String userEmail, String userId, String userName, List<String> vaccines, String visit) {
        this.date = date;
        this.status = status;
        this.userEmail = userEmail;
        this.userId = userId;
        this.userName = userName;
        this.vaccines = vaccines;
        this.visit = visit;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getVaccines() {
        return vaccines;
    }

    public void setVaccines(List<String> vaccines) {
        this.vaccines = vaccines;
    }

    public String getVisit() {
        return visit;
    }

    public void setVisit(String visit) {
        this.visit = visit;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getEachVaccine(){
        return String.join("\n", vaccines);
    }
}

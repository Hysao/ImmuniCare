package com.myprograms.admin.validating;

public class Users {

    String uid;
    String name;
    String email;
    String address;
    String status;
    Boolean isHw;

    public Users(String uid, String name, String email, String address, String status, Boolean isHw) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.address = address;
        this.status = status;
        this.isHw = isHw;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getHw() {
        return isHw;
    }

    public void setHw(Boolean hw) {
        isHw = hw;
    }
}

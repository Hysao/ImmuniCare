package com.myprograms.admin.validating;

public class Users {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private boolean isHw;
    private String isVerified;
    private String photo;

    public Users() {
    }


    public Users(String userId, String name, String email, String phone, String address, boolean isHw, String isVerified, String photo) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.isHw = isHw;
        this.isVerified = isVerified;
        this.photo = photo;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isHw() {
        return isHw;
    }

    public void setHw(boolean isHw) {
        this.isHw = isHw;
    }

    public String getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}

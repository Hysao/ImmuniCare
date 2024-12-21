package com.myprograms.admin.vaccines;

public class Vaccines {
    private String id;
    private String name;
    private String stock;
    private String manufacturedDate;
    private String expiryDate;

    public Vaccines() {

    }

    public Vaccines(String id, String name, String stock, String manufacturedDate, String expiryDate) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.manufacturedDate = manufacturedDate;
        this.expiryDate = expiryDate;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getManufacturedDate() {
        return manufacturedDate;
    }

    public void setManufacturedDate(String manufacturedDate) {
        this.manufacturedDate = manufacturedDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}

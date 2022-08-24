package com.raihan.stella.model;

public class Report {

    String productName = "";
    String productId = "";
    String date = "";
    String color = "";
    String productQty = "";
    String buyPrice = "";
    String sellPrice = "";


    public Report() {

    }

    public Report(String productName, String productId, String date, String color, String productQty, String buyPrice, String sellPrice) {
        this.productName = productName;
        this.productId = productId;
        this.date = date;
        this.color = color;
        this.productQty = productQty;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;

    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getProductQty() {
        return productQty;
    }

    public void setProductQty(String productQty) {
        this.productQty = productQty;
    }

    public String getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(String buyPrice) {
        this.buyPrice = buyPrice;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(String sellPrice) {
        this.sellPrice = sellPrice;
    }

}
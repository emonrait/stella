package com.raihan.stella.model;

public class Stock {
    String id="";
    String productName="";
    String productId="";
    String date="";
    String color="";
    String productMrp="";
    String productPercent="";
    String productQty="";
    String previousStock="";
    String flag="";
    String updateBy="";

    public Stock(String id, String productName, String productId, String date, String color, String productMrp, String productPercent, String productQty, String previousStock, String flag, String updateBy) {
        this.id = id;
        this.productName = productName;
        this.productId = productId;
        this.date = date;
        this.color = color;
        this.productMrp = productMrp;
        this.productPercent = productPercent;
        this.productQty = productQty;
        this.previousStock = previousStock;
        this.flag = flag;
        this.updateBy = updateBy;
    }

    public Stock() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getProductMrp() {
        return productMrp;
    }

    public void setProductMrp(String productMrp) {
        this.productMrp = productMrp;
    }

    public String getProductPercent() {
        return productPercent;
    }

    public void setProductPercent(String productPercent) {
        this.productPercent = productPercent;
    }

    public String getProductQty() {
        return productQty;
    }

    public void setProductQty(String productQty) {
        this.productQty = productQty;
    }

    public String getPreviousStock() {
        return previousStock;
    }

    public void setPreviousStock(String previousStock) {
        this.previousStock = previousStock;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }
}

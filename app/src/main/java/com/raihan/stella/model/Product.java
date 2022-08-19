package com.raihan.stella.model;

public class Product {
    String id="";
    String productName="";
    String productId="";
    String date="";
    String color="";
    String flag="";
    String updateBy="";

    public Product() {
    }

    public Product(String id, String productName, String productId, String date, String color, String flag,String updateBy) {
        this.id = id;
        this.productName = productName;
        this.productId = productId;
        this.date = date;
        this.color = color;
        this.flag = flag;
        this.updateBy = updateBy;
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

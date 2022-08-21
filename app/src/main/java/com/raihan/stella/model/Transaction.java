package com.raihan.stella.model;

public class Transaction {
    String id = "";
    String date = "";
    String customerName = "";
    String customerMobile = "";
    String amount = "";
    String remarks = "";
    String flag = "";
    String updateBy = "";

    public Transaction() {
    }

    public Transaction(String id, String date, String customerName, String customerMobile, String amount, String remarks, String flag, String updateBy) {
        this.id = id;
        this.date = date;
        this.customerName = customerName;
        this.customerMobile = customerMobile;
        this.amount = amount;
        this.remarks = remarks;
        this.flag = flag;
        this.updateBy = updateBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

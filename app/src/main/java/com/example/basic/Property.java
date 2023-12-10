package com.example.basic;

public class Property {

    private String address;
    private String id;
    private String title;
    private String dolilNo;
    private String previousOwner;
    private String taxReceipts;
    private String price;
    private String status;
    private String division;
    //private String prop_imgUrl;
    private String fee;

    public Property(){}

    public Property(String address, String title, String dolilNo, String previousOwner, String taxReceipts, String price, String id, String division,String fee) {
        this.address = address;
        this.title = title;
        this.dolilNo = dolilNo;
        this.previousOwner = previousOwner;
        this.taxReceipts = taxReceipts;
        this.price = price;
        this.id=id;
        this.division = division;
        this.fee = fee;
        //this.prop_imgUrl = prop_imgUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDolilNo() {
        return dolilNo;
    }

    public void setDolilNo(String dolilNo) {
        this.dolilNo = dolilNo;
    }

    public String getPreviousOwner() {
        return previousOwner;
    }

    public void setPreviousOwner(String previousOwner) {
        this.previousOwner = previousOwner;
    }

    public String getTaxReceipts() {
        return taxReceipts;
    }

    public void setTaxReceipts(String taxReceipts) {
        this.taxReceipts = taxReceipts;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }
}
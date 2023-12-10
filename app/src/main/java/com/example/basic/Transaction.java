package com.example.basic;

public class Transaction {
    String landno;
    String receiver;
    String signature;
    String imageUrl;
    String imagename;


    public Transaction(){}


    public Transaction(String receiver, String landno, String signature,String imageUrl,String imagename) {
        this.receiver = receiver;
        this.landno = landno;
        this.signature = signature;
        this.imageUrl= imageUrl;
        this.imagename=imagename;
    }

    public String getLandno() {
        return landno;
    }

    public void setLandno(String landno) {
        this.landno = landno;
    }

    public String getReceiver() {return receiver;}

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getImageUrl() {return imageUrl;}

    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}

    public String getImagename() {return imagename;}

    public void setImagename(String imagename) {this.imagename = imagename;}

}

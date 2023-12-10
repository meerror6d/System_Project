package com.example.basic;

public class Profile {

    String nid;
    String tin;
    String balance;
    String userImgUrl;
    String userImg;


    public Profile() {}

    public Profile(String nid, String tin, String balance, String userImgUrl) {
        this.nid = nid;
        this.tin = tin;
        this.balance = balance;
        this.userImgUrl = userImgUrl;
        //this.userImg = userImg;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getUserImgUrl() {
        return userImgUrl;
    }

    public void setUserImgUrl(String userImgUrl) {
        this.userImgUrl = userImgUrl;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }
}


package com.example.basic;

public class Admin {

    private String adminId;
    private String email;
    private String username;
    private String passwordad;
    private String nid;
    private  String uniquename;


    public Admin() {
        // Default constructor required for Firebase
    }


    public Admin(String adminId, String username, String email, String passwordad, String nid) {
        this.adminId = adminId;
        this.username = username;
        this.email = email;
        this.passwordad = passwordad;
        this.nid = nid;
        this.uniquename=uniquename;

    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordad() {return passwordad;}

    public void setPasswordad(String passwordad) {this.passwordad = passwordad;}

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getUniquename() {
        return uniquename;
    }

    public void setUniquename(String uniquename) {
        this.uniquename = uniquename;
    }
}

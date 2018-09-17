package org.properti.analisa.financialcheck.model;

public class User {

    private String userId;
    private String nama;
    private String email;
    private String phone;

    public User(String userId, String nama, String email, String phone) {
        this.userId = userId;
        this.nama = nama;
        this.email = email;
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public String getNama() {
        return nama;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}

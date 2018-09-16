package org.properti.analisa.financialcheck.model;

import java.util.List;

public class User {

    private String id;
    private String userId;
    private String nama;
    private String email;
    private String phone;

    public User(String id, String userId, String nama, String email, String phone) {
        this.id = id;
        this.userId = userId;
        this.nama = nama;
        this.email = email;
        this.phone = phone;
    }

    public String getId() {
        return id;
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

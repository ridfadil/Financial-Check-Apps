package org.properti.analisa.financialcheck.model;

public class User {

    private String nama;
    private String email;
    private String phone;

    public User(){

    }

    public User(String nama, String email, String phone) {
        this.nama = nama;
        this.email = email;
        this.phone = phone;
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

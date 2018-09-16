package org.properti.analisa.financialcheck.model;

public class Common {

//    private String dataId;
    private String judul;
    private String harga;
    private String image;

    public Common(String judul, String harga, String image) {
        this.judul = judul;
        this.harga = harga;
        this.image = image;
    }

    public String getJudul() {
        return judul;
    }

    public String getHarga() {
        return harga;
    }

    public String getImage() {
        return image;
    }
}

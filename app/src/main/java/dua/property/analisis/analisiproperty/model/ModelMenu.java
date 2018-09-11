package dua.property.analisis.analisiproperty.model;

public class ModelMenu {
    private String judul;
    private String harga;
    private int imageMenu;
    private int imagePencil;

    public ModelMenu(String judul, String harga, int imageMenu, int imagePencil) {
        this.judul = judul;
        this.harga = harga;
        this.imageMenu = imageMenu;
        this.imagePencil = imagePencil;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public int getImageMenu() {
        return imageMenu;
    }

    public void setImageMenu(int imageMenu) {
        this.imageMenu = imageMenu;
    }

    public int getImagePencil() {
        return imagePencil;
    }

    public void setImagePencil(int imagePencil) {
        this.imagePencil = imagePencil;
    }
}

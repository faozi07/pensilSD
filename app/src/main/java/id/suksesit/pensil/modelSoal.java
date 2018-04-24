package id.suksesit.pensil;

/*
 * Created by regopantes_apps on 24/04/18.
 */

public class modelSoal {
    private String kategori;
    private String pertanyaan;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public String getKategori() {
        return kategori;
    }

    void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getPertanyaan() {
        return pertanyaan;
    }

    void setPertanyaan(String pertanyaan) {
        this.pertanyaan = pertanyaan;
    }

    public String getStatus() {
        return status;
    }

    void setStatus(String status) {
        this.status = status;
    }

    private String status = "";
}

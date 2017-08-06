package id.rentist.mitrarentist.modul;

/**
 * Created by mdhif on 16/07/2017.
 */

public class ComplainModul {
    private String namaPelanggan, tglKirim, perihal, detilKeluhan, info;

    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
    }
    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public void setTglKirim(String tglKirim) {
        this.tglKirim = tglKirim;
    }
    public String getTglKirim() {
        return tglKirim;
    }

    public void setPerihal(String perihal) {
        this.perihal = perihal;
    }
    public String getPerihal() {
        return perihal;
    }

    public void setDetilKeluhan(String detilKeluhan) {
        this.detilKeluhan = detilKeluhan;
    }
    public String getDetilKeluhan() {
        return detilKeluhan;
    }

    public void setInfo(String info) {
        this.info = info;
    }
    public String getInfo() {
        return info;
    }
}

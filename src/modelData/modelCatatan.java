package modelData;

public class modelCatatan {
    int id_catatan;
    String judul_catatan;
    String isi;
    String tgl_pembuatan;
    byte[] lampiran_gambar;
    String pengingat;
    String kategori;

    public modelCatatan(int id_catatan, String judul_catatan, String isi, String tgl_pembuatan, byte[] lampiran_gambar, String pengingat, String kategori) {
        this.id_catatan = id_catatan;
        this.judul_catatan = judul_catatan;
        this.isi = isi;
        this.tgl_pembuatan = tgl_pembuatan;        
        this.lampiran_gambar = lampiran_gambar;        
        this.pengingat = pengingat;                
        this.kategori = kategori;
    }   

    public byte[] getLampiran_gambar() {
        return lampiran_gambar;
    }

    public void setLampiran_gambar(byte[] lampiran_gambar) {
        this.lampiran_gambar = lampiran_gambar;
    }      
    
    public String getPengingat() {
        return pengingat;
    }

    public void setPengingat(String pengingat) {
        this.pengingat = pengingat;
    }
    
    public int getId_catatan() {
        return id_catatan;
    }

    public void setId_catatan(int id_catatan) {
        this.id_catatan = id_catatan;
    }

    public String getJudul_catatan() {
        return judul_catatan;
    }

    public void setJudul_catatan(String judul_catatan) {
        this.judul_catatan = judul_catatan;
    }

    public String getIsi() {
        return isi;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public String getTgl_pembuatan() {
        return tgl_pembuatan;
    }

    public void setTgl_pembuatan(String tgl_pembuatan) {
        this.tgl_pembuatan = tgl_pembuatan;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }           
}

package component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.sql.Connection;
import java.sql.Statement;
import javax.swing.JOptionPane;
import modelData.modelKategori;
import remindnote_app.kategori;
import remindnote_app.koneksi_database;


public class kategoriItem extends javax.swing.JPanel {
    koneksi_database koneksiDB = new koneksi_database();

    public kategoriItem() {
        initComponents();
        setOpaque(false);
    }
    
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }
    
    private modelKategori data;

    public modelKategori getData() {
        return data;
    }
        
    public void setData(modelKategori data) {
        this.data = data;
        lbl_kategori.setText(data.getNama_kategori());
//        txtarea_isi.setText(data.getIsi());
    }
    
    
    
    @Override
    public void paint(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.BLACK);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        if(selected) {
            g2.setColor((new Color(94,156,255)));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
        }
        g2.dispose();
        super.paint(grphcs);
    }

    public void deleteKategori(int id_kategori) {
        if(id_kategori != 0){
//          Jika bukan kategori "All"
            try (Connection koneksi = koneksiDB.getConnection()) {
            Statement stmt = koneksi.createStatement();            
                String query_hapusKategori = "delete from kategori where id_kategori = " + id_kategori;            
                int rows = stmt.executeUpdate(query_hapusKategori);
                if (rows > 0) {
                // Catatan berhasil dihapus dari database
                // Anda bisa mengambil tindakan tambahan jika diperlukan
                JOptionPane.showMessageDialog(this, "Kategori Berhasil Dihapus.", "Info", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Catatan tidak ditemukan di database
                    JOptionPane.showMessageDialog(this, "Kategori tidak ditemukan.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            
            } catch (Exception e) {
                // Tangani kesalahan koneksi atau query
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan dalam menghapus Kategori.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else{
            JOptionPane.showMessageDialog(null, "Kategori Utama Tidak Bisa Dihapus");
        }
        
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbl_kategori = new javax.swing.JLabel();
        deleteBtn = new javax.swing.JButton();

        lbl_kategori.setText("kategori");

        deleteBtn.setText("x");
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_kategori)
                    .addComponent(deleteBtn))
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
        if(data != null){
            deleteKategori(data.getId_kategori());
        }   
//        JOptionPane.showMessageDialog(null, "method berjalan");
//        kategori Kategori = new kategori();
//        Kategori.dispose();
//        Kategori.setVisible(true);
//        Kategori.refreshKategori(true);
    }//GEN-LAST:event_deleteBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteBtn;
    private javax.swing.JLabel lbl_kategori;
    // End of variables declaration//GEN-END:variables
}

package component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.sql.Statement;
import java.sql.Connection;
import javax.swing.JOptionPane;
import modelData.modelCatatan;
import remindnote_app.Main;
import remindnote_app.koneksi_database;

public class catatan extends javax.swing.JPanel {
    koneksi_database koneksiDB = new koneksi_database();
    
    public catatan() {
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
    
    private modelCatatan data;

    public modelCatatan getData() {
        return data;
    }
        
    public void setData(modelCatatan data) {
        this.data = data;
        txt_judul.setText(data.getJudul_catatan());
        txtarea_isi.setText(data.getIsi());
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
    
    public void deleteCatatan(int id_catatan) {
        try (Connection koneksi = koneksiDB.getConnection()) {
            Statement stmt = koneksi.createStatement();
            String query_hapusCatatan = "delete from catatan where id_catatan = " + id_catatan;
            int rows = stmt.executeUpdate(query_hapusCatatan);
            if (rows > 0) {
            // Catatan berhasil dihapus dari database
            // Anda bisa mengambil tindakan tambahan jika diperlukan
            JOptionPane.showMessageDialog(this, "Catatan Berhasil Dihapus.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Catatan tidak ditemukan di database
                JOptionPane.showMessageDialog(this, "Catatan tidak ditemukan.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            // Tangani kesalahan koneksi atau query
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan dalam menghapus catatan.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txt_judul = new javax.swing.JLabel();
        txtarea_isi = new javax.swing.JTextArea();
        btn_delete = new javax.swing.JButton();

        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        setMaximumSize(new java.awt.Dimension(390, 135));

        txt_judul.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txt_judul.setText("Judul Catatan");

        txtarea_isi.setEditable(false);
        txtarea_isi.setBackground(new java.awt.Color(0, 0, 0));
        txtarea_isi.setColumns(10);
        txtarea_isi.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtarea_isi.setRows(2);
        txtarea_isi.setTabSize(6);
        txtarea_isi.setText("Deskripsi Catatan");
        txtarea_isi.setWrapStyleWord(true);
        txtarea_isi.setBorder(null);
        txtarea_isi.setEnabled(false);
        txtarea_isi.setFocusable(false);
        txtarea_isi.setMaximumSize(new java.awt.Dimension(110, 26));
        txtarea_isi.setOpaque(false);
        txtarea_isi.setPreferredSize(new java.awt.Dimension(20, 36));

        btn_delete.setText("Delete");
        btn_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_deleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_judul, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtarea_isi, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 10, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_delete)))
                .addGap(15, 15, 15))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txt_judul, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(txtarea_isi, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_delete)
                .addContainerGap(16, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btn_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_deleteActionPerformed
        if(data != null){
            deleteCatatan(data.getId_catatan());
        }       
        
    }//GEN-LAST:event_btn_deleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_delete;
    private javax.swing.JLabel txt_judul;
    private javax.swing.JTextArea txtarea_isi;
    // End of variables declaration//GEN-END:variables
}

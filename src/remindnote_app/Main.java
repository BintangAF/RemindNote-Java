package remindnote_app;

import com.formdev.flatlaf.FlatDarkLaf;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import modelData.modelCatatan;
import component.catatan;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Main extends javax.swing.JFrame {
    koneksi_database koneksiDB = new koneksi_database();
    Connection koneksi = koneksiDB.getConnection();    
   
    public Main() {
        initComponents();
        search_txt.putClientProperty("JTextField.placeholderText", "Search Note by Title");
        judulTxt.putClientProperty("JTextField.placeholderText", "Title");           
        isiTxtArea.putClientProperty("JComponent.roundRect", true);
        tampilItem();
        tampilKategoriComBox();   
        tampilFilter();
        mulaiPengecekanPengingat();
    }    

    public void mulaiPengecekanPengingat() {
        int delay = 30000; // Pengecekan setiap 30 detik
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cekPengingat();
            }
        };
        new Timer(delay, taskPerformer).start();
    }
    
    public void cekPengingat() {
        String query_tampil_data = "SELECT id_catatan, judul_catatan, pengingat FROM catatan";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();

        try (Statement stmt = koneksi.createStatement();
             ResultSet rs = stmt.executeQuery(query_tampil_data);) 
        {
            while(rs.next()) {
                int id_catatan = rs.getInt("id_catatan");
                String judul = rs.getString("judul_catatan");
                String pengingat = rs.getString("pengingat");

                if (pengingat != null && !pengingat.isEmpty()) {
                    Date pengingatDate = dateFormat.parse(pengingat);

                    if (now.compareTo(pengingatDate) >= 0) {
                        // Tampilkan notifikasi
                        JOptionPane.showMessageDialog(null, "Pengingat: " + judul, "Notifikasi", JOptionPane.INFORMATION_MESSAGE);

                        // Update database untuk menandai pengingat sudah diberitahukan
                        String updateQuery = "UPDATE catatan SET pengingat = NULL WHERE id_catatan = " + id_catatan;
                        try (Statement updateStmt = koneksi.createStatement()) {
                            updateStmt.executeUpdate(updateQuery);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal mengecek pengingat: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }        
    }
    
    private modelCatatan selectedItem = null;
    
    public void isiForm(modelCatatan data) {
        selectedItem = data;
        judulTxt.setText(data.getJudul_catatan());
        isiTxtArea.setText(data.getIsi());
        lbl_tgl_pembuatan.setText(data.getTgl_pembuatan());
        if(data.getPengingat() != null && !data.getPengingat().isEmpty()) {
            String[] dateTime = data.getPengingat().split(" ");
            if(dateTime.length == 2) {
                txt_date.setText(dateTime[0]);
                txt_time.setText(dateTime[1]);
            }
        }
        
        if (data.getKategori() == null) {
            comBoxKategori.setSelectedItem("All");
        } else {
            comBoxKategori.setSelectedItem(data.getKategori());
        }            
        
        if (data.getLampiran_gambar() != null) {
            ImageIcon imageIcon = new ImageIcon(data.getLampiran_gambar());
        
            int labelWidth = lbl_gambar.getWidth();
            int labelHeight = lbl_gambar.getHeight();

            int imageWidth = imageIcon.getIconWidth();
            int imageHeight = imageIcon.getIconHeight();

            double scaleX = (double) labelWidth / (double) imageWidth;
            double scaleY = (double) labelHeight / (double) imageHeight;
            double scale = Math.min(scaleX, scaleY);

            Image scaledImage = imageIcon.getImage().getScaledInstance((int) (scale * imageWidth), (int) (scale * imageHeight), Image.SCALE_SMOOTH);
            lbl_gambar.setIcon(new ImageIcon(scaledImage));
        } else {
            lbl_gambar.setIcon(null);
        }
        
    }
    
    public void addItemCatatan(modelCatatan data) {
        catatan Item = new catatan();
        Item.setData(data);
        Item.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                if(SwingUtilities.isLeftMouseButton(me)) {
                    isiForm(data);
                }
            }
        });
        panelItems.add(Item);
        panelItems.repaint();
        panelItems.revalidate();        
    }
    
    public void tampilItem() {
        String query_tampil_data = "select catatan.*, kategori.nama_kategori "
                                + "from catatan "
                                + "left join kategori on kategori.id_kategori = catatan.id_kategori "
                                + "order by catatan.tgl_pembuatan desc";
        try (Statement stmt = koneksi.createStatement();
            ResultSet rs = stmt.executeQuery(query_tampil_data);) 
        {
            while(rs.next()) {
                int id_catatan = rs.getInt("id_catatan");
                String judul = rs.getString("judul_catatan");
                String isi = rs.getString("isi");
                String tgl = rs.getString("tgl_pembuatan");
                byte[] lampiran_gambar = rs.getBytes("lampiran_gambar");
                String pengingat = rs.getString("pengingat");
                String nama_kategori = rs.getString("nama_kategori");

                modelCatatan item = new modelCatatan(id_catatan, judul, isi, tgl, lampiran_gambar, pengingat, nama_kategori);
                addItemCatatan(item);
            }
            
        } catch (Exception e) {
             e.printStackTrace();
            // Tampilkan pesan kesalahan jika ada masalah dengan database
            JOptionPane.showMessageDialog(this, "Gagal mengambil data dari database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }        
    }            
    
    private void tampilKategoriComBox() {
        String query = "SELECT * FROM kategori order by id_kategori asc";        
        try {
            Statement stmt = koneksi.createStatement();
            ResultSet rs = stmt.executeQuery(query);
//            int rows = stmt.executeUpdate(query);
            comBoxKategori.removeAllItems();
            comBoxKategori.addItem("Select Category"); // Pilihan Default

//            if(rows > 0) {
                comBoxKategori.removeAllItems();
                while (rs.next()) {
    //                int id = rs.getInt("id_kategori");
                    String nama_kategori = rs.getString("nama_kategori");
                    comBoxKategori.addItem(nama_kategori);
                }
//            }

            if (comBoxKategori.getItemCount() == 0) {
                comBoxKategori.removeAllItems();
                comBoxKategori.addItem("No Category"); // Pilihan Default
            }

//            rs.close();
//            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengambil data kategori dari database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void tampilFilter() {
        String query = "SELECT * FROM kategori order by id_kategori asc";        
        try {
            Statement stmt = koneksi.createStatement();
            ResultSet rs = stmt.executeQuery(query);
//            int rows = stmt.executeUpdate(query);
            comBox_filter.removeAllItems();
            comBox_filter.addItem("All Category"); // Pilihan Default

//            if(rows > 0) {
                comBox_filter.removeAllItems();
                while (rs.next()) {
    //                int id = rs.getInt("id_kategori");
                    String nama_kategori = rs.getString("nama_kategori");
                    comBox_filter.addItem(nama_kategori);
                }
//            }

//            if (comBoxKategori.getItemCount() == 1) {
//                comBoxKategori.removeAllItems();
//                comBoxKategori.addItem("No Category"); // Pilihan Default
//            }

//            rs.close();
//            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengambil data kategori dari database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchNotes(String keyword) {
        String query = "SELECT catatan.*, kategori.* FROM catatan join kategori on kategori.id_kategori = catatan.id_kategori WHERE judul_catatan LIKE '%" + keyword + "%'";

        try (Statement stmt = koneksi.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Clear previous search results
            panelItems.removeAll();
            panelItems.repaint();
            panelItems.revalidate();

            // Loop through the result set and add items to the panel
            while (rs.next()) {
                int id_catatan = rs.getInt("id_catatan");
                String judul = rs.getString("judul_catatan");
                String isi = rs.getString("isi");
                String tgl = rs.getString("tgl_pembuatan");
                byte[] lampiran_gambar = rs.getBytes("lampiran_gambar");
                String pengingat = rs.getString("pengingat");
                String nama_kategori = rs.getString("nama_kategori");

                modelCatatan item = new modelCatatan(id_catatan, judul, isi, tgl, lampiran_gambar, pengingat, nama_kategori);
                addItemCatatan(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi Kesalahan Dalam Mencari Catatan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }            
    
    private void filterNotes(int id_kategori) {
        String query = "SELECT catatan.*, kategori.* FROM catatan join kategori on kategori.id_kategori = catatan.id_kategori WHERE catatan.id_kategori = '" + id_kategori + "'";

        try (Statement stmt = koneksi.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Clear previous search results
            panelItems.removeAll();
            panelItems.repaint();
            panelItems.revalidate();

            // Loop through the result set and add items to the panel
            while (rs.next()) {
                int id_catatan = rs.getInt("id_catatan");
                String judul = rs.getString("judul_catatan");
                String isi = rs.getString("isi");
                String tgl = rs.getString("tgl_pembuatan");
                byte[] lampiran_gambar = rs.getBytes("lampiran_gambar");
                String pengingat = rs.getString("pengingat");
                String nama_kategori = rs.getString("nama_kategori");

                modelCatatan item = new modelCatatan(id_catatan, judul, isi, tgl, lampiran_gambar, pengingat, nama_kategori);
                addItemCatatan(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi Kesalahan Dalam Filter Catatan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dateChooser = new com.raven.datechooser.DateChooser();
        timePicker = new com.raven.swing.TimePicker();
        panelUtama = new javax.swing.JPanel();
        ScrPanelForm = new javax.swing.JScrollPane();
        PanelForm = new javax.swing.JPanel();
        ScrTxtArea = new javax.swing.JScrollPane();
        isiTxtArea = new javax.swing.JTextArea();
        lbl_gambar = new javax.swing.JLabel();
        txt_gambar = new javax.swing.JTextField();
        btn_deleteImg = new javax.swing.JButton();
        search_txt = new javax.swing.JTextField();
        txt_date = new javax.swing.JTextField();
        txt_time = new javax.swing.JTextField();
        comBoxKategori = new javax.swing.JComboBox<>();
        lbl_tgl_pembuatan = new javax.swing.JLabel();
        judulTxt = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btn_setTimer = new javax.swing.JButton();
        btn_setPicture = new javax.swing.JButton();
        btn_save = new javax.swing.JButton();
        comBox_filter = new javax.swing.JComboBox<>();
        btn_filter = new javax.swing.JButton();
        btn_clearFilter = new javax.swing.JButton();
        ScrPanelCatatan = new javax.swing.JScrollPane();
        panelItems = new component.panelItems();
        jLabel1 = new javax.swing.JLabel();
        btn_search = new javax.swing.JButton();
        btn_add = new javax.swing.JButton();
        btn_back = new javax.swing.JButton();
        btn_kateg = new javax.swing.JButton();

        dateChooser.setForeground(new java.awt.Color(18, 101, 239));
        dateChooser.setDateFormat("yyyy-MM-dd");
        dateChooser.setTextRefernce(txt_date);

        timePicker.set24hourMode(true);
        timePicker.setDisplayText(txt_time);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelUtama.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        ScrPanelForm.setBackground(new java.awt.Color(0, 0, 0));
        ScrPanelForm.setForeground(new java.awt.Color(0, 0, 0));
        ScrPanelForm.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        ScrPanelForm.setOpaque(false);

        PanelForm.setOpaque(false);
        PanelForm.setPreferredSize(new java.awt.Dimension(798, 850));
        PanelForm.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        ScrTxtArea.setOpaque(false);
        ScrTxtArea.setBackground(new java.awt.Color(0,0,0,1));
        ScrTxtArea.setBackground(new java.awt.Color(51, 51, 51));
        ScrTxtArea.setBorder(null);
        ScrTxtArea.setForeground(new java.awt.Color(255, 0, 51));
        ScrTxtArea.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        ScrTxtArea.setPreferredSize(new java.awt.Dimension(282, 104));

        isiTxtArea.setOpaque(false);
        isiTxtArea.setBackground(new java.awt.Color(0,0,0,1));
        isiTxtArea.setBackground(new java.awt.Color(51, 51, 51));
        isiTxtArea.setColumns(20);
        isiTxtArea.setFont(new java.awt.Font("SansSerif", 0, 15)); // NOI18N
        isiTxtArea.setForeground(new java.awt.Color(255, 255, 255));
        isiTxtArea.setLineWrap(true);
        isiTxtArea.setRows(5);
        isiTxtArea.setText("Description");
        isiTxtArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                isiTxtAreaKeyTyped(evt);
            }
        });
        ScrTxtArea.setViewportView(isiTxtArea);

        PanelForm.add(ScrTxtArea, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 310, 810, 530));

        lbl_gambar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        PanelForm.add(lbl_gambar, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 30, 570, 260));
        PanelForm.add(txt_gambar, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 0, 330, -1));

        btn_deleteImg.setText("Delete Image");
        btn_deleteImg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_deleteImgActionPerformed(evt);
            }
        });
        PanelForm.add(btn_deleteImg, new org.netbeans.lib.awtextra.AbsoluteConstraints(599, 0, 100, -1));

        ScrPanelForm.setViewportView(PanelForm);

        panelUtama.add(ScrPanelForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 260, 830, 460));

        search_txt.setOpaque(false);
        search_txt.setBackground(new java.awt.Color(0,0,0,1));
        search_txt.setBackground(new java.awt.Color(0, 0, 0));
        search_txt.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        search_txt.setForeground(new java.awt.Color(255, 255, 255));
        search_txt.setBorder(null);
        search_txt.setOpaque(true);
        search_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_txtActionPerformed(evt);
            }
        });
        panelUtama.add(search_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 36, 630, -1));

        txt_date.setBackground(new java.awt.Color(32, 32, 32));
        txt_date.setForeground(new java.awt.Color(255, 255, 255));
        txt_date.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_date.setBorder(null);
        panelUtama.add(txt_date, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 200, 70, 30));

        txt_time.setEditable(false);
        txt_time.setBackground(new java.awt.Color(32, 32, 32));
        txt_time.setForeground(new java.awt.Color(255, 255, 255));
        txt_time.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_time.setBorder(null);
        panelUtama.add(txt_time, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 200, 60, 30));

        comBoxKategori.setBackground(new java.awt.Color(51, 51, 51));
        comBoxKategori.setForeground(new java.awt.Color(255, 255, 255));
        comBoxKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comBoxKategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comBoxKategoriActionPerformed(evt);
            }
        });
        panelUtama.add(comBoxKategori, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 200, 190, 30));

        lbl_tgl_pembuatan.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lbl_tgl_pembuatan.setForeground(new java.awt.Color(255, 255, 255));
        panelUtama.add(lbl_tgl_pembuatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 200, 150, 30));

        judulTxt.setBackground(new java.awt.Color(22, 22, 22));
        judulTxt.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        judulTxt.setForeground(new java.awt.Color(255, 255, 255));
        judulTxt.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        judulTxt.setBorder(null);
        judulTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                judulTxtActionPerformed(evt);
            }
        });
        panelUtama.add(judulTxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 100, 790, 50));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/aset/save_icons (2).png"))); // NOI18N
        panelUtama.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 160, -1, -1));

        btn_setTimer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_setTimerActionPerformed(evt);
            }
        });
        panelUtama.add(btn_setTimer, new org.netbeans.lib.awtextra.AbsoluteConstraints(1090, 200, 30, 30));

        btn_setPicture.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_setPictureActionPerformed(evt);
            }
        });
        panelUtama.add(btn_setPicture, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 200, 30, 30));

        btn_save.setOpaque(false);
        btn_save.setContentAreaFilled(false);
        btn_save.setBackground(new java.awt.Color(0,0,0,1));
        btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_saveActionPerformed(evt);
            }
        });
        panelUtama.add(btn_save, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 190, 40, 40));

        comBox_filter.setBackground(new java.awt.Color(51, 51, 51));
        comBox_filter.setForeground(new java.awt.Color(255, 255, 255));
        comBox_filter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        panelUtama.add(comBox_filter, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 130, 240, 30));

        btn_filter.setBackground(new java.awt.Color(51, 51, 51));
        btn_filter.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btn_filter.setForeground(new java.awt.Color(255, 255, 255));
        btn_filter.setText("Filter");
        btn_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_filterActionPerformed(evt);
            }
        });
        panelUtama.add(btn_filter, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 130, -1, 30));

        btn_clearFilter.setBackground(new java.awt.Color(51, 51, 51));
        btn_clearFilter.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btn_clearFilter.setForeground(new java.awt.Color(255, 255, 255));
        btn_clearFilter.setText("Clear");
        btn_clearFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_clearFilterActionPerformed(evt);
            }
        });
        panelUtama.add(btn_clearFilter, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 130, 60, 30));

        ScrPanelCatatan.setBackground(new java.awt.Color(36, 36, 36));
        ScrPanelCatatan.setBorder(null);
        ScrPanelCatatan.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        ScrPanelCatatan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        ScrPanelCatatan.setOpaque(false);
        ScrPanelCatatan.setPreferredSize(new java.awt.Dimension(400, 1500));

        panelItems.setBackground(new java.awt.Color(0, 0, 0));
        panelItems.setPreferredSize(new java.awt.Dimension(400, 1588));
        ScrPanelCatatan.setViewportView(panelItems);

        panelUtama.add(ScrPanelCatatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 420, 520));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/aset/Main (1).png"))); // NOI18N
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        panelUtama.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 730));

        btn_search.setOpaque(false);
        btn_search.setContentAreaFilled(false);
        btn_search.setBackground(new java.awt.Color(0,0,0,1));
        btn_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_searchActionPerformed(evt);
            }
        });
        panelUtama.add(btn_search, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 30, 90, 30));

        btn_add.setOpaque(false);
        btn_add.setContentAreaFilled(false);
        btn_add.setBackground(new java.awt.Color(0,0,0,1));
        btn_add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_addActionPerformed(evt);
            }
        });
        panelUtama.add(btn_add, new org.netbeans.lib.awtextra.AbsoluteConstraints(1170, 30, 40, 40));

        btn_back.setOpaque(false);
        btn_back.setContentAreaFilled(false);
        btn_back.setBackground(new java.awt.Color(0,0,0,1));
        btn_back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_backActionPerformed(evt);
            }
        });
        panelUtama.add(btn_back, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 40, 40));

        btn_kateg.setOpaque(false);
        btn_kateg.setContentAreaFilled(false);
        btn_kateg.setBackground(new java.awt.Color(0,0,0,1));
        btn_kateg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_kategActionPerformed(evt);
            }
        });
        panelUtama.add(btn_kateg, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 40, 40, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelUtama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelUtama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_saveActionPerformed
        String nama_kategori = (String) comBoxKategori.getSelectedItem();
        String judul = judulTxt.getText();
        String isi = isiTxtArea.getText();
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatTgl = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatWaktu = DateTimeFormatter.ofPattern("HH:mm:ss");
        String tggl = date.format(formatTgl);
        String waktu = time.format(formatWaktu);
        String tgl_pengingat = txt_date.getText();
        String waktu_pengingat = txt_time.getText();
        byte[] imageData = null;

        String path_gambar = txt_gambar.getText();
        if(path_gambar != null && !path_gambar.isEmpty()){
            try {
                File imageFile = new File(path_gambar);
                FileInputStream fis = new FileInputStream(imageFile);
                imageData = new byte[(int) imageFile.length()];
                fis.read(imageData);
                fis.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (judul.length() > 40) {
            JOptionPane.showMessageDialog(this, "Judul maksimal 40 karakter.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Hentikan proses penyimpanan jika judul melebihi 40 karakter
        }

        String query_kategori = "select id_kategori from kategori where nama_kategori = ?";
        try (PreparedStatement stmtKategori = koneksi.prepareStatement(query_kategori)) {
            stmtKategori.setString(1, nama_kategori);
            ResultSet rs = stmtKategori.executeQuery();
            int id_kategori = 0;
            if (rs.next()) {
                id_kategori = rs.getInt("id_kategori");
            }

            if (selectedItem == null) {
                String query_tambah = "insert into catatan (judul_catatan, isi, tgl_pembuatan, lampiran_gambar, pengingat, id_kategori) values (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = koneksi.prepareStatement(query_tambah)) {
                    stmt.setString(1, judul);
                    stmt.setString(2, isi);
                    stmt.setString(3, tggl + " " + waktu);
                    stmt.setBytes(4, imageData);
                    stmt.setString(5, tgl_pengingat + " " + waktu_pengingat);
                    stmt.setInt(6, id_kategori);
                    int rows = stmt.executeUpdate();
                    if(rows > 0) {
                        JOptionPane.showMessageDialog(null,"Data berhasil masuk");
                    } else {
                        JOptionPane.showMessageDialog(null,"Data gagal masuk");
                    }
                } catch (SQLException e) {
                    System.err.println("Terjadi Kesalahan Dalam Penambahan Catatan: " + e.getMessage());
                }
            } else {
                String query_update = "update catatan set judul_catatan = ?, isi = ?, tgl_pembuatan = ?, lampiran_gambar = ?, pengingat = ?, id_kategori = ? where id_catatan = ?";
                try (PreparedStatement stmt = koneksi.prepareStatement(query_update)) {
                    stmt.setString(1, judul);
                    stmt.setString(2, isi);
                    stmt.setString(3, tggl + " " + waktu);
                    stmt.setBytes(4, imageData);
                    stmt.setString(5, tgl_pengingat + " " + waktu_pengingat);
                    stmt.setInt(6, id_kategori);
                    stmt.setInt(7, selectedItem.getId_catatan());
                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(null, "Data berhasil diperbarui");
                    } else {
                        JOptionPane.showMessageDialog(null, "Data gagal diperbarui");
                    }
                } catch (SQLException e) {
                    System.err.println("Terjadi Kesalahan Dalam Pembaruan Catatan: " + e.getMessage());
                }
            }
        } catch (SQLException e){
            System.err.println("Terjadi Kesalahan Dalam Pengambilan ID Kategori: " + e.getMessage());
        }
        
        panelItems.removeAll();
        panelItems.repaint();
        panelItems.revalidate();
        tampilItem();
    }//GEN-LAST:event_btn_saveActionPerformed

    private void judulTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_judulTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_judulTxtActionPerformed

    private void search_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_search_txtActionPerformed

    private void btn_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_addActionPerformed
        judulTxt.setText("");
        isiTxtArea.setText("Description");
        selectedItem = null;
        
        panelItems.removeAll();
        panelItems.repaint();
        panelItems.revalidate();
        tampilItem();
    }//GEN-LAST:event_btn_addActionPerformed

    private void btn_backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_backActionPerformed
        // TODO add your handling code here:
        this.dispose();
        welcome image = new welcome();
        image.setVisible(true);        
    }//GEN-LAST:event_btn_backActionPerformed

    private void btn_kategActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_kategActionPerformed
        // TODO add your handling code here:
        this.dispose();
        kategori Kategori = new kategori();
        Kategori.setVisible(true);
        
    }//GEN-LAST:event_btn_kategActionPerformed

    private void isiTxtAreaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_isiTxtAreaKeyTyped
        if (isiTxtArea.getText().equals("Description")) {
            isiTxtArea.setText("");
        }else if(isiTxtArea.getText().isEmpty()) {
            isiTxtArea.setText("Description");
        }
    }//GEN-LAST:event_isiTxtAreaKeyTyped

    private void btn_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_searchActionPerformed
        String keyword = search_txt.getText().trim();
        if (keyword.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Please enter a keyword to search.", "Warning", JOptionPane.WARNING_MESSAGE);
//            return;
        panelItems.removeAll();
        panelItems.repaint();
        panelItems.revalidate();
        tampilItem();
        } else {            
            searchNotes(keyword);
        }
    }//GEN-LAST:event_btn_searchActionPerformed

    private void comBoxKategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comBoxKategoriActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comBoxKategoriActionPerformed

    private void btn_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_filterActionPerformed
        String nama_kategori = (String) comBox_filter.getSelectedItem();
        if(!nama_kategori.equals("All")) {
            String queri_kategori = "select id_kategori from kategori where nama_kategori = '" + nama_kategori + "'";         
            int idKategori = 0;
            try {            
                Statement stmt = koneksi.createStatement();
                ResultSet rs = stmt.executeQuery(queri_kategori);            
                if (rs.next()) {
                    idKategori = rs.getInt("id_kategori");
                }    
                filterNotes(idKategori);

            } catch (SQLException e) {
                System.err.println("Terjadi Kesalahan Dalam Pengambilan Kategori Untuk Filter: " + e.getMessage());
            }
        } else {
            panelItems.removeAll();
            tampilItem();
            panelItems.revalidate();
            panelItems.repaint();                        
        }
    }//GEN-LAST:event_btn_filterActionPerformed

    private void btn_clearFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_clearFilterActionPerformed
        panelItems.removeAll();
        panelItems.repaint();
        panelItems.revalidate();
        tampilItem();
//        if() {
            comBox_filter.setSelectedItem("All");
//        }
    }//GEN-LAST:event_btn_clearFilterActionPerformed

    private void btn_setTimerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_setTimerActionPerformed
//        dateChooser.showPopup();
        timePicker.showPopup(this, 100, 100);
//        timePicker.set24hourMode(true);
    }//GEN-LAST:event_btn_setTimerActionPerformed

    private void btn_setPictureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_setPictureActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("IMAGE FILES", "jpg", "gif", "png", "jpeg");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(null);  // Menggunakan showOpenDialog untuk memilih file
        if(result == JFileChooser.APPROVE_OPTION){
            File selectedFile = fileChooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            txt_gambar.setText(path);
                    
            try {
                byte[] img = Files.readAllBytes(selectedFile.toPath());
                ImageIcon imageicon = new ImageIcon(img);
                
                int labelWidth = 570;
                int labelHeight = 260;
                
                int imageWidth = imageicon.getIconWidth();
                int imageHeight = imageicon.getIconHeight();
                
                double scaleX = (double) labelWidth / (double) imageWidth;
                double scaleY = (double) labelHeight / (double) imageHeight;
                double scale = Math.min(scaleX, scaleY);
                
                Image scaledImage = imageicon.getImage().getScaledInstance((int) (scale * imageWidth), (int) (scale * imageHeight), Image.SCALE_SMOOTH);
                
                lbl_gambar.setIcon(new ImageIcon(scaledImage));
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_btn_setPictureActionPerformed

    private void btn_deleteImgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_deleteImgActionPerformed
        if (selectedItem != null) {
            try {
                String query_update = "UPDATE catatan SET lampiran_gambar = NULL WHERE id_catatan = ?";
                try (PreparedStatement pstmt = koneksi.prepareStatement(query_update)) {
                    pstmt.setInt(1, selectedItem.getId_catatan());
                    int rows = pstmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(null, "Gambar berhasil dihapus");
                        lbl_gambar.setIcon(null); // Menghapus gambar dari label
                        selectedItem.setLampiran_gambar(null); // Mengupdate model
                    } else {
                        JOptionPane.showMessageDialog(null, "Gambar gagal dihapus");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal menghapus gambar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Tidak ada catatan yang dipilih.", "Error", JOptionPane.ERROR_MESSAGE);
            
        }
        txt_gambar.setText("");
    }//GEN-LAST:event_btn_deleteImgActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FlatDarkLaf.setup();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelForm;
    private javax.swing.JScrollPane ScrPanelCatatan;
    private javax.swing.JScrollPane ScrPanelForm;
    private javax.swing.JScrollPane ScrTxtArea;
    private javax.swing.JButton btn_add;
    private javax.swing.JButton btn_back;
    private javax.swing.JButton btn_clearFilter;
    private javax.swing.JButton btn_deleteImg;
    private javax.swing.JButton btn_filter;
    private javax.swing.JButton btn_kateg;
    private javax.swing.JButton btn_save;
    private javax.swing.JButton btn_search;
    private javax.swing.JButton btn_setPicture;
    private javax.swing.JButton btn_setTimer;
    private javax.swing.JComboBox<String> comBoxKategori;
    private javax.swing.JComboBox<String> comBox_filter;
    private com.raven.datechooser.DateChooser dateChooser;
    private javax.swing.JTextArea isiTxtArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField judulTxt;
    private javax.swing.JLabel lbl_gambar;
    private javax.swing.JLabel lbl_tgl_pembuatan;
    public component.panelItems panelItems;
    private javax.swing.JPanel panelUtama;
    private javax.swing.JTextField search_txt;
    private com.raven.swing.TimePicker timePicker;
    private javax.swing.JTextField txt_date;
    private javax.swing.JTextField txt_gambar;
    private javax.swing.JTextField txt_time;
    // End of variables declaration//GEN-END:variables
}

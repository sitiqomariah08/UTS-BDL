/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package uts_bdl_ria;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Careen Emilza
 */
public class detail_penjualan extends javax.swing.JFrame {

    private final ArrayList<String[]> dataList = new ArrayList<>();
    PreparedStatement ps;
    Connection conn;
    Statement stmt;
    String driver = "org.postgresql.Driver";
    String koneksi = "jdbc:postgresql://localhost:5432/TOKO_BUKU";
    String user = "postgres";
    String password = "nidzom15";
    private String tanggal;
    private Object connection;
    private String nama;
    private int harga;

    /**
     * Creates new form detail_penjualan
     */
    public detail_penjualan() {
        initComponents();
        tableModel();
//        loadData();
        txtIDpenjualan.requestFocus();
        tableModel2();
        loadData2();
    }

    private void hitungTotalHarga() {
        int total_harga = 0;
        int jumlahBaris = tblKeranjang.getRowCount();

        for (int i = 0; i < jumlahBaris; i++) {

            if (tblKeranjang.getValueAt(i, 1) != null) {
                try {
                    int harga = Integer.parseInt(tblKeranjang.getValueAt(i, 5).toString());
                    total_harga += harga;
                } catch (NumberFormatException e) {
                    System.out.println("Format harga salah di baris: " + i);
                }
            }
        }
        txtTotalHarga.setText(String.valueOf(total_harga));
    }

    public void loadData() {
        DefaultTableModel model = (DefaultTableModel) tblKeranjang.getModel();
        model.insertRow(0, new Object[]{
            txtIDpenjualan.getText(),
            txtIDpelanggan.getText(),
            txtKode.getText(),
            txtJudul.getText(),
            txtJumlah.getText(),
            txtHarga.getText(),
            txtTanggal.getText()
        });
        loadTanggal();
    }
    
    public void loadTanggal() {
        Date date = new Date();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        txtTanggal.setText(s.format(date));
    }

    public void tableModel() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Penjualan");
        model.addColumn("ID Pelanggan");
        model.addColumn("Kode Buku");
        model.addColumn("Judul Buku");
        model.addColumn("Jumlah");
        model.addColumn("Harga");
        model.addColumn("Tanggal Penjualan");
        
        tblKeranjang.setModel(model);
    }

    public void tableModel2() {
        DefaultTableModel model = new DefaultTableModel();


        model.addColumn("ID Penjualan");
        model.addColumn("ID Pelanggan");
        model.addColumn("Tanggal");
        model.addColumn("Total Harga");
        model.addColumn("Kode Buku");
        model.addColumn("Jumlah Beli");
        tblPembelian.setModel(model);
    }

    public void loadData2() {
        DefaultTableModel model = (DefaultTableModel) tblPembelian.getModel();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(koneksi, user, password);
            String sql = "SELECT p.id_penjualan, p.id_pelanggan, p.tanggal, p.total_harga, dp.kode_buku, dp.jumlah "
                    + "FROM penjualan p "
                    + "JOIN detail_penjualan dp ON p.id_penjualan = dp.id_penjualan";

            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            model.setRowCount(0);  

            while (rs.next()) {
                Object[] row = {
                    rs.getString("id_penjualan"),
                    rs.getString("id_pelanggan"),
                    rs.getDate("tanggal"),
                    rs.getInt("total_harga"),
                    rs.getString("kode_buku"),
                    rs.getInt("jumlah")
                };
                model.addRow(row);  
            }
            
            tblPembelian.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();  
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadDataFromKeranjang() {
        
        DefaultTableModel model = (DefaultTableModel) tblPembelian.getModel();
        model.setRowCount(0); 

       
        DefaultTableModel keranjangModel = (DefaultTableModel) tblKeranjang.getModel();
        int jumlahBaris = keranjangModel.getRowCount();
        for (int i = 0; i < jumlahBaris; i++) {
            Object[] row = new Object[]{
                txtIDpenjualan.getText(), 
                txtIDpelanggan.getText(), 
                txtTanggal.getText(),
                txtTotalHarga.getText(),
                keranjangModel.getValueAt(i, 2), 
                1 
            };
            model.addRow(row); 
        }

        tblPembelian.setModel(model); 
    }

    public void clearTextField() {
        txtKode.setText("");
        txtJudul.setText("");
        txtHarga.setText("");
        txtJumlah.setText("");
    }

    public void clearTextField2() {
        txtKode.setText("");
        txtJudul.setText("");
        txtHarga.setText("");
        txtIDpenjualan.setText("");
        txtIDpelanggan.setText("");
        txtNama.setText("");
        txtJumlah.setText("");
    }

    public void keranjang() {
        loadData();
        clearTextField();
        hitungTotalHarga();
    }

    public void masukDatabase() {
        String id_penjualan = txtIDpenjualan.getText();
        String id_pelanggan = txtIDpelanggan.getText();
        String tanggal = txtTanggal.getText();
        String total_harga = txtTotalHarga.getText();

        try {
            conn = DriverManager.getConnection(koneksi, user, password);
            conn.setAutoCommit(false); // Mulai transaksi

          
            DefaultTableModel model = (DefaultTableModel) tblKeranjang.getModel();
            int jumlahBaris = model.getRowCount();

            
            String sqlInsert = "INSERT INTO penjualan (id_penjualan, id_pelanggan,  total_harga) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(sqlInsert);
            ps.setInt(1, Integer.parseInt(id_penjualan)); 
            ps.setInt(2, Integer.parseInt(id_pelanggan)); 
            ps.setInt(3, Integer.parseInt(total_harga)); 
            ps.executeUpdate();
            ps.close();

            for (int i = 0; i < jumlahBaris; i++) {
                int kode_buku = Integer.parseInt(model.getValueAt(i, 2).toString()); 
                String jumlah = model.getValueAt(i, 4).toString();

                
                String sqlInsertDetail = "INSERT INTO detail_penjualan (id_penjualan, kode_buku, jumlah) VALUES (?, ?, ?)";
                ps = conn.prepareStatement(sqlInsertDetail);
                ps.setInt(1, Integer.parseInt(id_penjualan));
                ps.setInt(2, kode_buku); 
                ps.setInt(3, Integer.parseInt(jumlah)); 
                ps.executeUpdate();
                ps.close();

                String sqlUpdateStok = "UPDATE buku SET stok = stok - ? WHERE kode_buku = ?";
                ps = conn.prepareStatement(sqlUpdateStok);
                ps.setInt(1, Integer.parseInt(jumlah)); 
                ps.setInt(2, kode_buku); 
                ps.executeUpdate();
                ps.close();
            }

            conn.commit(); 
            loadData2(); 
            JOptionPane.showMessageDialog(null, "Transaksi berhasil ditambahkan dan stok dikurangi!");

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); 
                }
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Transaksi gagal! Error: " + e.getMessage());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void kosong() {
        DefaultTableModel model = (DefaultTableModel) tblKeranjang.getModel();

        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
    }

    private void lookupBuku() {
        try {
            String sql = "SELECT * FROM buku WHERE kode_buku = ?";
            conn = DriverManager.getConnection(koneksi, user, password);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(txtKode.getText()));

            ResultSet res = ps.executeQuery();
            while (res.next()) {
                String judul = res.getString("judul");
                harga = res.getInt("harga");
                txtJudul.setText(judul);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void lookupPelanggan() {
        try {
            conn = DriverManager.getConnection(koneksi, user, password);
            String sql = "SELECT nama FROM pelanggan WHERE id_pelanggan = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(txtIDpelanggan.getText()));

            ResultSet res = ps.executeQuery();
            while (res.next()) {
                nama = res.getString("nama");
                txtNama.setText(nama);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void lookupHarga() {
        try {
            conn = DriverManager.getConnection(koneksi, user, password);
            String sql = "SELECT * FROM buku WHERE kode_buku = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setLong(1, Long.parseLong(txtKode.getText()));

            ResultSet res = ps.executeQuery();
            while (res.next()) {
                String judul = res.getString("judul");
                int harga = res.getInt("harga");
                txtJudul.setText(judul);
                txtHarga.setText(String.valueOf(harga));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtIDpenjualan = new javax.swing.JTextField();
        txtIDpelanggan = new javax.swing.JTextField();
        txtNama = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtTanggal = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtKode = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtJudul = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKeranjang = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        txtTotalHarga = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        btnSimpan2 = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        txtHarga = new javax.swing.JTextField();
        btnDelete = new javax.swing.JButton();
        txtJumlah = new javax.swing.JTextField();
        btnClear = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblPembelian = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Pembelian Buku");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("ID Penjualan");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("ID Pelanggan");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Nama");

        txtIDpenjualan.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        txtIDpelanggan.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtIDpelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIDpelangganActionPerformed(evt);
            }
        });

        txtNama.setEditable(false);
        txtNama.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Tanggal");

        txtTanggal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtTanggal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTanggalActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Kode Buku");

        txtKode.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtKode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKodeActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("Judul Buku");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setText("Jumlah");

        txtJudul.setEditable(false);
        txtJudul.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        tblKeranjang.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        tblKeranjang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblKeranjang.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(tblKeranjang);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setText("Total Harga");

        txtTotalHarga.setEditable(false);
        txtTotalHarga.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtTotalHarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalHargaActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setText("Pembelian");

        btnSimpan2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnSimpan2.setText("Simpan Pembelian");
        btnSimpan2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpan2ActionPerformed(evt);
            }
        });

        btnBatal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnBatal.setText("Batal Pembelian");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        btnSimpan.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnSimpan.setText("Simpan di Keranjang");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton5.setText("Back");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setText("Harga ");

        txtHarga.setEditable(false);
        txtHarga.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtHarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHargaActionPerformed(evt);
            }
        });

        btnDelete.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        txtJumlah.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtJumlah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtJumlahActionPerformed(evt);
            }
        });

        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        tblPembelian.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tblPembelian);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6)
                            .addComponent(jLabel2)
                            .addComponent(jLabel11)
                            .addComponent(jLabel10))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 975, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 976, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtKode, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtIDpenjualan, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtIDpelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnSimpan))
                                        .addGap(33, 33, 33)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGap(76, 76, 76)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(layout.createSequentialGroup()
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                .addComponent(txtHarga, javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(txtJudul, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE))
                                                            .addGap(18, 18, 18)
                                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                    .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                    .addGap(18, 18, 18)
                                                                    .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGroup(layout.createSequentialGroup()
                                                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                    .addComponent(jLabel5)
                                                    .addGap(18, 18, 18)
                                                    .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addComponent(jLabel9)
                                            .addComponent(jLabel7)))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtTotalHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(1, 1, 1)
                                        .addComponent(btnSimpan2)))))
                        .addGap(0, 27, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jButton5))
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIDpenjualan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5)
                    .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtIDpelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtKode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtJudul, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan)
                    .addComponent(jLabel9)
                    .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete)
                    .addComponent(btnClear))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtTotalHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSimpan2)
                            .addComponent(btnBatal)))
                    .addComponent(jLabel10))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtTanggalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTanggalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTanggalActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBatalActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        dashboard p = new dashboard();
        p.setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        loadTanggal();
        keranjang();
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnSimpan2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpan2ActionPerformed

        loadDataFromKeranjang();

        masukDatabase();

        loadData2(); 
    }//GEN-LAST:event_btnSimpan2ActionPerformed

    private void txtKodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKodeActionPerformed
        lookupBuku();
    }//GEN-LAST:event_txtKodeActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        DefaultTableModel model = (DefaultTableModel) tblKeranjang.getModel();
        int row = tblKeranjang.getSelectedRow();
        model.removeRow(row);
        hitungTotalHarga();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void txtIDpelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIDpelangganActionPerformed
        lookupPelanggan();
    }//GEN-LAST:event_txtIDpelangganActionPerformed

    private void txtHargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHargaActionPerformed

    private void txtJumlahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtJumlahActionPerformed
        try {
            int jumlah = Integer.parseInt(txtJumlah.getText().trim());

            int hitung = harga * jumlah;

            txtHarga.setText(String.valueOf(hitung));
        } catch (NumberFormatException e) {
            txtHarga.setText("Masukkan jumlah yang valid");
        }
    }//GEN-LAST:event_txtJumlahActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        txtIDpenjualan.setEditable(false);
        txtTanggal.setEditable(false);
        txtJudul.setEditable(false);
        txtNama.setEditable(false);
    }//GEN-LAST:event_formComponentShown

    private void txtTotalHargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalHargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalHargaActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearTextField2();
    }//GEN-LAST:event_btnClearActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(detail_penjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(detail_penjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(detail_penjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(detail_penjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new detail_penjualan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnSimpan2;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tblKeranjang;
    private javax.swing.JTable tblPembelian;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtIDpelanggan;
    private javax.swing.JTextField txtIDpenjualan;
    private javax.swing.JTextField txtJudul;
    private javax.swing.JTextField txtJumlah;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtTanggal;
    private javax.swing.JTextField txtTotalHarga;
    // End of variables declaration//GEN-END:variables
}

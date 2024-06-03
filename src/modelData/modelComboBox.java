package modelData;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import javax.swing.JComboBox;
import remindnote_app.koneksi_database;

public class modelComboBox {
    private HashMap<Integer, String> items;

    public modelComboBox() {
        items = new HashMap<>();
    }

    public void loadItems(JComboBox<String> comboBox) {
        koneksi_database koneksiDB = new koneksi_database();
        Connection koneksi = koneksiDB.getConnection();
        String query = "SELECT * FROM kategori";
        try (PreparedStatement ps = koneksi.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id_catatan");
                String name = rs.getString("nama_kategori");
                items.put(id, name);
                comboBox.addItem(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getSelectedId(String selectedItem) {
        for (HashMap.Entry<Integer, String> entry : items.entrySet()) {
            if (entry.getValue().equals(selectedItem)) {
                return entry.getKey();
            }
        }
        return -1; // or throw an exception if not found
    }
}


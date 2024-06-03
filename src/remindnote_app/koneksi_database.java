
package remindnote_app;

//import com.mysql.jdbc.Connection; // dari library hasil download
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class koneksi_database {
    private final String url = "jdbc:mysql://localhost:3306/catatan_jadwal?useSSL=false&serverTimezone=UTC";
    private final String username = "root";
    private final String password = "";
    private Connection koneksi = null;

    public Connection getConnection() {
        try {
            // Tidak perlu memanggil Class.forName("com.mysql.cj.jdbc.Driver");
            koneksi = DriverManager.getConnection(url, username, password);
            System.out.println("Koneksi ke Database Berhasil");
        } catch (SQLException e) {
            System.err.println("Koneksi ke Database Error: " + e.getMessage());
        }
        return koneksi;
    }
    
    public static void main(String[] args) {
        koneksi_database db = new koneksi_database();
        Connection conn = db.getConnection();
        
        // Pastikan untuk menutup koneksi jika tidak lagi digunakan
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error saat menutup koneksi: " + e.getMessage());
            }
        }
    }
}


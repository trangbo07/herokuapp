package utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;

public class DBConnection {
    private static DBConnection instance = new DBConnection();
    private Connection connection;

    public static DBConnection getInstance() {
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                String user = "sa";
                String password = "123";
                String url = "jdbc:sqlserver://localhost:1433;databaseName=swp391;encrypt=true;trustServerCertificate=true";
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (Exception e) {
            System.out.println("Error connecting to database: " + e.getMessage());
            e.printStackTrace();
            connection = null;
        }
        return connection;
    }

    private DBConnection() {
        getConnection();
    }

//    // Thêm hàm test connection
//    public void testConnection() {
//        try {
//            Connection conn = getConnection();
//            if (conn != null && !conn.isClosed()) {
//                // Lấy thông tin metadata của database
//                DatabaseMetaData metaData = conn.getMetaData();
//
//                System.out.println("**** Database Connection Test ****");
//                System.out.println("✓ Kết nối database thành công!");
//                System.out.println("Database Info:");
//                System.out.println("- Database Name: " + metaData.getDatabaseProductName());
//                System.out.println("- Database Version: " + metaData.getDatabaseProductVersion());
//                System.out.println("- Driver Name: " + metaData.getDriverName());
//                System.out.println("- Driver Version: " + metaData.getDriverVersion());
//            } else {
//                System.out.println("✗ Không thể kết nối đến database!");
//            }
//        } catch (Exception e) {
//            System.out.println("✗ Lỗi kết nối database:");
//            System.out.println("Error: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    // Main method để chạy test
//    public static void main(String[] args) {
//        DBConnection.getInstance().testConnection();
//    }
}
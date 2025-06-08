package dao;

import model.AccountPatient;
import java.sql.*;
import java.util.ArrayList;

public class AccountPatientDAO {
    public static AccountPatient getAccountPatient(String username, String password) {
        DBContext db = DBContext.getInstance();
        AccountPatient patient = null;

        try {
            String sql = """
                         SELECT * FROM AccountPatient WHERE username = ? AND password = ?
                         """;
            PreparedStatement statement = db.getConnection().prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                patient = new AccountPatient(
                        rs.getInt("account_patient_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("status")
                );
            }
        } catch (Exception e) {
            return null;
        }

        return patient;
    }
    public static boolean registerPatient(String username ,String email, String password, String status) {
        DBContext db = DBContext.getInstance();

        try {
            // Kiểm tra email đã tồn tại chưa
            String checkSql = "SELECT COUNT(*) FROM AccountPatient WHERE email = ?";
            PreparedStatement checkStmt = db.getConnection().prepareStatement(checkSql);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false; // Email đã tồn tại
            }

            // Chưa tồn tại → thêm mới
            String sql = "INSERT INTO AccountPatient(username, password, email, status) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = db.getConnection().prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setString(4, status);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Lỗi khi đăng ký: " + e.getClass().getName() + " - " + e.getMessage());
            return false;
        }
    }
}


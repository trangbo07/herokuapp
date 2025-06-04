package dao;

import model.AccountStaff;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AccountStaffDAO {
    private DBContext db = DBContext.getInstance();
    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    public AccountStaff checkLogin(String username, String password) {
        String query = "SELECT * FROM AccountStaff WHERE username = ? AND password = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AccountStaff accountStaff = new AccountStaff();
                    accountStaff.setAccount_staff_id(rs.getInt("account_staff_id"));
                    accountStaff.setUsername(rs.getString("username"));
                    accountStaff.setPassword(rs.getString("password"));
                    accountStaff.setRole(rs.getString("role"));
                    accountStaff.setEmail(rs.getString("email"));
                    accountStaff.setStatus(rs.getString("status"));
                    accountStaff.setImg(rs.getString("img"));
                    return accountStaff;
                }
            }
        } catch (Exception e) {
            System.out.println("Error in checkLogin: " + e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        AccountStaffDAO dao = new AccountStaffDAO();
        AccountStaff accountStaff = dao.checkLogin("doctor1", "docpass");
        System.out.println(accountStaff.toString());
    }
}

package dao;

import model.AccountPharmacist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AccountPharmacistDAO {
    private DBContext db = DBContext.getInstance();
    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    public AccountPharmacist checkLogin(String username, String password) {
        String query = "SELECT * FROM AccountPharmacist WHERE username = ? AND password = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AccountPharmacist accountPharmacist = new AccountPharmacist();
                    accountPharmacist.setAccount_pharmacist_id(rs.getInt("account_pharmacist_id"));
                    accountPharmacist.setUsername(rs.getString("username"));
                    accountPharmacist.setPassword(rs.getString("password"));
                    accountPharmacist.setEmail(rs.getString("email"));
                    accountPharmacist.setStatus(rs.getString("status"));
                    accountPharmacist.setImg(rs.getString("img"));
                    return accountPharmacist;
                }
            }
        } catch (Exception e) {
            System.out.println("Error in checkLogin: " + e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        AccountPharmacistDAO dao = new AccountPharmacistDAO();
        AccountPharmacist AccountPatient = dao.checkLogin("pharma1", "pharmapass1");
        System.out.println(AccountPatient.toString());
    }
}

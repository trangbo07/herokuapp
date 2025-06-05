package dao;

import model.AccountPharmacist;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountPharmacistDAO {
    private DBContext db = DBContext.getInstance();

    public AccountPharmacist checkLogin(Connection conn, String username, String password) throws SQLException {
        String query = "SELECT * FROM AccountPharmacist WHERE username = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AccountPharmacist pharmacist = new AccountPharmacist();
                    pharmacist.setAccount_pharmacist_id(rs.getInt("account_pharmacist_id"));
                    pharmacist.setUsername(rs.getString("username"));
                    pharmacist.setPassword(rs.getString("password"));
                    pharmacist.setEmail(rs.getString("email"));
                    pharmacist.setStatus(rs.getString("status"));
                    pharmacist.setImg(rs.getString("img"));
                    return pharmacist;
                }
            }
        }
        return null;
    }
}

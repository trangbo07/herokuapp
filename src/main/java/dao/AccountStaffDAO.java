package dao;

import model.AccountStaff;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountStaffDAO {
    private DBContext db = DBContext.getInstance();

    public AccountStaff checkLogin(Connection conn, String username, String password) throws SQLException {
        String query = "SELECT * FROM AccountStaff WHERE username = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AccountStaff staff = new AccountStaff();
                    staff.setAccount_staff_id(rs.getInt("account_staff_id"));
                    staff.setUsername(rs.getString("username"));
                    staff.setPassword(rs.getString("password"));
                    staff.setRole(rs.getString("role"));
                    staff.setEmail(rs.getString("email"));
                    staff.setStatus(rs.getString("status"));
                    staff.setImg(rs.getString("img"));
                    return staff;
                }
            }
        }
        return null;
    }
}

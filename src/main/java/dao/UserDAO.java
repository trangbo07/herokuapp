
package dao;

import utils.DBConnection;
import model.User;
import java.sql.*;

public class UserDAO {
    private DBConnection db = DBConnection.getInstance();
    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    public User checkLogin(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try {
            conn = db.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                return user;
            }
        } catch (Exception e) {
            System.out.println("Error in checkLogin: " + e.getMessage());
        }
        return null;
    }
}
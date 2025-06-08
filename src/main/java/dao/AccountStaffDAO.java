package dao;

import model.AccountStaff;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AccountStaffDAO {
    public static AccountStaff getAccountStaff(String username, String password) {
        DBContext db = DBContext.getInstance();
        AccountStaff staff = null;

        try {
            String sql = """
                         SELECT * FROM AccountStaff WHERE username = ? AND password = ?
                         """;
            PreparedStatement statement = db.getConnection().prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                staff = new AccountStaff(
                        rs.getInt("account_staff_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getString("status"),
                        rs.getString("img")
                );
            }
        } catch (Exception e) {
            return null;
        }

        return staff;
    }
}

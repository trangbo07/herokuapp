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
}


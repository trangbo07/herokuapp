package dao;

import model.AccountPatient;
import java.sql.*;

public class AccountPatientDAO {
    private DBContext db = DBContext.getInstance();

    public AccountPatient checkLogin(Connection conn, String username, String password) throws SQLException {
        String query = "SELECT * FROM AccountPatient WHERE username = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AccountPatient patient = new AccountPatient();
                    patient.setAccount_patient_id(rs.getInt("account_patient_id"));
                    patient.setUsername(rs.getString("username"));
                    patient.setPassword(rs.getString("password"));
                    patient.setEmail(rs.getString("email"));
                    patient.setStatus(rs.getString("status"));
                    return patient;
                }
            }
        }
        return null;
    }

}

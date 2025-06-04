package dao;

import model.AccountPatient;
import java.sql.*;

public class AccountPatientDAO {
    private DBContext db = DBContext.getInstance();
    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    public AccountPatient checkLogin(String username, String password) {
        String query = "SELECT * FROM AccountPatient WHERE username = ? AND password = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AccountPatient AccountPatient = new AccountPatient();
                    AccountPatient.setAccount_patient_id(rs.getInt("account_patient_id"));
                    AccountPatient.setUsername(rs.getString("username"));
                    AccountPatient.setPassword(rs.getString("password"));
                    AccountPatient.setEmail(rs.getString("email"));
                    AccountPatient.setStatus(rs.getString("status"));                    return AccountPatient;
                }
            }
        } catch (Exception e) {
            System.out.println("Error in checkLogin: " + e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        AccountPatientDAO dao = new AccountPatientDAO();
        AccountPatient AccountPatient = dao.checkLogin("patient1", "pass123");
        System.out.println(AccountPatient.toString());
    }
}

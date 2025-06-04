package dao;

import model.AccountPatient;
import model.AccountPharmacist;
import model.AccountStaff;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Account {

    public Object checkLogin(String username, String password) {
        AccountStaffDAO dao1 = new AccountStaffDAO();
        AccountStaff accountStaff = dao1.checkLogin(username, password);
        AccountPharmacistDAO dao2 = new AccountPharmacistDAO();
        AccountPharmacist accountPharmacist = dao2.checkLogin(username, password);
        AccountPatientDAO dao3 = new AccountPatientDAO();
        AccountPatient accountPatient = dao3.checkLogin(username, password);

        if (accountStaff != null) {
            return accountStaff;
        } else if (accountPatient != null) {
            return accountPatient;
        } else if (accountPharmacist != null) {
            return accountPharmacist;
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        Account account = new Account();

        Object result = account.checkLogin("pharma1", "pharmapass1"); // test thử tài khoản

        if (result != null) {
            System.out.println("✅ Đăng nhập thành công: " + result.getClass().getSimpleName());
        } else {
            System.out.println("❌ Đăng nhập thất bại.");
        }
    }
}

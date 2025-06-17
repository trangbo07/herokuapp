package dao;

import model.AccountStaff;
import model.AccountPharmacist;
import model.AccountPatient;

public class AccountDAO {
    AccountStaffDAO accountStaffDAO = new AccountStaffDAO();
    AccountPharmacistDAO accountPharmacistDAO = new AccountPharmacistDAO();
    AccountPatientDAO accountPatientDAO = new AccountPatientDAO();

    public Object checkLogin(String username, String password) {
        AccountStaff staff = accountStaffDAO.checkLogin(username, password);
        if (staff != null) {
            return staff;
        }

        AccountPharmacist pharmacist = accountPharmacistDAO.checkLogin(username, password);
        if (pharmacist != null) {
            return pharmacist;
        }

        AccountPatient patient = accountPatientDAO.checkLogin(username, password);
        if (patient != null) {
            return patient;
        }

        return null;
    }



    public Object checkAccount(String username, String email) {
        AccountStaffDAO  accountStaffDAO = new AccountStaffDAO();
        AccountPharmacistDAO  accountPharmacistDAO = new AccountPharmacistDAO();
        AccountPatientDAO   accountPatientDAO = new AccountPatientDAO();

        // Check AccountStaff
        if (accountStaffDAO.checkAccountStaff(username, email)) {
            AccountStaff staff = AccountStaffDAO.checkLogin(username, email);
            if (staff != null) {
                return staff;
            }
        }

        // Check AccountPharmacist
        if (accountPharmacistDAO.checkAccountPharmacist(username, email)) {
            AccountPharmacist pharmacist = AccountPharmacistDAO.checkLogin(username, email);
            if (pharmacist != null) {
                return pharmacist;
            }
        }

        // Check AccountPatient
        AccountPatient patient = accountPatientDAO.checkLogin(username, email);
        if (patient != null) {
            return patient;
        }

        return null;
    }

    public Object checkAccount1(String email) {
        AccountStaffDAO  accountStaffDAO = new AccountStaffDAO();
        AccountPharmacistDAO  accountPharmacistDAO = new AccountPharmacistDAO();
        AccountPatientDAO   accountPatientDAO = new AccountPatientDAO();
        // 1. Kiểm tra trong AccountPatientDAO
        AccountPatient patient = accountPatientDAO.getAccountByEmailOrUsername(email);
        if (patient != null) {
            return patient;
        }

        // 2. Kiểm tra trong AccountStaffDAO
        AccountStaff staff = accountStaffDAO.getAccountByEmailOrUsername(email);
        if (staff != null) {
            return staff;
        }

        // 3. Kiểm tra trong AccountPharmacistDAO
        AccountPharmacist pharmacist = accountPharmacistDAO.getAccountByEmailOrUsername(email);
        if (pharmacist != null) {
            return pharmacist;
        }

        // Không tìm thấy tài khoản
        return null;
    }


    public boolean updatePassword(String email, String newPassword) {
        // 1. Kiểm tra trong AccountPatientDAO
        AccountPatient patient = accountPatientDAO.getAccountByEmailOrUsername(email);
        if (patient != null) {
            return accountPatientDAO.updatePassword(email, newPassword);
        }

        // 2. Kiểm tra trong AccountStaffDAO
        AccountStaff staff = accountStaffDAO.getAccountByEmailOrUsername(email);
        if (staff != null) {
            return accountStaffDAO.updatePassword(email, newPassword);
        }

        // 3. Kiểm tra trong AccountPharmacistDAO
        AccountPharmacist pharmacist = accountPharmacistDAO.getAccountByEmailOrUsername(email);
        if (pharmacist != null) {
            return accountPharmacistDAO.updatePassword(email, newPassword);
        }

        // Không tìm thấy tài khoản với email này
        return false;
    }



}

package dao;

import model.AccountStaff;
import model.AccountPharmacist;
import model.AccountPatient;

public class AccountDAO {
    AccountStaffDAO accountStaffDAO = new AccountStaffDAO();
    AccountPharmacistDAO accountPharmacistDAO = new AccountPharmacistDAO();
    AccountPatientDAO accountPatientDAO = new AccountPatientDAO();

    public Object checkLogin(String username, String password) {
        AccountStaff staff = AccountStaffDAO.checkLogin(username, password);
        if (staff != null) {
            return staff;
        }

        AccountPharmacist pharmacist = AccountPharmacistDAO.checkLogin(username, password);
        if (pharmacist != null) {
            return pharmacist;
        }

        AccountPatient patient = AccountPatientDAO.checkLogin(username, password);
        if (patient != null) {
            return patient;
        }

        return null;
    }
}

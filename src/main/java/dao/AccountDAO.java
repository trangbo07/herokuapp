package dao;

import model.AccountStaff;
import model.AccountPharmacist;
import model.AccountPatient;

public class AccountDAO {
    AccountStaffDAO accountStaffDAO = new AccountStaffDAO();
    AccountPharmacistDAO accountPharmacistDAO = new AccountPharmacistDAO();
    AccountPatientDAO accountPatientDAO = new AccountPatientDAO();

    public Object checkLogin(String username, String password) {
        AccountStaff staff = AccountStaffDAO.getAccountStaff(username, password);
        if (staff != null) {
            return staff;
        }

        AccountPharmacist pharmacist = AccountPharmacistDAO.getAccountPharmacist(username, password);
        if (pharmacist != null) {
            return pharmacist;
        }

        AccountPatient patient = AccountPatientDAO.getAccountPatient(username, password);
        if (patient != null) {
            return patient;
        }

        return null;
    }
}

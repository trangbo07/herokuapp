package dao;

import model.AccountStaff;
import model.AccountPharmacist;
import model.AccountPatient;

import java.sql.Connection;

public class AccountDAO {

    private final AccountStaffDAO staffDAO = new AccountStaffDAO();
    private final AccountPharmacistDAO pharmacistDAO = new AccountPharmacistDAO();
    private final AccountPatientDAO patientDAO = new AccountPatientDAO();


    public Object checkLogin(Connection conn, String username, String password) {

        try {
            AccountStaff staff = staffDAO.checkLogin(conn, username, password);
            if (staff != null) return staff;

            AccountPharmacist pharmacist = pharmacistDAO.checkLogin(conn, username, password);
            if (pharmacist != null) return pharmacist;

            AccountPatient patient = patientDAO.checkLogin(conn, username, password);
            if (patient != null) return patient;

        } catch (Exception e) {
            System.out.println("Error in AccountDAO.checkLogin: " + e.getMessage());
        }

        return null;
    }

    public AccountStaff asStaff(Object obj) {
        if (obj instanceof AccountStaff) {
            return (AccountStaff) obj;
        }
        return null;
    }

    public AccountPharmacist asPharmacist(Object obj) {
        if (obj instanceof AccountPharmacist) {
            return (AccountPharmacist) obj;
        }
        return null;
    }

    public AccountPatient asPatient(Object obj) {
        if (obj instanceof AccountPatient) {
            return (AccountPatient) obj;
        }
        return null;
    }
}

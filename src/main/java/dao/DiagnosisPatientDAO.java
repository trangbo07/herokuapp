package dao;

import model.DiagnosisDetails;
import model.DiagnosisPatient;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DiagnosisPatientDAO {
    public List<DiagnosisPatient> getPatientDiagnosis(int patient_id) {
        DBContext db = DBContext.getInstance();
        List<DiagnosisPatient> list = new ArrayList<>();

        try {
            String sql = """
                SELECT p.full_name, p.dob, p.gender,
                           d.disease, d.conclusion, d.treatment_plan
                    FROM Diagnosis d
                    JOIN MedicineRecords m ON d.medicineRecord_id = m.medicineRecord_id
                    JOIN Patient p ON p.patient_id = m.patient_id
                    WHERE m.patient_id = ?
            """;

            PreparedStatement statement = db.getConnection().prepareStatement(sql);
            statement.setInt(1, patient_id);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                DiagnosisPatient detail = new DiagnosisPatient(
                        rs.getString("full_name"),
                        rs.getString("dob"),
                        rs.getString("gender"),
                        rs.getString("disease"),
                        rs.getString("conclusion"),
                        rs.getString("treatment_plan")
                );
                list.add(detail);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return list.isEmpty() ? null : list;
    }
}

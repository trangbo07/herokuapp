package dao;

import model.DiagnosisDetails;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DiagnosisDAO {
    public List<DiagnosisDetails> getDiagnosisDetailsByDoctorID(int doctor_id) {
        DBContext db = DBContext.getInstance();
        List<DiagnosisDetails> list = new ArrayList<>();

        try {
            String sql = """
                SELECT p.full_name, p.dob, p.gender,
                       d.disease, d.conclusion, d.treatment_plan
                FROM Diagnosis d
                JOIN MedicineRecords m ON d.medicineRecord_id = m.medicineRecord_id
                JOIN Patient p ON p.patient_id = m.patient_id
                WHERE d.doctor_id = ?
            """;

            PreparedStatement statement = db.getConnection().prepareStatement(sql);
            statement.setInt(1, doctor_id);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                DiagnosisDetails detail = new DiagnosisDetails(
                        rs.getString("full_name"),
                        rs.getDate("dob").toLocalDate(),
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

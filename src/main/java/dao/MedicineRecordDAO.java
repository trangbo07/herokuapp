package dao;



import dto.RecordSummaryDTO;
import java.sql.*;
import java.util.*;

public class MedicineRecordDAO {
    public List<RecordSummaryDTO> getSummaryByPatientId(int patientId) {
        List<RecordSummaryDTO> list = new ArrayList<>();

        String sql = """
            SELECT 
                mr.medicineRecord_id,
                d.full_name AS doctorName,
                d.department AS clinicName,
                diag.disease AS reason,
                a.note
            FROM MedicineRecords mr
            JOIN Diagnosis diag ON diag.medicineRecord_id = mr.medicineRecord_id
            JOIN Doctor d ON diag.doctor_id = d.doctor_id
            LEFT JOIN Appointment a ON a.appointment_id = (
                SELECT TOP 1 appointment_id FROM Appointment
                WHERE patient_id = mr.patient_id
                ORDER BY appointment_datetime DESC
            )
            WHERE mr.patient_id = ?
            ORDER BY mr.medicineRecord_id DESC
        """;

        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new RecordSummaryDTO(
                        rs.getInt("medicineRecord_id"),
                        rs.getString("doctorName"),
                        rs.getString("clinicName"),
                        rs.getString("reason"),
                        rs.getString("note")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}

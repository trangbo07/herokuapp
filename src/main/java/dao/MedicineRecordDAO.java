package dao;

import dto.RecordSummaryDTO;
import model.MedicineRecords;
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
    
    public int createMedicineRecord(int patientId) {
        String sql = "INSERT INTO MedicineRecords (patient_id) VALUES (?)";
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, patientId);
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return -1;
    }
    
    public MedicineRecords getMedicineRecordById(int medicineRecordId) {
        String sql = "SELECT * FROM MedicineRecords WHERE medicineRecord_id = ?";
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, medicineRecordId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new MedicineRecords(
                    rs.getInt("medicineRecord_id"),
                    rs.getInt("patient_id"),
                    rs.getInt("appointment_id")
                );
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public MedicineRecords getLatestMedicineRecordByPatientId(int patientId) {
        String sql = """
            SELECT TOP 1 * FROM MedicineRecords 
            WHERE patient_id = ? 
            ORDER BY medicineRecord_id DESC
        """;
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new MedicineRecords(
                    rs.getInt("medicineRecord_id"),
                    rs.getInt("patient_id"),
                    rs.getInt("appointment_id")
                );
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public MedicineRecords getMedicineRecordByAppointmentId(int appointmentId) {
        String sql = "SELECT * FROM MedicineRecords WHERE appointment_id = ?";
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, appointmentId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new MedicineRecords(
                    rs.getInt("medicineRecord_id"),
                    rs.getInt("patient_id"),
                    rs.getInt("appointment_id")
                );
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
}

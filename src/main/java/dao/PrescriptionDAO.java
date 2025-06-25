package dao;

import model.Prescription;
import java.sql.*;
import java.util.*;

public class PrescriptionDAO {
    public List<Prescription> getPrescriptionsByPatientId(int patientId) {
        List<Prescription> list = new ArrayList<>();
        String sql = "SELECT p.* FROM Prescription p JOIN MedicineRecords m ON p.medicineRecord_id = m.medicineRecord_id WHERE m.patient_id = ?";
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Prescription p = new Prescription();
                p.setPrescription_id(rs.getInt("prescription_id"));
                p.setMedicineRecord_id(rs.getInt("medicineRecord_id"));
                p.setDoctor_id(rs.getInt("doctor_id"));
                p.setPrescription_date(rs.getString("prescription_date"));
                p.setStatus(rs.getString("status"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, Object>> getPrescriptionsByPatientIdWithDoctorName(int patientId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT p.*, d.full_name as doctor_name FROM Prescription p JOIN MedicineRecords m ON p.medicineRecord_id = m.medicineRecord_id JOIN Doctor d ON p.doctor_id = d.doctor_id WHERE m.patient_id = ?";
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("prescription_id", rs.getInt("prescription_id"));
                map.put("medicineRecord_id", rs.getInt("medicineRecord_id"));
                map.put("doctor_id", rs.getInt("doctor_id"));
                map.put("doctor_name", rs.getString("doctor_name"));
                map.put("prescription_date", rs.getString("prescription_date"));
                map.put("status", rs.getString("status"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, Object>> getPrescriptionDetailsByPatientId(int patientId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT p.prescription_id, p.medicineRecord_id, p.doctor_id, p.prescription_date, p.status, " +
                "pd.prescription_detail_id, m.medicine_id, m.name AS medicine_name, " +
                "pd.quantity AS prescribed_quantity, pd.dosage, pd.note " +
                "FROM Prescription p " +
                "JOIN MedicineRecords mr ON p.medicineRecord_id = mr.medicineRecord_id " +
                "JOIN PrescriptionDetail pd ON p.prescription_id = pd.prescription_id " +
                "JOIN Medicine m ON pd.medicine_id = m.medicine_id " +
                "WHERE mr.patient_id = ? " +
                "ORDER BY p.prescription_id, pd.prescription_detail_id";
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("prescription_id", rs.getInt("prescription_id"));
                map.put("medicineRecord_id", rs.getInt("medicineRecord_id"));
                map.put("doctor_id", rs.getInt("doctor_id"));
                map.put("prescription_date", rs.getString("prescription_date"));
                map.put("status", rs.getString("status"));
                map.put("prescription_detail_id", rs.getInt("prescription_detail_id"));
                map.put("medicine_id", rs.getInt("medicine_id"));
                map.put("medicine_name", rs.getString("medicine_name"));
                map.put("prescribed_quantity", rs.getInt("prescribed_quantity"));
                map.put("dosage", rs.getString("dosage"));
                map.put("note", rs.getString("note"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
} 
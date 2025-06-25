package dao;

import model.Invoice;
import java.util.List;

import java.sql.*;
import java.util.*;

import dto.PatientDiagnosisInvoiceDTO;

public class InvoiceDAO {
    // Lấy danh sách hóa đơn của một bệnh nhân
    public List<Invoice> getInvoicesByPatientId(int patientId) {
        // TODO: Triển khai truy vấn DB
        return null;
    }
    // Lấy chi tiết hóa đơn theo mã hóa đơn
    public Invoice getInvoiceById(int invoiceId) {
        // TODO: Triển khai truy vấn DB
        return null;
    }


    public List<PatientDiagnosisInvoiceDTO> getDiagnosisInvoicesByPatientId(int patientId) {
        List<PatientDiagnosisInvoiceDTO> list = new ArrayList<>();
        String sql = "SELECT p.full_name, p.dob, p.gender, d.disease, d.conclusion, d.treatment_plan, i.invoice_id, i.total_amount, i.status " +
                     "FROM Diagnosis d " +
                     "JOIN MedicineRecords m ON d.medicineRecord_id = m.medicineRecord_id " +
                     "JOIN Patient p ON p.patient_id = m.patient_id " +
                     "JOIN Invoice i ON i.medicineRecord_id = m.medicineRecord_id " +
                     "WHERE m.patient_id = ?";
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PatientDiagnosisInvoiceDTO dto = new PatientDiagnosisInvoiceDTO(
                        rs.getInt("invoice_id"),
                        rs.getString("full_name"),
                        rs.getString("dob"),
                        rs.getString("gender"),
                        rs.getString("disease"),
                        rs.getString("conclusion"),
                        rs.getString("treatment_plan"),
                        rs.getDouble("total_amount"),
                        rs.getString("status")
                    );
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateInvoiceStatus(int invoiceId, String status) {
        System.out.println("[DAO] Updating invoiceId=" + invoiceId + " to status=" + status);
        String sql = "UPDATE Invoice SET status = ? WHERE invoice_id = ?";
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, invoiceId);
            int rows = ps.executeUpdate();
            System.out.println("[DAO] Rows updated: " + rows);
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
} 
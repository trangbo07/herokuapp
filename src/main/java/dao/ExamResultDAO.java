package dao;

import model.ExamResult;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamResultDAO {
    
    public boolean createExamResult(ExamResult examResult) {
        String sql = """
            INSERT INTO ExamResult (medicineRecord_id, symptoms, preliminary_diagnosis, doctor_id)
            VALUES (?, ?, ?, ?)
        """;
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, examResult.getMedicineRecord_id());
            ps.setString(2, examResult.getSymptoms());
            ps.setString(3, examResult.getPreliminary_diagnosis());
            ps.setInt(4, examResult.getDoctor_id());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public ExamResult getExamResultById(int examResultId) {
        String sql = "SELECT * FROM ExamResult WHERE exam_result_id = ?";
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, examResultId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new ExamResult(
                    rs.getInt("exam_result_id"),
                    rs.getInt("medicineRecord_id"),
                    rs.getInt("doctor_id"),
                    rs.getString("preliminary_diagnosis"),
                    rs.getString("symptoms")
                );
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public List<ExamResult> getExamResultsByMedicineRecordId(int medicineRecordId) {
        List<ExamResult> results = new ArrayList<>();
        String sql = "SELECT * FROM ExamResult WHERE medicineRecord_id = ? ORDER BY exam_result_id DESC";
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, medicineRecordId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                results.add(new ExamResult(
                    rs.getInt("exam_result_id"),
                    rs.getInt("medicineRecord_id"),
                    rs.getInt("doctor_id"),
                    rs.getString("preliminary_diagnosis"),
                    rs.getString("symptoms")
                ));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return results;
    }
    
    public List<ExamResult> getExamResultsByDoctorId(int doctorId) {
        List<ExamResult> results = new ArrayList<>();
        String sql = "SELECT * FROM ExamResult WHERE doctor_id = ? ORDER BY exam_result_id DESC";
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                results.add(new ExamResult(
                    rs.getInt("exam_result_id"),
                    rs.getInt("medicineRecord_id"),
                    rs.getInt("doctor_id"),
                    rs.getString("preliminary_diagnosis"),
                    rs.getString("symptoms")
                ));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return results;
    }
    
    public boolean updateExamResult(ExamResult examResult) {
        String sql = """
            UPDATE ExamResult 
            SET symptoms = ?, preliminary_diagnosis = ?
            WHERE exam_result_id = ?
        """;
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, examResult.getSymptoms());
            ps.setString(2, examResult.getPreliminary_diagnosis());
            ps.setInt(3, examResult.getExam_result_id());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteExamResult(int examResultId) {
        String sql = "DELETE FROM ExamResult WHERE exam_result_id = ?";
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, examResultId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public ExamResult getExamResultByMedicineRecordId(int medicineRecordId) {
        String sql = "SELECT * FROM ExamResult WHERE medicineRecord_id = ?";
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, medicineRecordId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new ExamResult(
                    rs.getInt("exam_result_id"),
                    rs.getInt("medicineRecord_id"),
                    rs.getInt("doctor_id"),
                    rs.getString("preliminary_diagnosis"),
                    rs.getString("symptoms")
                );
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public List<ExamResult> getExamResultsByPatientId(int patientId) {
        List<ExamResult> results = new ArrayList<>();
        String sql = """
            SELECT er.* FROM ExamResult er
            JOIN MedicineRecords mr ON er.medicineRecord_id = mr.medicineRecord_id
            WHERE mr.patient_id = ?
            ORDER BY er.exam_result_id DESC
        """;
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                results.add(new ExamResult(
                    rs.getInt("exam_result_id"),
                    rs.getInt("medicineRecord_id"),
                    rs.getInt("doctor_id"),
                    rs.getString("preliminary_diagnosis"),
                    rs.getString("symptoms")
                ));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return results;
    }
} 
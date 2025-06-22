package dao;

import model.ServiceResult;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceResultDAO {
    
    public boolean createServiceResult(ServiceResult result) {
        String sql = """
            INSERT INTO ResultsOfParaclinicalServices (service_order_item_id, result_description, created_at)
            VALUES (?, ?, ?)
        """;
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            ps.setInt(1, result.getService_order_item_id());
            ps.setString(2, result.getResult_description());
            ps.setString(3, currentDate);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateServiceResult(ServiceResult result) {
        String sql = """
            UPDATE ResultsOfParaclinicalServices 
            SET result_description = ?
            WHERE service_order_item_id = ?
        """;
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, result.getResult_description());
            ps.setInt(2, result.getService_order_item_id());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public ServiceResult getServiceResultByServiceOrderItemId(int serviceOrderItemId) {
        String sql = "SELECT * FROM ResultsOfParaclinicalServices WHERE service_order_item_id = ?";
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, serviceOrderItemId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new ServiceResult(
                    rs.getInt("result_id"),
                    rs.getInt("service_order_item_id"),
                    rs.getString("result_description"),
                    rs.getString("created_at")
                );
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public List<ServiceResult> getServiceResultsByDoctorId(int doctorId) {
        List<ServiceResult> results = new ArrayList<>();
        String sql = """
            SELECT rps.* FROM ResultsOfParaclinicalServices rps
            JOIN ServiceOrderItem soi ON rps.service_order_item_id = soi.service_order_item_id
            WHERE soi.doctor_id = ?
            ORDER BY rps.created_at DESC
        """;
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                results.add(new ServiceResult(
                    rs.getInt("result_id"),
                    rs.getInt("service_order_item_id"),
                    rs.getString("result_description"),
                    rs.getString("created_at")
                ));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return results;
    }
    
    public boolean deleteServiceResult(int resultId) {
        String sql = "DELETE FROM ResultsOfParaclinicalServices WHERE result_id = ?";
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, resultId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
} 
package dao;

import model.ServiceOrder;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ServiceOrderDAO {
    
    public int createServiceOrder(int doctorId, int medicineRecordId) {
        String sql = """
            INSERT INTO ServiceOrder (doctor_id, medicineRecord_id, order_date)
            VALUES (?, ?, ?)
        """;
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            ps.setInt(1, doctorId);
            ps.setInt(2, medicineRecordId);
            ps.setString(3, currentDate);
            
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
    
    public ServiceOrder getServiceOrderById(int serviceOrderId) {
        String sql = "SELECT * FROM ServiceOrder WHERE service_order_id = ?";
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, serviceOrderId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new ServiceOrder(
                    rs.getInt("service_order_id"),
                    rs.getInt("doctor_id"),
                    rs.getInt("medicineRecord_id"),
                    rs.getString("order_date")
                );
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public List<ServiceOrder> getServiceOrdersByMedicineRecordId(int medicineRecordId) {
        List<ServiceOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM ServiceOrder WHERE medicineRecord_id = ? ORDER BY order_date DESC";
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, medicineRecordId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                orders.add(new ServiceOrder(
                    rs.getInt("service_order_id"),
                    rs.getInt("doctor_id"),
                    rs.getInt("medicineRecord_id"),
                    rs.getString("order_date")
                ));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return orders;
    }
    
    public List<ServiceOrder> getServiceOrdersByDoctorId(int doctorId) {
        List<ServiceOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM ServiceOrder WHERE doctor_id = ? ORDER BY order_date DESC";
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                orders.add(new ServiceOrder(
                    rs.getInt("service_order_id"),
                    rs.getInt("doctor_id"),
                    rs.getInt("medicineRecord_id"),
                    rs.getString("order_date")
                ));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return orders;
    }
} 
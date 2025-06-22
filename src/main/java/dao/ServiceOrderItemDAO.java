package dao;

import model.ServiceOrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceOrderItemDAO {
    
    public boolean createServiceOrderItem(ServiceOrderItem item) {
        String sql = """
            INSERT INTO ServiceOrderItem (service_order_id, service_id, doctor_id)
            VALUES (?, ?, ?)
        """;
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, item.getService_order_id());
            ps.setInt(2, item.getService_id());
            ps.setInt(3, item.getDoctor_id());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean createMultipleServiceOrderItems(int serviceOrderId, int doctorId, List<Integer> serviceIds) {
        String sql = """
            INSERT INTO ServiceOrderItem (service_order_id, service_id, doctor_id)
            VALUES (?, ?, ?)
        """;
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (Integer serviceId : serviceIds) {
                ps.setInt(1, serviceOrderId);
                ps.setInt(2, serviceId);
                ps.setInt(3, doctorId);
                ps.addBatch();
            }
            
            int[] results = ps.executeBatch();
            for (int result : results) {
                if (result <= 0) {
                    return false;
                }
            }
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Map<String, Object>> getServiceOrderItemsWithDetails(int serviceOrderId) {
        List<Map<String, Object>> items = new ArrayList<>();
        String sql = """
            SELECT 
                soi.service_order_item_id,
                soi.service_order_id,
                soi.service_id,
                soi.doctor_id,
                lms.name AS service_name,
                lms.description AS service_description,
                lms.price AS service_price,
                d.full_name AS doctor_name
            FROM ServiceOrderItem soi
            JOIN ListOfMedicalService lms ON soi.service_id = lms.service_id
            JOIN Doctor d ON soi.doctor_id = d.doctor_id
            WHERE soi.service_order_id = ?
        """;
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, serviceOrderId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("service_order_item_id", rs.getInt("service_order_item_id"));
                item.put("service_order_id", rs.getInt("service_order_id"));
                item.put("service_id", rs.getInt("service_id"));
                item.put("doctor_id", rs.getInt("doctor_id"));
                item.put("service_name", rs.getString("service_name"));
                item.put("service_description", rs.getString("service_description"));
                item.put("service_price", rs.getDouble("service_price"));
                item.put("doctor_name", rs.getString("doctor_name"));
                items.add(item);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    public List<ServiceOrderItem> getServiceOrderItemsByServiceOrderId(int serviceOrderId) {
        List<ServiceOrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM ServiceOrderItem WHERE service_order_id = ?";
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, serviceOrderId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                items.add(new ServiceOrderItem(
                    rs.getInt("service_order_item_id"),
                    rs.getInt("service_order_id"),
                    rs.getInt("service_id"),
                    rs.getInt("doctor_id")
                ));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    public ServiceOrderItem getServiceOrderItemById(int serviceOrderItemId) {
        String sql = "SELECT * FROM ServiceOrderItem WHERE service_order_item_id = ?";
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, serviceOrderItemId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new ServiceOrderItem(
                    rs.getInt("service_order_item_id"),
                    rs.getInt("service_order_id"),
                    rs.getInt("service_id"),
                    rs.getInt("doctor_id")
                );
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean deleteServiceOrderItem(int serviceOrderItemId) {
        String sql = "DELETE FROM ServiceOrderItem WHERE service_order_item_id = ?";
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, serviceOrderItemId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
} 
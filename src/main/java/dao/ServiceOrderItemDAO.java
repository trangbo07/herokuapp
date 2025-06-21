package dao;

import model.ServiceOrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
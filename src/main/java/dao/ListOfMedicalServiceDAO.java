package dao;

import model.ListOfMedicalService;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ListOfMedicalServiceDAO {
    
    public List<ListOfMedicalService> getAllServices() {
        List<ListOfMedicalService> services = new ArrayList<>();
        String sql = "SELECT * FROM ListOfMedicalService ORDER BY name";
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                services.add(new ListOfMedicalService(
                    rs.getInt("service_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price")
                ));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return services;
    }
    
    public ListOfMedicalService getServiceById(int serviceId) {
        String sql = "SELECT * FROM ListOfMedicalService WHERE service_id = ?";
        
        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, serviceId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new ListOfMedicalService(
                    rs.getInt("service_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price")
                );
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
} 
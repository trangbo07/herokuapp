package dao;
import dto.DoctorDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
public class DoctorDAO {



    public List<DoctorDTO> getAllDoctorDTOs() {
        List<DoctorDTO> list = new ArrayList<>();

        String sql = "SELECT doctor_id, full_name, department, eduLevel FROM Doctor";

        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DoctorDTO dto = new DoctorDTO(
                        rs.getInt("doctor_id"),
                        rs.getString("full_name"),
                        rs.getString("department"),
                        rs.getString("eduLevel"),
                        "/assets/assets/images/doctor/" + rs.getInt("doctor_id") + ".webp" // ảnh tự động
                );
                list.add(dto);
            }

        } catch (Exception e) {
            System.err.println("Error in getAllDoctorDTOs: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

}

package dao;

import dto.DoctorDTOFA;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AdminSystemDAO {

    //For Doctor
    public List<DoctorDTOFA> getAllDoctors() {
        List<DoctorDTOFA> list = new ArrayList<>();
        String sql = """
                    SELECT a.account_staff_id, a.username, a.role, a.email, a.img, a.status,
                                       b.doctor_id, b.full_name, b.department, b.phone, b.eduLevel
                                FROM AccountStaff a
                                JOIN Doctor b ON a.account_staff_id = b.account_staff_id
                """;

        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DoctorDTOFA dto = new DoctorDTOFA(
                        rs.getInt("account_staff_id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getString("img"),
                        rs.getString("status"),
                        rs.getInt("doctor_id"),
                        rs.getString("full_name"),
                        rs.getString("department"),
                        rs.getString("phone"),
                        rs.getString("eduLevel")
                );
                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public DoctorDTOFA getDoctorById(int doctorId) {
        String sql = """
                    SELECT a.account_staff_id, a.username, a.role, a.email, a.img, a.status,
                           b.doctor_id, b.full_name, b.department, b.phone, b.eduLevel
                    FROM AccountStaff a
                    JOIN Doctor b ON a.account_staff_id = b.account_staff_id
                    WHERE b.doctor_id = ?
                """;

        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DoctorDTOFA(
                            rs.getInt("account_staff_id"),
                            rs.getString("username"),
                            rs.getString("role"),
                            rs.getString("email"),
                            rs.getString("img"),
                            rs.getString("status"),
                            rs.getInt("doctor_id"),
                            rs.getString("full_name"),
                            rs.getString("department"),
                            rs.getString("phone"),
                            rs.getString("eduLevel")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteDoctor(int doctorId) {
        String sql = """
                    UPDATE AccountStaff
                    SET status = 'Disable'
                    WHERE account_staff_id = (
                        SELECT account_staff_id FROM Doctor WHERE doctor_id = ?
                    )
                      AND role = 'Doctor'
                """;

        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            int affected = ps.executeUpdate();

            return affected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getDistinctValues(String fieldName) {
        List<String> result = new ArrayList<>();
        // Kiểm tra để tránh SQL injection từ đầu vào không hợp lệ
        List<String> allowedFields = List.of("status", "eduLevel", "department");

        if (!allowedFields.contains(fieldName)) {
            throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }

        String sql = "SELECT DISTINCT " + fieldName +
                " FROM AccountStaff a JOIN Doctor b ON a.account_staff_id = b.account_staff_id";

        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(rs.getString(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<DoctorDTOFA> filterDoctors(String status, String eduLevel, String department, String search) {
        List<DoctorDTOFA> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                    SELECT a.account_staff_id, a.username, a.password, a.email, a.img, a.role, a.status,
                           b.doctor_id, b.full_name, b.phone, b.department, b.eduLevel
                    FROM AccountStaff a JOIN Doctor b ON a.account_staff_id = b.account_staff_id
                    WHERE 1 = 1
                """);

        List<Object> params = new ArrayList<>();

        if (status != null && !status.isEmpty()) {
            sql.append(" AND a.status = ?");
            params.add(status);
        }

        if (eduLevel != null && !eduLevel.isEmpty()) {
            sql.append(" AND b.eduLevel = ?");
            params.add(eduLevel);
        }

        if (department != null && !department.isEmpty()) {
            sql.append(" AND b.department = ?");
            params.add(department);
        }

        if (search != null && !search.isEmpty()) {
            sql.append("""
                     AND (
                         b.full_name COLLATE Latin1_General_CI_AI LIKE ? OR
                         a.username COLLATE Latin1_General_CI_AI LIKE ? OR
                         a.email COLLATE Latin1_General_CI_AI LIKE ?
                     )
                    """);

            String keyword = "%" + search.replaceAll("\\s+", " ") + "%";
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
        }

        try (Connection conn = DBContext.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DoctorDTOFA doctor = new DoctorDTOFA(
                        rs.getInt("account_staff_id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getString("img"),
                        rs.getString("status"),
                        rs.getInt("doctor_id"),
                        rs.getString("full_name"),
                        rs.getString("department"),
                        rs.getString("phone"),
                        rs.getString("eduLevel")
                );
                result.add(doctor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    //For ...
}
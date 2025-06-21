package dao;

import dto.AppointmentDTO;
import model.Appointment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    public List<AppointmentDTO> getAppointmentByDoctorIDUpcomming(int doctor_id, String currentime) {
        DBContext db = DBContext.getInstance();
        List<AppointmentDTO> appointments = new ArrayList<>();

        try {
            String sql = """    
                     SELECT a.*, p.full_name, p.dob, p.gender, p.phone
                     FROM Appointment a
                     JOIN Patient p ON a.patient_id = p.patient_id
                     WHERE doctor_id = ? AND status = 'Confirmed'
                       AND appointment_datetime >= ?
                     ORDER BY appointment_datetime ASC;
                    """;
            PreparedStatement statement = db.getConnection().prepareStatement(sql);
            statement.setInt(1, doctor_id);
            statement.setString(2, currentime);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                AppointmentDTO appointmentDTO =  null;
                appointmentDTO = new AppointmentDTO(
                        rs.getInt("appointment_id"),
                        rs.getString("appointment_datetime"),
                        rs.getString("shift"),
                        rs.getString("status"),
                        rs.getString("note"),
                        rs.getInt("patient_id"),
                        rs.getString("full_name"),
                        rs.getString("dob"),
                        rs.getString("gender"),
                        rs.getString("phone")
                );
                appointments.add(appointmentDTO);
            }
        } catch (Exception e) {
            return null;
        }

        if (appointments.isEmpty()) {
            return null;
        } else {
            return appointments;
        }
    }

    public List<AppointmentDTO> getAppointmentByDoctorIDWithStatus(int doctor_id, String status) {
        DBContext db = DBContext.getInstance();
        List<AppointmentDTO> appointments = new ArrayList<>();

        try {
            String sql = """
                    SELECT a.*, p.full_name, p.dob, p.gender, p.phone
                    FROM Appointment a
                    JOIN Patient p ON a.patient_id = p.patient_id
                    WHERE doctor_id = ? AND status = ?
                    ORDER BY appointment_datetime DESC;
                    """;
            PreparedStatement statement = db.getConnection().prepareStatement(sql);
            statement.setInt(1, doctor_id);
            statement.setString(2, status);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                AppointmentDTO appointmentDTO =  null;
                appointmentDTO = new AppointmentDTO(
                        rs.getInt("appointment_id"),
                        rs.getString("appointment_datetime"),
                        rs.getString("shift"),
                        rs.getString("status"),
                        rs.getString("note"),
                        rs.getInt("patient_id"),
                        rs.getString("full_name"),
                        rs.getString("dob"),
                        rs.getString("gender"),
                        rs.getString("phone")
                );
                appointments.add(appointmentDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return appointments.isEmpty() ? null : appointments;
    }

    public List<AppointmentDTO> getAppointmentsTodayByDoctorID(int doctor_id, String currentDateOnly) {
        DBContext db = DBContext.getInstance();
        List<AppointmentDTO> appointments = new ArrayList<>();

        try {
            String sql = """
                    SELECT a.*, p.full_name, p.dob, p.gender, p.phone
                    FROM Appointment a
                    JOIN Patient p ON a.patient_id = p.patient_id
                    WHERE doctor_id = ? 
                      AND CONVERT(date, appointment_datetime) = CONVERT(date, ?)
                    ORDER BY appointment_datetime DESC;
                    """;

            PreparedStatement statement = db.getConnection().prepareStatement(sql);
            statement.setInt(1, doctor_id);
            statement.setString(2, currentDateOnly); // Format: "2025-06-18"

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                AppointmentDTO appointment = new AppointmentDTO(
                        rs.getInt("appointment_id"),
                        rs.getString("appointment_datetime"),
                        rs.getString("shift"),
                        rs.getString("status"),
                        rs.getString("note"),
                        rs.getInt("patient_id"),
                        rs.getString("full_name"),
                        rs.getString("dob"),
                        rs.getString("gender"),
                        rs.getString("phone")
                );
                appointments.add(appointment);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return appointments.isEmpty() ? null : appointments;
    }

    public AppointmentDTO getAppointmentDetailWithAppointmentById(int appointmentId) {
        DBContext db = DBContext.getInstance();
        AppointmentDTO appointmentDTO = null;

        try {
            String sql = """
                        SELECT a.*, p.full_name, p.dob, p.gender, p.phone
                        FROM Appointment a
                        JOIN Patient p ON a.patient_id = p.patient_id
                        WHERE a.appointment_id = ?
                    """;

            PreparedStatement statement = db.getConnection().prepareStatement(sql);
            statement.setInt(1, appointmentId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                appointmentDTO = new AppointmentDTO(
                        rs.getInt("appointment_id"),
                        rs.getString("appointment_datetime"),
                        rs.getString("shift"),
                        rs.getString("status"),
                        rs.getString("note"),
                        rs.getInt("patient_id"),
                        rs.getString("full_name"),
                        rs.getString("dob"),
                        rs.getString("gender"),
                        rs.getString("phone")
                );
            }

            rs.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return appointmentDTO;
    }

    public boolean cancelAppointmentById(int appointmentId) {
        DBContext db = DBContext.getInstance();
        String sql = "UPDATE Appointment SET status = 'Cancelled' WHERE appointment_id = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAppointmentStatus(int appointmentId, String status) {
        DBContext db = DBContext.getInstance();
        String sql = "UPDATE Appointment SET status = ? WHERE appointment_id = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, appointmentId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<AppointmentDTO> searchAppointmentsByKeyword(int doctorId, String keyword, String status) {
        DBContext db = DBContext.getInstance();
        List<AppointmentDTO> appointments = new ArrayList<>();

        try {
            String sql = """
            SELECT a.*, p.full_name, p.dob, p.gender, p.phone
            FROM Appointment a
            JOIN Patient p ON a.patient_id = p.patient_id
            WHERE a.doctor_id = ?
              AND a.status = ?
              AND (
                REPLACE(p.full_name, ' ', '') COLLATE Latin1_General_CI_AI LIKE ?
                OR REPLACE(a.shift, ' ', '') COLLATE Latin1_General_CI_AI LIKE ?
                OR REPLACE(a.note, ' ', '') COLLATE Latin1_General_CI_AI LIKE ?
              )
            ORDER BY a.appointment_datetime DESC;
        """;

            PreparedStatement statement = db.getConnection().prepareStatement(sql);
            String likeKeyword = "%" + normalizeKeyword(keyword)  + "%";
            statement.setInt(1, doctorId);
            statement.setString(2, status);
            statement.setString(3, likeKeyword);
            statement.setString(4, likeKeyword);
            statement.setString(5, likeKeyword);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                AppointmentDTO appointment = new AppointmentDTO(
                        rs.getInt("appointment_id"),
                        rs.getString("appointment_datetime"),
                        rs.getString("shift"),
                        rs.getString("status"),
                        rs.getString("note"),
                        rs.getInt("patient_id"),
                        rs.getString("full_name"),
                        rs.getString("dob"),
                        rs.getString("gender"),
                        rs.getString("phone")
                );
                appointments.add(appointment);
            }

            rs.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return appointments.isEmpty() ? null : appointments;
    }

    public static String normalizeKeyword(String input) {
        if (input == null) return "";
        input = input.trim().replaceAll("\\s+", " "); // loại bỏ khoảng trắng thừa
        String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").replaceAll(" ", "");
    }

}

package dao;

import model.Appointment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    public  List<Appointment> getAppointmentByDoctorIDUpcomming(int doctor_id, String currentime) {
        DBContext db = DBContext.getInstance();
        List<Appointment> appointments = new ArrayList<>();

        try {
            String sql = """    
                     SELECT *
                     FROM Appointment
                     WHERE doctor_id = ? AND status = 'Confirmed'
                       AND appointment_datetime >= ?
                     ORDER BY appointment_datetime ASC;
                    """;
            PreparedStatement statement = db.getConnection().prepareStatement(sql);
            statement.setInt(1, doctor_id);
            statement.setString(2, currentime);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getInt("doctor_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("receptionist_id"),
                        rs.getString("appointment_datetime"),
                        rs.getString("shift"),
                        rs.getString("status"),
                        rs.getString("note")
                );
                appointments.add(appointment);
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

    public List<Appointment> getAppointmentByDoctorIDWithStatus(int doctor_id, String status) {
        DBContext db = DBContext.getInstance();
        List<Appointment> appointments = new ArrayList<>();

        try {
            String sql = """
                SELECT *
                FROM Appointment
                WHERE doctor_id = ? AND status = ?
                ORDER BY appointment_datetime DESC;
                """;
            PreparedStatement statement = db.getConnection().prepareStatement(sql);
            statement.setInt(1, doctor_id);
            statement.setString(2, status);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getInt("doctor_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("receptionist_id"),
                        rs.getString("appointment_datetime"),
                        rs.getString("shift"),
                        rs.getString("status"),
                        rs.getString("note")
                );
                appointments.add(appointment);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return appointments.isEmpty() ? null : appointments;
    }

    public List<Appointment> getAppointmentsTodayByDoctorID(int doctor_id, String currentDateOnly) {
        DBContext db = DBContext.getInstance();
        List<Appointment> appointments = new ArrayList<>();

        try {
            String sql = """
            SELECT *
            FROM Appointment
            WHERE doctor_id = ? 
              AND CONVERT(date, appointment_datetime) = CONVERT(date, ?)
            ORDER BY appointment_datetime DESC;
            """;

            PreparedStatement statement = db.getConnection().prepareStatement(sql);
            statement.setInt(1, doctor_id);
            statement.setString(2, currentDateOnly); // Format: "2025-06-18"

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getInt("doctor_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("receptionist_id"),
                        rs.getString("appointment_datetime"),
                        rs.getString("shift"),
                        rs.getString("status"),
                        rs.getString("note")
                );
                appointments.add(appointment);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return appointments.isEmpty() ? null : appointments;
    }

}

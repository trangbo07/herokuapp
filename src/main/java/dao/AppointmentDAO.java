package dao;

import model.Appointment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    public  List<Appointment> getAppointmentByDoctorID(int doctor_id) {
        DBContext db = DBContext.getInstance();
        List<Appointment> appointments = new ArrayList<>();

        try {
            String sql = """    
                     SELECT * FROM Appointment WHERE doctor_id = ?
                    """;
            PreparedStatement statement = db.getConnection().prepareStatement(sql);
            statement.setInt(1, doctor_id);

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
}

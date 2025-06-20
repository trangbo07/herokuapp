package controller;

import com.google.gson.Gson;
import dao.AccountStaffDAO;
import dao.AppointmentDAO;
import dto.AppointmentDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AccountStaff;
import model.Appointment;
import model.Doctor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/doctor/appointment")
public class DoctorAppointmentServlet extends HttpServlet {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final AccountStaffDAO accountStaffDAO = new AccountStaffDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Unauthorized\"}");
            return;
        }

        String action = request.getParameter("action");
        String idParam = request.getParameter("id");

        if ("detail".equalsIgnoreCase(action) && idParam != null) {
            try {
                int appointmentId = Integer.parseInt(idParam);
                AppointmentDTO appointment = appointmentDAO.getAppointmentDetailWithAppointmentById(appointmentId);

                if (appointment == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"Appointment not found\"}");
                    return;
                }

                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print(gson.toJson(appointment));
                out.flush();
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid appointment ID\"}");
            }
            return;
        }

        if ("cancel".equalsIgnoreCase(action) && idParam != null) {
            try {
                int appointmentId = Integer.parseInt(idParam);
                boolean success = appointmentDAO.cancelAppointmentById(appointmentId); // bạn cần tạo hàm này trong DAO

                response.setContentType("application/json");
                PrintWriter out = response.getWriter();

                if (success) {
                    out.print("{\"message\":\"Appointment cancelled successfully\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"error\":\"Failed to cancel appointment\"}");
                }
                out.flush();

            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid appointment ID\"}");
            }
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Unauthorized\"}");
            return;
        }

        AccountStaff user = (AccountStaff) session.getAttribute("user");
        Doctor doctor = (Doctor) accountStaffDAO.getOStaffByStaffId(user.getAccount_staff_id(), user.getRole());

        if (doctor == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Doctor not found\"}");
            return;
        }

        // Đọc action từ JSON body
        String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        String action = gson.fromJson(requestBody, RequestAction.class).action;

//        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String currentTime = "2024-01-15 08:00:00.000";
        List<Appointment> appointments;

        // Gọi DAO tương ứng theo action
        switch (action) {
            case "Upcoming" -> appointments = appointmentDAO.getAppointmentByDoctorIDUpcomming(doctor.getDoctor_id(), currentTime);
            case "Completed" -> appointments = appointmentDAO.getAppointmentByDoctorIDWithStatus(doctor.getDoctor_id(), "Completed");
            case "Cancelled" -> appointments = appointmentDAO.getAppointmentByDoctorIDWithStatus(doctor.getDoctor_id(), "Cancelled");
            case "Count" -> appointments = appointmentDAO.getAppointmentsTodayByDoctorID(doctor.getDoctor_id(), currentTime);
            default -> {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid action\"}");
                return;
            }
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        if (!action.equals("Count")) {
            out.print(gson.toJson(appointments));
        } else {
            out.print(gson.toJson(appointments.size()));
        }
        out.flush();
    }

    private static class RequestAction {
        String action;
    }
}


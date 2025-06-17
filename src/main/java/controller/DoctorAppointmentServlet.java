package controller;

import com.google.gson.Gson;
import dao.AccountStaffDAO;
import dao.AppointmentDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/doctor/appointment")
public class DoctorAppointmentServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        AppointmentDAO  appointmentDAO = new AppointmentDAO();
        AccountStaffDAO accountStaffDAO = new AccountStaffDAO();

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/view/home.html");
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

        List<Appointment> appointments = appointmentDAO.getAppointmentByDoctorID(doctor.getDoctor_id());

        Gson gson = new Gson();
        String json = gson.toJson(appointments);

        System.out.println(json);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }
}

package controller;

import com.google.gson.Gson;
import dao.AccountStaffDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AccountStaff;
import model.Doctor;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/doctor/profile")
public class DoctorProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        AccountStaffDAO accountStaffDAO = new AccountStaffDAO();

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/view/home.html");
            return;
        }

        AccountStaff user = (AccountStaff) session.getAttribute("user");

        // Lấy thông tin bác sĩ từ staff_id và role
        Doctor doctor = (Doctor) accountStaffDAO.getOStaffByStaffId(user.getAccount_staff_id(), user.getRole());

        if (doctor == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Doctor not found\"}");
            return;
        }


        Gson gson = new Gson();
        String json = gson.toJson(doctor);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }
}

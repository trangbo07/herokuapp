package controller;

import com.google.gson.Gson;
import dao.DoctorDAO;
import dto.DoctorDetailsDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AccountStaff;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/doctor/profile")
public class DoctorProfileServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final DoctorDAO doctorDAO = new DoctorDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Check session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/view/home.html");
            return;
        }

        // Get user
        AccountStaff user = (AccountStaff) session.getAttribute("user");
        int accountStaffId = user.getAccount_staff_id();

        // Get doctor details
        DoctorDetailsDTO doctorDetails = doctorDAO.getDoctorDetailsByAccountStaffId(accountStaffId);
        if (doctorDetails == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            PrintWriter out = response.getWriter();
            out.write("{\"error\":\"Doctor not found\"}");
            out.flush();
            return;
        }

        // Return JSON
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(doctorDetails));
        out.flush();
    }
}
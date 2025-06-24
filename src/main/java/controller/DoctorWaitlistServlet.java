package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dao.AccountStaffDAO;
import dao.AppointmentDAO;
import dao.WaitlistDAO;
import dto.AppointmentDTO;
import dto.WaitlistDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AccountStaff;
import model.Doctor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/api/doctor/waitlist")
public class DoctorWaitlistServlet extends HttpServlet {

    private final AccountStaffDAO accountStaffDAO = new AccountStaffDAO();
    private final WaitlistDAO waitlistDAO = new WaitlistDAO();
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

        AccountStaff user = (AccountStaff) session.getAttribute("user");
        Doctor doctor = (Doctor) accountStaffDAO.getOStaffByStaffId(user.getAccount_staff_id(), user.getRole());

        if (doctor == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Doctor not found\"}");
            return;
        }

        String action = request.getParameter("action");
        String idParam = request.getParameter("id");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // 1. Trả danh sách waitlist của bác sĩ
        if ("waitlist".equalsIgnoreCase(action)) {
            List<WaitlistDTO> waitlist = waitlistDAO.getDoctorWaitlist(doctor.getDoctor_id());
            out.print(gson.toJson(waitlist));
            out.flush();
            return;
        }

        // 2. Trả thông tin chi tiết 1 bệnh nhân trong waitlist
        if ("detail".equalsIgnoreCase(action) && idParam != null) {
            try {
                int waitlistId = Integer.parseInt(idParam);
                WaitlistDTO waitlistDetail = waitlistDAO.getWaitlistDetailById(waitlistId);

                if (waitlistDetail == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.write("{\"error\":\"Patient not found in waitlist.\"}");
                    return;
                }

                out.print(gson.toJson(waitlistDetail));
                out.flush();
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"Invalid waitlist ID.\"}");
            }
            return;
        }

        // 3. Nếu không phải action hợp lệ
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write("{\"error\":\"Invalid action\"}");
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

    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            BufferedReader reader = request.getReader();
            JsonObject jsonBody = JsonParser.parseReader(reader).getAsJsonObject();

            if (!jsonBody.has("waitlistId") || !jsonBody.has("status")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Missing waitlistId or status\"}");
                return;
            }

            int waitlistId = jsonBody.get("waitlistId").getAsInt();
            String status = jsonBody.get("status").getAsString();

            boolean updated = waitlistDAO.updateStatus(waitlistId, status);

            response.setContentType("application/json");
            if (updated) {
                response.getWriter().write("{\"success\":true}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"success\":false,\"message\":\"Waitlist not found or not updated\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

}


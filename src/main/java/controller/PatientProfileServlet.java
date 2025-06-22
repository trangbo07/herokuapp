package controller;

import com.google.gson.Gson;
import dao.AccountPatientDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AccountPatient;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/patient/profile")
public class PatientProfileServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final AccountPatientDAO accountPatientDAO = new AccountPatientDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Not logged in\"}");
            return;
        }

        Object user = session.getAttribute("user");
        if (!(user instanceof AccountPatient)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\":\"Not a patient\"}");
            return;
        }

        AccountPatient accountPatient = (AccountPatient) user;
        int accountPatientId = accountPatient.getAccount_patient_id();
        AccountPatientDAO.PatientProfile profile = accountPatientDAO.getPatientProfileByAccountPatientId(accountPatientId);
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(profile));
        out.flush();
    }
} 
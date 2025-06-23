package controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AccountPatient;

import java.io.IOException;

@WebServlet("/api/session/patient")
    public class SessionInfoServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, IOException {
            HttpSession session = request.getSession(false);
            response.setContentType("application/json");

            if (session != null && session.getAttribute("user") instanceof AccountPatient patient) {
                response.getWriter().write("{\"patientId\": " + patient.getAccount_patient_id() + "}");
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Not logged in as patient\"}");
            }
        }
    }


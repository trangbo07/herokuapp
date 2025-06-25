package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.PrescriptionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Prescription;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/prescription")
public class PrescriptionServlet extends HttpServlet {
    private final PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String patientIdStr = request.getParameter("patientId");
        if (patientIdStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Missing patientId\"}");
            return;
        }
        int patientId = Integer.parseInt(patientIdStr);
        var prescriptionDetails = prescriptionDAO.getPrescriptionDetailsByPatientId(patientId);
        response.setContentType("application/json");
        mapper.writeValue(response.getWriter(), prescriptionDetails);
    }
}


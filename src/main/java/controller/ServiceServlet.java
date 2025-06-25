package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ListOfMedicalServiceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ListOfMedicalService;

import java.io.IOException;
import java.util.List;

@WebServlet("/services")
public class ServiceServlet extends HttpServlet {
    private ListOfMedicalServiceDAO serviceDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        serviceDAO = new ListOfMedicalServiceDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("api".equals(action)) {
            handleApiRequest(request, response);
        } else {
            // Forward to the services page
            request.getRequestDispatcher("/view/services.html").forward(request, response);
        }
    }

    private void handleApiRequest(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            List<ListOfMedicalService> services = serviceDAO.getAllServices();
            String jsonResponse = objectMapper.writeValueAsString(services);
            response.getWriter().write(jsonResponse);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to fetch services: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // For future implementation: Add, Update, Delete services
            response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
            response.getWriter().write("{\"message\": \"Service management operations not yet implemented\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Operation failed: " + e.getMessage() + "\"}");
        }
    }
} 
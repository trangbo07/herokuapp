package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.AccountStaffDAO;
import dao.ListOfMedicalServiceDAO;
import dao.ServiceOrderDAO;
import dao.ServiceOrderItemDAO;
import dto.ServiceOrderDTO;
import dto.ServiceOrderItemDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AccountStaff;
import model.Doctor;
import model.ListOfMedicalService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/doctor/service-order")
public class DoctorServiceOrderServlet extends HttpServlet {
    
    private final ServiceOrderDAO serviceOrderDAO = new ServiceOrderDAO();
    private final ServiceOrderItemDAO serviceOrderItemDAO = new ServiceOrderItemDAO();
    private final ListOfMedicalServiceDAO medicalServiceDAO = new ListOfMedicalServiceDAO();
    private final AccountStaffDAO accountStaffDAO = new AccountStaffDAO();
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> jsonResponse = new HashMap<>();
        
        try {
            // Kiểm tra session
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Unauthorized access");
                mapper.writeValue(response.getWriter(), jsonResponse);
                return;
            }
            
            String action = request.getParameter("action");
            
            if ("getServices".equals(action)) {
                // Lấy danh sách tất cả dịch vụ y tế
                List<ListOfMedicalService> services = medicalServiceDAO.getAllServices();
                jsonResponse.put("success", true);
                jsonResponse.put("data", services);
                
            } else if ("getServiceOrder".equals(action)) {
                // Lấy chi tiết service order
                String serviceOrderIdStr = request.getParameter("serviceOrderId");
                if (serviceOrderIdStr != null) {
                    int serviceOrderId = Integer.parseInt(serviceOrderIdStr);
                    // TODO: Implement getServiceOrderWithDetails
                    jsonResponse.put("success", true);
                    jsonResponse.put("message", "Service order details retrieved");
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Missing serviceOrderId parameter");
                }
                
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Invalid action");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Internal server error: " + e.getMessage());
        }
        
        mapper.writeValue(response.getWriter(), jsonResponse);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> jsonResponse = new HashMap<>();
        
        try {
            // Kiểm tra session và quyền bác sĩ
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Unauthorized access");
                mapper.writeValue(response.getWriter(), jsonResponse);
                return;
            }
            
            AccountStaff accountStaff = (AccountStaff) session.getAttribute("user");
            if (!"Doctor".equals(accountStaff.getRole())) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Access denied. Doctor role required.");
                mapper.writeValue(response.getWriter(), jsonResponse);
                return;
            }
            
            // Đọc dữ liệu từ request
            Map<String, Object> requestData = mapper.readValue(request.getReader(), Map.class);
            
            // Lấy thông tin từ request
            Integer medicineRecordId = (Integer) requestData.get("medicineRecordId");
            @SuppressWarnings("unchecked")
            List<Integer> serviceIds = (List<Integer>) requestData.get("serviceIds");
            
            // Validation
            if (medicineRecordId == null || serviceIds == null || serviceIds.isEmpty()) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Missing required fields: medicineRecordId and serviceIds");
                mapper.writeValue(response.getWriter(), jsonResponse);
                return;
            }
            
            // Lấy doctor_id từ session
            Doctor doctor = (Doctor) accountStaffDAO.getOStaffByStaffId(accountStaff.getAccount_staff_id(), "Doctor");
            if (doctor == null) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Failed to get doctor information");
                mapper.writeValue(response.getWriter(), jsonResponse);
                return;
            }
            
            int doctorId = doctor.getDoctor_id();
            
            // Tạo service order
            int serviceOrderId = serviceOrderDAO.createServiceOrder(doctorId, medicineRecordId);
            if (serviceOrderId == -1) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Failed to create service order");
                mapper.writeValue(response.getWriter(), jsonResponse);
                return;
            }
            
            // Tạo service order items
            boolean success = serviceOrderItemDAO.createMultipleServiceOrderItems(serviceOrderId, doctorId, serviceIds);
            
            if (success) {
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Service order created successfully");
                jsonResponse.put("serviceOrderId", serviceOrderId);
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Failed to create service order items");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Internal server error: " + e.getMessage());
        }
        
        mapper.writeValue(response.getWriter(), jsonResponse);
    }
} 
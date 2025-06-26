package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.AccountStaffDAO;
import dao.ListOfMedicalServiceDAO;
import dao.ServiceOrderDAO;
import dao.ServiceOrderItemDAO;
import dao.WaitlistDAO;
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
import model.ServiceOrder;
import model.ServiceOrderItem;
import dto.WaitlistDTO;

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
    private final WaitlistDAO waitlistDAO = new WaitlistDAO();
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
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
            
            String action = request.getParameter("action");
            
            if ("getServices".equals(action)) {
                // Lấy danh sách tất cả services
                List<ListOfMedicalService> services = medicalServiceDAO.getAllServices();
                
                jsonResponse.put("success", true);
                jsonResponse.put("data", services);
                
            } else if ("getServiceOrder".equals(action)) {
                // Lấy chi tiết service order
                String serviceOrderIdParam = request.getParameter("serviceOrderId");
                if (serviceOrderIdParam == null || serviceOrderIdParam.trim().isEmpty()) {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Service order ID is required");
                    mapper.writeValue(response.getWriter(), jsonResponse);
                    return;
                }
                
                int serviceOrderId = Integer.parseInt(serviceOrderIdParam);
                Map<String, Object> serviceOrderDetails = serviceOrderDAO.getServiceOrderWithDetails(serviceOrderId);
                
                if (serviceOrderDetails == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Service order not found");
                    mapper.writeValue(response.getWriter(), jsonResponse);
                    return;
                }
                
                List<Map<String, Object>> items = serviceOrderItemDAO.getServiceOrderItemsWithDetails(serviceOrderId);
                double totalAmount = items.stream()
                    .mapToDouble(item -> (Double) item.get("service_price"))
                    .sum();
                
                Map<String, Object> result = new HashMap<>();
                result.put("serviceOrder", serviceOrderDetails);
                result.put("items", items);
                result.put("totalAmount", totalAmount);
                
                jsonResponse.put("success", true);
                jsonResponse.put("data", result);
                
            } else if ("getServiceOrdersByMedicineRecord".equals(action)) {
                // Lấy danh sách service orders theo medicine record
                String medicineRecordIdParam = request.getParameter("medicineRecordId");
                if (medicineRecordIdParam == null || medicineRecordIdParam.trim().isEmpty()) {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Medicine record ID is required");
                    mapper.writeValue(response.getWriter(), jsonResponse);
                    return;
                }
                
                int medicineRecordId = Integer.parseInt(medicineRecordIdParam);
                List<ServiceOrder> serviceOrders = serviceOrderDAO.getServiceOrdersByMedicineRecordId(medicineRecordId);
                
                jsonResponse.put("success", true);
                jsonResponse.put("data", serviceOrders);
                
            } else if ("getServiceOrdersByDoctor".equals(action)) {
                // Lấy danh sách service orders của bác sĩ hiện tại
                Doctor doctor = (Doctor) accountStaffDAO.getOStaffByStaffId(accountStaff.getAccount_staff_id(), "Doctor");
                if (doctor == null) {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Failed to get doctor information");
                    mapper.writeValue(response.getWriter(), jsonResponse);
                    return;
                }
                
                List<ServiceOrder> serviceOrders = serviceOrderDAO.getServiceOrdersByDoctorId(doctor.getDoctor_id());
                
                jsonResponse.put("success", true);
                jsonResponse.put("data", serviceOrders);
                
            } else if ("getAssignedServices".equals(action)) {
                // Lấy danh sách services được giao cho bác sĩ hiện tại
                Doctor doctor = (Doctor) accountStaffDAO.getOStaffByStaffId(accountStaff.getAccount_staff_id(), "Doctor");
                if (doctor == null) {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Failed to get doctor information");
                    mapper.writeValue(response.getWriter(), jsonResponse);
                    return;
                }
                
                List<Map<String, Object>> assignedServices = serviceOrderItemDAO.getAssignedServicesByDoctorId(doctor.getDoctor_id());
                
                jsonResponse.put("success", true);
                jsonResponse.put("data", assignedServices);
                
            } else if ("getDoctors".equals(action)) {
                // Lấy danh sách tất cả bác sĩ
                List<Doctor> doctors = accountStaffDAO.getAllDoctors();
                
                jsonResponse.put("success", true);
                jsonResponse.put("data", doctors);
                
            } else if ("getServiceOrderWaitlist".equals(action)) {
                // Lấy danh sách waitlist có status = InProgress và visittype = Initial cho bác sĩ hiện tại
                Doctor doctor = (Doctor) accountStaffDAO.getOStaffByStaffId(accountStaff.getAccount_staff_id(), "Doctor");
                if (doctor == null) {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Failed to get doctor information");
                    mapper.writeValue(response.getWriter(), jsonResponse);
                    return;
                }
                
                List<WaitlistDTO> serviceOrderWaitlist = waitlistDAO.getServiceOrderWaitlist(doctor.getDoctor_id());
                
                jsonResponse.put("success", true);
                jsonResponse.put("data", serviceOrderWaitlist);
                
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
            List<Map<String, Object>> services = (List<Map<String, Object>>) requestData.get("services");
            
            // Validation
            if (medicineRecordId == null || services == null || services.isEmpty()) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Missing required fields");
                mapper.writeValue(response.getWriter(), jsonResponse);
                return;
            }
            
            // Lấy thông tin bác sĩ hiện tại
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
            
            // Chuẩn bị danh sách service với doctor_id được chỉ định
            List<ServiceOrderItem> serviceOrderItems = new ArrayList<>();
            for (Map<String, Object> serviceData : services) {
                Integer serviceId = (Integer) serviceData.get("serviceId");
                Integer assignedDoctorId = (Integer) serviceData.get("doctorId");
                
                if (serviceId != null && assignedDoctorId != null) {
                    ServiceOrderItem item = new ServiceOrderItem();
                    item.setService_order_id(serviceOrderId);
                    item.setService_id(serviceId);
                    item.setDoctor_id(assignedDoctorId);
                    serviceOrderItems.add(item);
                }
            }
            
            // Tạo service order items
            boolean success = serviceOrderItemDAO.createMultipleServiceOrderItemsWithDoctors(serviceOrderItems);
            
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
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
import model.ServiceOrder;

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
                    
                    // Lấy thông tin service order với chi tiết
                    Map<String, Object> serviceOrderDetails = serviceOrderDAO.getServiceOrderWithDetails(serviceOrderId);
                    
                    if (serviceOrderDetails != null) {
                        // Lấy danh sách service order items với chi tiết
                        List<Map<String, Object>> serviceOrderItems = serviceOrderItemDAO.getServiceOrderItemsWithDetails(serviceOrderId);
                        
                        // Tính tổng tiền
                        double totalAmount = 0.0;
                        for (Map<String, Object> item : serviceOrderItems) {
                            totalAmount += (Double) item.get("service_price");
                        }
                        
                        // Tạo response object
                        Map<String, Object> responseData = new HashMap<>();
                        responseData.put("serviceOrder", serviceOrderDetails);
                        responseData.put("items", serviceOrderItems);
                        responseData.put("totalAmount", totalAmount);
                        
                        jsonResponse.put("success", true);
                        jsonResponse.put("data", responseData);
                        jsonResponse.put("message", "Service order details retrieved successfully");
                    } else {
                        jsonResponse.put("success", false);
                        jsonResponse.put("message", "Service order not found");
                    }
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Missing serviceOrderId parameter");
                }
                
            } else if ("getServiceOrdersByMedicineRecord".equals(action)) {
                // Lấy danh sách service orders theo medicine record ID
                String medicineRecordIdStr = request.getParameter("medicineRecordId");
                if (medicineRecordIdStr != null) {
                    int medicineRecordId = Integer.parseInt(medicineRecordIdStr);
                    
                    List<ServiceOrder> serviceOrders = serviceOrderDAO.getServiceOrdersByMedicineRecordId(medicineRecordId);
                    List<Map<String, Object>> serviceOrdersWithDetails = new ArrayList<>();
                    
                    for (ServiceOrder serviceOrder : serviceOrders) {
                        Map<String, Object> orderDetails = serviceOrderDAO.getServiceOrderWithDetails(serviceOrder.getService_order_id());
                        if (orderDetails != null) {
                            List<Map<String, Object>> items = serviceOrderItemDAO.getServiceOrderItemsWithDetails(serviceOrder.getService_order_id());
                            
                            // Tính tổng tiền cho order này
                            double totalAmount = 0.0;
                            for (Map<String, Object> item : items) {
                                totalAmount += (Double) item.get("service_price");
                            }
                            
                            orderDetails.put("items", items);
                            orderDetails.put("totalAmount", totalAmount);
                            serviceOrdersWithDetails.add(orderDetails);
                        }
                    }
                    
                    jsonResponse.put("success", true);
                    jsonResponse.put("data", serviceOrdersWithDetails);
                    jsonResponse.put("message", "Service orders retrieved successfully");
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Missing medicineRecordId parameter");
                }
                
            } else if ("getServiceOrdersByDoctor".equals(action)) {
                // Lấy danh sách service orders theo doctor ID
                AccountStaff accountStaff = (AccountStaff) session.getAttribute("user");
                Doctor doctor = (Doctor) accountStaffDAO.getOStaffByStaffId(accountStaff.getAccount_staff_id(), "Doctor");
                
                if (doctor != null) {
                    List<ServiceOrder> serviceOrders = serviceOrderDAO.getServiceOrdersByDoctorId(doctor.getDoctor_id());
                    List<Map<String, Object>> serviceOrdersWithDetails = new ArrayList<>();
                    
                    for (ServiceOrder serviceOrder : serviceOrders) {
                        Map<String, Object> orderDetails = serviceOrderDAO.getServiceOrderWithDetails(serviceOrder.getService_order_id());
                        if (orderDetails != null) {
                            List<Map<String, Object>> items = serviceOrderItemDAO.getServiceOrderItemsWithDetails(serviceOrder.getService_order_id());
                            
                            // Tính tổng tiền cho order này
                            double totalAmount = 0.0;
                            for (Map<String, Object> item : items) {
                                totalAmount += (Double) item.get("service_price");
                            }
                            
                            orderDetails.put("items", items);
                            orderDetails.put("totalAmount", totalAmount);
                            serviceOrdersWithDetails.add(orderDetails);
                        }
                    }
                    
                    jsonResponse.put("success", true);
                    jsonResponse.put("data", serviceOrdersWithDetails);
                    jsonResponse.put("message", "Service orders retrieved successfully");
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Failed to get doctor information");
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
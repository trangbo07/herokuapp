package controller;

import dao.AccountPatientDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
@MultipartConfig
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("view/login.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String loginId = request.getParameter("loginId"); // Có thể là email hoặc username
        String password = request.getParameter("password");

        // Kiểm tra dữ liệu đầu vào
        if (loginId == null || password == null || 
            loginId.trim().isEmpty() || password.trim().isEmpty()) {
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"Vui lòng điền đầy đủ thông tin\"}");
            return;
        }

        // Kiểm tra xem loginId là email hay username
        boolean isEmail = loginId.contains("@");
        
        // Đăng nhập
        boolean success;
        if (isEmail) {
            success = AccountPatientDAO.loginWithEmail(loginId, password);
        } else {
            success = AccountPatientDAO.loginWithUsername(loginId, password);
        }

        // Trả về kết quả dạng JSON
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        if (success) {
            // Tạo session cho user
            HttpSession session = request.getSession();
            session.setAttribute("loginId", loginId);
            
            out.print("{\"success\": true, \"message\": \"Đăng nhập thành công\"}");
        } else {
            out.print("{\"success\": false, \"message\": \"Email/Username hoặc mật khẩu không đúng\"}");
        }
    }
}


package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.AccountPatientDAO;
import dto.JsonResponse;
import dto.RegisterRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AccountPatient;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();
    private final AccountPatientDAO accountPatientDAO = new AccountPatientDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");


        RegisterRequest registerRequest = mapper.readValue(request.getReader(), RegisterRequest.class);
        String username = registerRequest.username;
        String email = registerRequest.email;
        String password = registerRequest.password;
        String re_password = registerRequest.re_password;

        JsonResponse jsonResponse;


        if (!password.equals(re_password)) {
            jsonResponse = new JsonResponse(false, "Mật khẩu xác nhận không khớp.");
            mapper.writeValue(response.getWriter(), jsonResponse);
            return;
        }


        if (accountPatientDAO.getAccountByEmailOrUsername(username) != null ||
            accountPatientDAO.getAccountByEmailOrUsername(email) != null) {
            jsonResponse = new JsonResponse(false, "Tên đăng nhập hoặc email đã tồn tại.");
            mapper.writeValue(response.getWriter(), jsonResponse);
            return;
        }


        boolean created = accountPatientDAO.registerPatient(username, email, password, "https://jbagy.me/wp-content/uploads/2025/03/Hinh-nen-don-gian-dang-yeu-cho-nu-4.png", "Enable");

        if (created) {
            jsonResponse = new JsonResponse(true, "Đăng ký thành công", "login.html");
        } else {
            jsonResponse = new JsonResponse(false, "Đăng ký thất bại, vui lòng thử lại.");
        }


        mapper.writeValue(response.getWriter(), jsonResponse);
    }
}

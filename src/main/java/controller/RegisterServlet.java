package controller;

import dao.AccountPatientDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // Parse JSON (dùng thư viện nếu cần)
        JSONObject json = new JSONObject(sb.toString());
        String email = json.getString("email");
        String password = json.getString("password");
        String username = json.getString("username");

        boolean success = AccountPatientDAO.registerPatient(username, email, password, "Enable");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        if (success) {
            out.print("{\"success\": true}");
        } else {
            out.print("{\"success\": false, \"message\": \"Email đã tồn tại\"}");
        }
    }
}

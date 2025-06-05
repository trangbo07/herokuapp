package controller;

import dao.AccountDAO;
import model.AccountStaff;
import model.AccountPharmacist;
import model.AccountPatient;
import dao.DBContext;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final AccountDAO accountDAO = new AccountDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try (Connection conn = DBContext.getInstance().getConnection()) {

            Object account = accountDAO.checkLogin(conn, username, password);

            if (account != null) {
                HttpSession session = request.getSession();
                session.setAttribute("user", account);

                // Điều hướng theo loại tài khoản
                if (account instanceof AccountStaff) {

                    out.println("thành công vào trang admin");
//                    response.sendRedirect("admin/dashboard.jsp");
                } else if (account instanceof AccountPharmacist) {
                    out.println("thành công vào trang dược sĩ");
                  //  response.sendRedirect("pharmacist/home.jsp");
                } else if (account instanceof AccountPatient) {
                    out.println("thành công vào trang bệnh nhân");
                   // response.sendRedirect("patient/home.jsp");
                } else {
                    out.println("lỗi");
                    //response.sendRedirect("login.jsp?error=unknownrole");
                }

            } else {
                out.println("lỗi");
            }

        } catch (Exception e) {
            out.println("lỗi");
        }
    }
}

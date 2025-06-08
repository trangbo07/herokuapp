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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chuyển hướng sang trang login.html khi có GET request
        request.getRequestDispatcher("view/login.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String username = request.getParameter("username");
        String password = request.getParameter("password");

            Object account = accountDAO.checkLogin(username, password);

            if (account != null) {
                HttpSession session = request.getSession();
                session.setAttribute("user", account);

                // Điều hướng theo loại tài khoản
                if (account instanceof AccountStaff) {
                    response.sendRedirect("home");
                } else if (account instanceof AccountPharmacist) {
                    response.sendRedirect("home");
                } else if (account instanceof AccountPatient) {
                    response.sendRedirect("home");
                } else {
                    request.getRequestDispatcher("view/login.html").forward(request, response);
                }

            } else {
                System.out.println(account.toString());
                out.println("lỗi 1");
            }
    }
}

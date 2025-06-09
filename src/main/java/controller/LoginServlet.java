package controller;

import dao.AccountDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import model.AccountPatient;
import model.AccountPharmacist;
import model.AccountStaff;

import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
@MultipartConfig
public class LoginServlet extends HttpServlet {
    AccountDAO accountDAO = new AccountDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("view/login.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Object account = accountDAO.checkLogin(username, password);

        if (account != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", account);

            // Điều hướng theo role
            if (account instanceof AccountStaff) {
                response.sendRedirect(request.getContextPath() + "/staff");
            } else if (account instanceof AccountPharmacist) {
                response.sendRedirect(request.getContextPath() + "/pharmacist");
            } else if (account instanceof AccountPatient) {
                response.sendRedirect(request.getContextPath() + "/patient");
            } else {
                response.sendRedirect(request.getContextPath() + "/login?error=invalidrole");
            }
        } else {
            response.sendRedirect("view/login.html?error=1");
        }
    }
}


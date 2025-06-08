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

@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("view/index.html");
            return;
        }

        Object user = session.getAttribute("user");
        if (user instanceof AccountStaff) {
            AccountStaff staff = (AccountStaff) user;
            request.getRequestDispatcher("view/home_staff.html").forward(request, response);
        } else if (user instanceof AccountPharmacist) {
            AccountPharmacist pharmacist = (AccountPharmacist) user;
            request.getRequestDispatcher("view/home_pharmacist.html").forward(request, response);
        } else if (user instanceof AccountPatient) {
            AccountPatient patient = (AccountPatient) user;
            request.getRequestDispatcher("view/home_patient.html").forward(request, response);
        } else {
            request.getSession().invalidate();
            request.getRequestDispatcher("view/index.html").forward(request, response);
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }
}

package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/pharmacist/*")
public class PharmacistServlet extends HttpServlet {
    private final Map<String, String> pageRoutes = new HashMap<>();

    @Override
    public void init() {
        pageRoutes.put("/home", "/view/pharmacist/home_pharmacist.html");
        // Thêm path khác nếu cần
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();

        if (path == null || path.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/pharmacist/home");
            return;
        }

        if (path.equals("/logout")) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate(); // Hủy session
            }
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        String view = pageRoutes.get(path);
        if (view != null) {
            request.getRequestDispatcher(view).forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy trang: " + path);
        }
    }

}

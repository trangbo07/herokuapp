package controller;

import com.google.gson.Gson;
import dao.AdminSystemDAO;

import dto.DistinctResponse;
import dto.DoctorDTOFA;
import dto.JsonResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import util.NormalizeUtil;

import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/admin/doctors")
public class AdminSys4DoctorServlet extends HttpServlet {

    private final AdminSystemDAO dao = new AdminSystemDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            JsonResponse res = new JsonResponse(false, "Unauthorized", "/view/home.html");
            out.print(new Gson().toJson(res));
            return;
        }

        try {
            String action = req.getParameter("action");
            if (action == null) action = ""; // Dùng "" làm mặc định nếu không có action

            switch (action) {
                case "" -> { // Mặc định: lấy toàn bộ bác sĩ
                    List<DoctorDTOFA> doctors = dao.getAllDoctors();
                    out.print(gson.toJson(doctors));
                }

                case "distinct" -> {
                    String field = req.getParameter("field");
                    List<String> values = dao.getDistinctValues(field);
                    DistinctResponse res = new DistinctResponse(field, values);
                    out.print(gson.toJson(res));
                }

                case "view" -> {
                    int id = Integer.parseInt(req.getParameter("id"));
                    DoctorDTOFA doctor = dao.getDoctorById(id);
                    out.print(gson.toJson(doctor));
                }

                case "filter" -> {
                    String status = req.getParameter("status");
                    String eduLevel = req.getParameter("eduLevel");
                    String department = req.getParameter("department");
                    String search = NormalizeUtil.normalizeKeyword(req.getParameter("search"));

                    List<DoctorDTOFA> filteredDoctors = dao.filterDoctors(status, eduLevel, department, search);
                    out.print(gson.toJson(filteredDoctors));
                }

                case "delete" -> {
                    int id = Integer.parseInt(req.getParameter("id"));
//                    boolean success = dao.deleteDoctor(id);
//                    JsonResponse res = new JsonResponse(success, success ? "Doctor deleted." : "Delete failed.");
//                    out.print(gson.toJson(res));
                }

                case "search" -> {
//                    String keyword = req.getParameter("keyword");
//                    List<DoctorDTOFA> results = dao.searchDoctors(keyword);
//                    out.print(gson.toJson(results));
                }

                default -> {
                    JsonResponse res = new JsonResponse(false, "Unknown action: " + action);
                    out.print(gson.toJson(res));
                }
            }

            out.flush();
        } catch (NumberFormatException e) {
            JsonResponse res = new JsonResponse(false, "Invalid ID format");
            out.print(gson.toJson(res));
        } catch (Exception e) {
            e.printStackTrace();
            JsonResponse res = new JsonResponse(false, "Server error");
            out.print(gson.toJson(res));
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            String action = req.getParameter("action");
            if (action == null) action = ""; // Mặc định là rỗng nếu không có action

            DoctorDTOFA dto = gson.fromJson(req.getReader(), DoctorDTOFA.class);
            boolean success;

            switch (action) {
                case "create" -> {
//                    success = dao.createDoctor(dto);
//                    JsonResponse res = new JsonResponse(success, success ? "Doctor created successfully." : "Doctor creation failed.");
//                    out.print(gson.toJson(res));
                }

                case "update" -> {
//                    success = dao.updateDoctor(dto);
//                    JsonResponse res = new JsonResponse(success, success ? "Doctor updated successfully." : "Doctor update failed.");
//                    out.print(gson.toJson(res));
                }

                default -> {
                    JsonResponse res = new JsonResponse(false, "Unknown or missing action.");
                    out.print(gson.toJson(res));
                }
            }

            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            JsonResponse res = new JsonResponse(false, "Invalid JSON or server error");
            out.print(gson.toJson(res));
        }
    }

}


package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DoctorDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import dto.DoctorDTO;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/doctors")
public class DoctorListServlet extends HttpServlet {
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<DoctorDTO> list = doctorDAO.getAllDoctorDTOs();

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), list);
    }
}

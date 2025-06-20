package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.MedicineRecordDAO;
import dto.RecordSummaryDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.AccountPatient;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/records/summary")
public class RecordSummaryServlet extends HttpServlet {

    private final MedicineRecordDAO dao = new MedicineRecordDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        AccountPatient account = (AccountPatient) session.getAttribute("user");
        int accountPatientId = account.getAccount_patient_id();

        List<RecordSummaryDTO> list = dao.getSummaryByPatientId(accountPatientId);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), list);
    }
}

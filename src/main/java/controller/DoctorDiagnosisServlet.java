package controller;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonToken; // Add this import
import dao.DiagnosisDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AccountStaff;
import model.DiagnosisDetails;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/api/doctor/diagnosis")
public class DoctorDiagnosisServlet extends HttpServlet {

    // Custom TypeAdapter for LocalDate
    private static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(formatter.format(value));
            }
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String date = in.nextString();
            return LocalDate.parse(date, formatter);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        DiagnosisDAO diagnosisDAO = new DiagnosisDAO();

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/view/home.html");
            return;
        }

        AccountStaff user = (AccountStaff) session.getAttribute("user");

        List<DiagnosisDetails> diagnosisList = diagnosisDAO.getDiagnosisDetailsByDoctorID(user.getAccount_staff_id());

        if (diagnosisList == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"No diagnosis records found\"}");
            return;
        }

        // Create Gson instance with custom LocalDate adapter
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        String json = gson.toJson(diagnosisList);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }
}
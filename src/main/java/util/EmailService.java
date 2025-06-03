package util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class EmailService {
    // Dán API Key mới vào đây DF564NVA9JJFGYGMXZF41G28
    private static final String API_KEY = System.getenv("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");

    public static void sendEmail(String toEmail, String subject, String body) throws IOException {
        System.out.println("[DEBUG] Preparing to send email to: " + toEmail);
        String url = "https://api.sendgrid.com/v3/mail/send";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Bearer " + API_KEY);
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        String jsonPayload = "{"
                + "\"personalizations\": [{"
                + "\"to\": [{\"email\": \"" + toEmail + "\"}]"
                + "}],"
                + "\"from\": {\"email\": \"qpnguyenhuukhai@gmail.com\"},"
                + "\"reply_to\": {\"email\": \"qpnguyenhuukhai@gmail.com\"},"
                + "\"subject\": \"" + subject + "\","
                + "\"content\": [{\"type\": \"text/plain\", \"value\": \"" + body.replace("\n", "\\n").replace("\"", "\\\"") + "\"}]"
                + "}";
        System.out.println("[DEBUG] JSON Payload: " + jsonPayload);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonPayload.getBytes("utf-8");
            os.write(input, 0, input.length);
            System.out.println("[DEBUG] Payload sent to SendGrid");
        }

        int responseCode = con.getResponseCode();
        System.out.println("[DEBUG] SendGrid Response Code: " + responseCode);

        if (responseCode == 202) {
            System.out.println("[DEBUG] Email sent successfully to: " + toEmail);
        } else {
            System.err.println("[ERROR] Failed to send email. Response Code: " + responseCode);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream()))) {
                String line;
                StringBuilder errorDetails = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    errorDetails.append(line).append("\n");
                }
                System.err.println("[ERROR] SendGrid Error Details: " + errorDetails.toString());
            }
            throw new IOException("SendGrid API returned error code: " + responseCode);
        }
    }
}
package util;

import jakarta.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VnPayConfig {
    private static final Properties properties = new Properties();

    public static void loadConfig(ServletContext context) {
        if (properties.isEmpty()) {
            try (InputStream input = context.getResourceAsStream("/WEB-INF/vnpay.properties")) {
                if (input != null) {
                    properties.load(input);
                } else {
                    throw new RuntimeException("Cannot find vnpay.properties");
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to load vnpay.properties", e);
            }
        }
    }

    public static String getTmnCode() {
        return properties.getProperty("vnp_TmnCode");
    }
    public static String getHashSecret() {
        return properties.getProperty("vnp_HashSecret");
    }
    public static String getPayUrl() {
        return properties.getProperty("vnp_Url");
    }
    public static String getReturnUrl() {
        return properties.getProperty("vnp_Returnurl");
    }
} 
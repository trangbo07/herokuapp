package util;

public class NormalizeUtil {
    public static String normalizeKeyword(String input) {
        if (input == null) return "";
        input = input.trim().replaceAll("\\s+", " "); // chỉ rút gọn khoảng trắng về 1
        String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase(); // bỏ dấu + lowercase
    }
}

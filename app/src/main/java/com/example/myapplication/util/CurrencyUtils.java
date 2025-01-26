package com.example.myapplication.util;

import java.text.DecimalFormat;
import java.text.Normalizer;

public class CurrencyUtils {

    /**
     * Format số thành định dạng tiền tệ Việt Nam, thêm "đ" ở cuối.
     * @param amount Số tiền cần format (đơn vị: VNĐ)
     * @return Chuỗi định dạng, ví dụ: "19.000đ"
     */
    public static String formatCurrency(int amount) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(amount) + "đ";
    }

    public static String normalizeString(String input) {
        if (input == null) return null;

        // Loại bỏ dấu tiếng Việt và chuyển về chữ thường
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", ""); // Loại bỏ các ký tự dấu
        return normalized.toLowerCase();
    }

}

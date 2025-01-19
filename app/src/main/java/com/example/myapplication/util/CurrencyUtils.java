package com.example.myapplication.util;

import java.text.DecimalFormat;

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
}

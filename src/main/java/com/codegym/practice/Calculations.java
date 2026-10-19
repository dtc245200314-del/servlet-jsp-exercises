package com.codegym.practice;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Locale;
import java.util.Map;

public final class Calculations {
    private Calculations() {}
    private static final Map<String,String> WORDS = Map.of("hello","Xin chào", "book","Quyển sách",
        "computer","Máy tính", "student","Sinh viên", "how","Thế nào");
    public static BigDecimal number(String value) {
        if (value == null || value.isBlank() || value.length() > 50)
            throw new IllegalArgumentException("Vui lòng nhập số hợp lệ.");
        try {
            BigDecimal n = new BigDecimal(value.trim());
            if (n.precision() > 30 || Math.abs((long)n.scale()) > 18 || n.abs().compareTo(new BigDecimal("1000000000000")) > 0)
                throw new IllegalArgumentException("Số vượt phạm vi hỗ trợ.");
            return n;
        } catch (NumberFormatException ex) { throw new IllegalArgumentException("Vui lòng nhập số hợp lệ."); }
    }
    public static BigDecimal nonNegative(String value) {
        BigDecimal n = number(value);
        if (n.signum() < 0) throw new IllegalArgumentException("Giá trị không được âm.");
        return n;
    }
    public static BigDecimal currency(String rate, String usd) { return nonNegative(rate).multiply(nonNegative(usd)); }
    public static BigDecimal discount(BigDecimal price, BigDecimal percent) {
        if (price.signum()<0 || percent.signum()<0 || percent.compareTo(new BigDecimal("100"))>0)
            throw new IllegalArgumentException("Giá không âm; phần trăm phải từ 0 đến 100.");
        return price.multiply(percent).movePointLeft(2);
    }
    public static BigDecimal calculate(BigDecimal left, BigDecimal right, String operator) {
        if (operator == null) throw new IllegalArgumentException("Thiếu phép toán.");
        return switch (operator) {
            case "+" -> left.add(right);
            case "-" -> left.subtract(right);
            case "*" -> left.multiply(right);
            case "/" -> {
                if (right.signum() == 0) throw new IllegalArgumentException("Không thể chia cho 0.");
                yield left.divide(right, MathContext.DECIMAL64);
            }
            default -> throw new IllegalArgumentException("Phép toán không hợp lệ.");
        };
    }
    public static String translate(String word) {
        return word == null ? null : WORDS.get(word.trim().toLowerCase(Locale.ROOT));
    }
}

package pro1041.utils;

import java.text.DecimalFormat;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public class MoneyFormat {

    private static final DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

    public static String formatVND(Object value) {
        return decimalFormat.format(value) + " VNĐ";
    }
    
     public static String format(Object value) {
        return decimalFormat.format(value);
    }
}

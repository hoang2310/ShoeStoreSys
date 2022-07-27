package pro1041.entity;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public enum PaymentMethod {
    
    CASH("Tiền Mặt"), BANKING("Thẻ / Chuyển Khoản");

    private final String value;

    private PaymentMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}

package pro1041.entity;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public enum Status {
    
    ORDERED("Đã Đặt"), TOSHIP("Đang Giao"), CANCELLED("Đã Hủy"), RECEIVED("Đã Nhận");

    private final String value;

    private Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}

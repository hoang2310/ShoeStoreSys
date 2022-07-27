package pro1041.entity;

import java.sql.Timestamp;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public class Bill {

    private int id;
    private Timestamp createdTime;
    private PaymentMethod paymentMethod;
    private int employeeID;
    private int orderID;

    public Bill() {
    }

    public Bill(int id, Timestamp createdTime, PaymentMethod paymentMethod, int employeeID, int orderID) {
        this.id = id;
        this.createdTime = createdTime;
        this.paymentMethod = paymentMethod;
        this.employeeID = employeeID;
        this.orderID = orderID;
    }

    public Bill(Timestamp createdTime, PaymentMethod paymentMethod, int employeeID, int orderID) {
        this.createdTime = createdTime;
        this.paymentMethod = paymentMethod;
        this.employeeID = employeeID;
        this.orderID = orderID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

}

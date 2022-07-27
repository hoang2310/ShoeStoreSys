package pro1041.entity;

import java.sql.Timestamp;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public class Delivery {

    private int id;
    private String customerName;
    private String email;
    private String phoneNumber;
    private String address;
    private String note;
    private Status status;
    private Timestamp updateTime;
    private int orderID;

    public Delivery() {
    }

    public Delivery(int id, String customerName, String email, String phoneNumber, String address, String note, Status status, Timestamp updateTime, int orderID) {
        this.id = id;
        this.customerName = customerName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.note = note;
        this.status = status;
        this.updateTime = updateTime;
        this.orderID = orderID;
    }

    public Delivery(String customerName, String email, String phoneNumber, String address, String note, Status status, Timestamp updateTime, int orderID) {
        this.customerName = customerName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.note = note;
        this.status = status;
        this.updateTime = updateTime;
        this.orderID = orderID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

}

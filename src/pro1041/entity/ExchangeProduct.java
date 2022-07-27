package pro1041.entity;

import java.sql.Timestamp;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public class ExchangeProduct {

    private int id;
    private Timestamp createdTime;
    private String note;
    private int productDetailReturnID;
    private int productDetailExchangeID;
    private int billID;
    private int employeeID;

    public ExchangeProduct() {
    }

    public ExchangeProduct(int id, Timestamp createdTime, String note, int productDetailReturnID, int productDetailExchangeID, int billID, int employeeID) {
        this.id = id;
        this.createdTime = createdTime;
        this.note = note;
        this.productDetailReturnID = productDetailReturnID;
        this.productDetailExchangeID = productDetailExchangeID;
        this.billID = billID;
        this.employeeID = employeeID;
    }

    public ExchangeProduct(Timestamp createdTime, String note, int productDetailReturnID, int productDetailExchangeID, int billID, int employeeID) {
        this.createdTime = createdTime;
        this.note = note;
        this.productDetailReturnID = productDetailReturnID;
        this.productDetailExchangeID = productDetailExchangeID;
        this.billID = billID;
        this.employeeID = employeeID;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getProductDetailReturnID() {
        return productDetailReturnID;
    }

    public void setProductDetailReturnID(int productDetailReturnID) {
        this.productDetailReturnID = productDetailReturnID;
    }

    public int getProductDetailExchangeID() {
        return productDetailExchangeID;
    }

    public void setProductDetailExchangeID(int productDetailExchangeID) {
        this.productDetailExchangeID = productDetailExchangeID;
    }

    public int getBillID() {
        return billID;
    }

    public void setBillID(int billID) {
        this.billID = billID;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

}

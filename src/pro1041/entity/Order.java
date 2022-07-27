package pro1041.entity;

import java.sql.Timestamp;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public class Order {

    private int id;
    private Timestamp createdTime;
    private int employeeID;

    public Order() {
    }

    public Order(int id, Timestamp createdTime, int employeeID) {
        this.id = id;
        this.createdTime = createdTime;
        this.employeeID = employeeID;
    }

    public Order(Timestamp createdTime, int employeeID) {
        this.createdTime = createdTime;
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

    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

}

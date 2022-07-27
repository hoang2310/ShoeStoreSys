package pro1041.entity;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public class OrderDetail {

    private int id;
    private int quantity;
    private double price;
    private int productDetailID;
    private int orderID;

    public OrderDetail() {
    }

    public OrderDetail(int id, int quantity, double price, int productDetailID, int orderID) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
        this.productDetailID = productDetailID;
        this.orderID = orderID;
    }

    public OrderDetail(int quantity, double price, int productDetailID, int orderID) {
        this.quantity = quantity;
        this.price = price;
        this.productDetailID = productDetailID;
        this.orderID = orderID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getProductDetailID() {
        return productDetailID;
    }

    public void setProductDetailID(int productDetailID) {
        this.productDetailID = productDetailID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

}

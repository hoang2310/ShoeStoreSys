package pro1041.entity;

import java.sql.Date;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public class ProductDetail {

    private int id;
    private float size;
    private double price;
    private Type type;
    private int quantity;
    private Date importDate;
    private String image;
    private String barcode;
    private String productID;

    public ProductDetail() {
    }

    public ProductDetail(int id, float size, double price, Type type, int quantity, Date importDate, String image, String barcode, String productID) {
        this.id = id;
        this.size = size;
        this.price = price;
        this.type = type;
        this.quantity = quantity;
        this.importDate = importDate;
        this.image = image;
        this.barcode = barcode;
        this.productID = productID;
    }

    public ProductDetail(float size, double price, Type type, int quantity, Date importDate, String image, String barcode, String productID) {
        this.size = size;
        this.price = price;
        this.type = type;
        this.quantity = quantity;
        this.importDate = importDate;
        this.image = image;
        this.barcode = barcode;
        this.productID = productID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getImportDate() {
        return importDate;
    }

    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

}

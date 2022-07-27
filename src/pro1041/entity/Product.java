package pro1041.entity;

public class Product {

    private String id;
    private String name;
    private String description;
    private boolean activate;
    private int brandID;

    public Product() {
    }

    public Product(String id, String name, String description, boolean activate, int brandID) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.activate = activate;
        this.brandID = brandID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActivate() {
        return activate;
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
    }

    public int getBrandID() {
        return brandID;
    }

    public void setBrandID(int brandID) {
        this.brandID = brandID;
    }

}

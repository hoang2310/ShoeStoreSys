package pro1041.entity;

public class Employee {

    private int id;
    private String username;
    private String password;
    private String fullname;
    private Role role;
    private boolean gender;
    private String email;
    private String phoneNumber;
    private String address;
    private boolean activate;

    public Employee() {
    }

    public Employee(int id, String username, String password, String fullname, Role role, boolean gender, String email, String phoneNumber, String address, boolean activate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.role = role;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.activate = activate;
    }

    public Employee(String username, String password, String fullname, Role role, boolean gender, String email, String phoneNumber, String address, boolean activate) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.role = role;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.activate = activate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
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

    public boolean isActivate() {
        return activate;
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
    }

}

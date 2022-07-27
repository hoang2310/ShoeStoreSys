package pro1041.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pro1041.entity.Employee;
import pro1041.entity.Role;
import pro1041.utils.JdbcHelper;

public class EmployeeDAO implements DAO<Employee, Integer> {

    private final String INSERT_SQL = "INSERT INTO EMPLOYEE(USERNAME, PASSWORD, FULLNAME, ROLE, GENDER, EMAIL, PHONE_NUMBER, ADDRESS, ACTIVATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final String UPDATE_SQL = "UPDATE EMPLOYEE SET USERNAME = ?, PASSWORD = ?, FULLNAME = ?, ROLE = ?, GENDER = ?, EMAIL = ?, PHONE_NUMBER = ?, ADDRESS = ?, ACTIVATE = ? WHERE ID = ?";
    private final String DELETE_SQL = "DELETE FROM EMPLOYEE WHERE ID = ?";
    private final String SELECT_ALL_SQL = "SELECT * FROM EMPLOYEE";
    private final String SELECT_BY_ID_SQL = "SELECT * FROM EMPLOYEE WHERE ID = ?";
    private final String SELECT_BY_USERNAME_SQL = "SELECT * FROM EMPLOYEE WHERE USERNAME = ?";

    private final String UPDATE_USER_INFO_SQL = "UPDATE EMPLOYEE SET USERNAME = ?, FULLNAME = ?, GENDER = ?, EMAIL = ?, PHONE_NUMBER = ?, ADDRESS = ? WHERE ID = ?";
    private final String UPDATE_PASSWORD_SQL = "UPDATE EMPLOYEE SET PASSWORD = ? WHERE ID = ?";

    @Override
    public void insert(Employee entity) {
        PreparedStatement stm = null;
        try {
            stm = JdbcHelper.getReturnGeneratedKeysStmt(INSERT_SQL, entity.getUsername(), entity.getPassword(), entity.getFullname(),
                    entity.getRole().toString(), entity.isGender(), entity.getEmail(), entity.getPhoneNumber(), entity.getAddress(), entity.isActivate());
            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                entity.setId(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                stm.getConnection().close();
            } catch (SQLException ex) {
                Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void update(Employee entity) {
        JdbcHelper.update(UPDATE_SQL, entity.getUsername(), entity.getPassword(), entity.getFullname(), entity.getRole().toString(), entity.isGender(), entity.getEmail(), entity.getPhoneNumber(), entity.getAddress(), entity.isActivate(), entity.getId());
    }

    @Override
    public void delete(Integer id) {
        JdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public Employee selectByID(Integer id) {
        List<Employee> list = this.selectBySQL(SELECT_BY_ID_SQL, id);
        if (list.isEmpty()) {
            return null;
        }
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<Employee> selectAll() {
        return this.selectBySQL(SELECT_ALL_SQL);

    }

    @Override
    public List<Employee> selectBySQL(String sql, Object... args) {
        List<Employee> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                Employee nv = new Employee();
                nv.setId(rs.getInt("ID"));
                nv.setPassword(rs.getString("PASSWORD"));
                nv.setUsername(rs.getString("USERNAME"));
                nv.setFullname(rs.getString("FULLNAME"));
                nv.setRole(Role.valueOf(rs.getString("ROLE")));
                nv.setGender(rs.getBoolean("GENDER"));
                nv.setEmail(rs.getString("EMAIL"));
                nv.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                nv.setAddress(rs.getString("ADDRESS"));
                nv.setActivate(rs.getBoolean("ACTIVATE"));
                list.add(nv);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Employee selectByUsername(String username) {
        List<Employee> list = this.selectBySQL(SELECT_BY_USERNAME_SQL, username);
        if (list.isEmpty()) {
            return null;
        }
        return list.size() > 0 ? list.get(0) : null;
    }

    public void updateUserInfo(Employee employee) {
        JdbcHelper.update(UPDATE_USER_INFO_SQL,employee.getUsername(),employee.getFullname(), employee.isGender(), employee.getEmail(), employee.getPhoneNumber(), employee.getAddress(), employee.getId());
    }

    public void updatePassword(Employee employee) {
        JdbcHelper.update(UPDATE_PASSWORD_SQL, employee.getPassword(), employee.getId());
    }

}

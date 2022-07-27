package pro1041.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pro1041.entity.Delivery;
import pro1041.entity.Status;
import pro1041.utils.JdbcHelper;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public class DeliveryDAO implements DAO<Delivery, Integer> {

    private final String INSERT_SQL = "INSERT INTO DELIVERY (CUSTOMER_NAME, EMAIL, PHONE_NUMBER, ADDRESS, NOTE, STATUS, UPDATE_TIME, ORDER_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private final String UPDATE_SQL = "UPDATE DELIVERY SET CUSTOMER_NAME = ?, EMAIL = ?, PHONE_NUMBER = ?, ADDRESS = ?, NOTE = ?, STATUS = ?, UPDATE_TIME = ?, ORDER_ID = ? WHERE ID = ?";
    private final String DELETE_SQL = "DELETE FROM DELIVERY WHERE ID = ?";
    private final String SELECT_ALL_SQL = "SELECT * FROM DELIVERY";
    private final String SELECT_BY_ID_SQL = "SELECT * FROM DELIVERY WHERE ID = ?";
    private final String SELECT_BY_ORDER_ID_SQL = "SELECT * FROM DELIVERY WHERE ORDER_ID = ?";

    @Override
    public void insert(Delivery entity) {
        PreparedStatement stm = null;
        try {
            stm = JdbcHelper.getReturnGeneratedKeysStmt(INSERT_SQL, entity.getCustomerName(), entity.getEmail(), entity.getPhoneNumber(), entity.getAddress(), entity.getNote(), entity.getStatus().toString(), entity.getUpdateTime(), entity.getOrderID());
            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                entity.setId(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                stm.getConnection().close();
            } catch (SQLException ex) {
                Logger.getLogger(DeliveryDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void update(Delivery entity) {
        JdbcHelper.update(UPDATE_SQL, entity.getCustomerName(), entity.getEmail(), entity.getPhoneNumber(), entity.getAddress(), entity.getNote(), entity.getStatus().toString(), entity.getUpdateTime(), entity.getOrderID(), entity.getId());
    }

    @Override
    public void delete(Integer id) {
        JdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public Delivery selectByID(Integer id) {
        List<Delivery> list = selectBySQL(SELECT_BY_ID_SQL, id);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<Delivery> selectAll() {
        return selectBySQL(SELECT_ALL_SQL);
    }

    @Override
    public List<Delivery> selectBySQL(String sql, Object... args) {
        List<Delivery> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                Delivery delivery = new Delivery();
                delivery.setId(rs.getInt("ID"));
                delivery.setCustomerName(rs.getString("CUSTOMER_NAME"));
                delivery.setEmail(rs.getString("EMAIL"));
                delivery.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                delivery.setAddress(rs.getString("ADDRESS"));
                delivery.setNote(rs.getString("NOTE"));
                delivery.setStatus(Status.valueOf(rs.getString("STATUS")));
                delivery.setUpdateTime(rs.getTimestamp("UPDATE_TIME"));
                delivery.setOrderID(rs.getInt("ORDER_ID"));
                list.add(delivery);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Delivery selectByOrderID(int orderID) {
        List<Delivery> list = selectBySQL(SELECT_BY_ORDER_ID_SQL, orderID);
        return list.size() > 0 ? list.get(0) : null;
    }

}

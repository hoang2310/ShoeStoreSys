package pro1041.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pro1041.entity.Order;
import pro1041.utils.JdbcHelper;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public class OrderDAO implements DAO<Order, Integer> {

    private final String INSERT_SQL = "INSERT INTO ORDERS(CREATED_TIME, EMPLOYEE_ID) VALUES (?, ?)";
    private final String UPDATE_SQL = "UPDATE ORDERS SET CREATED_TIME = ?, EMPLOYEE_ID = ? WHERE ID = ?";
    private final String DELETE_SQL = "DELETE FROM ORDERS WHERE ID = ?";
    private final String SELECT_ALL_SQL = "SELECT * FROM ORDERS";
    private final String SELECT_BY_ID_SQL = "SELECT * FROM ORDERS WHERE ID = ?";

    @Override
    public void insert(Order entity) {
        PreparedStatement stm = null;
        try {
            stm = JdbcHelper.getReturnGeneratedKeysStmt(INSERT_SQL, entity.getCreatedTime(), entity.getEmployeeID());
            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                entity.setId(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                stm.getConnection().close();
            } catch (SQLException ex) {
                Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void update(Order entity) {
        JdbcHelper.update(UPDATE_SQL, entity.getCreatedTime(), entity.getEmployeeID(), entity.getId());
    }

    @Override
    public void delete(Integer id) {
        JdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public Order selectByID(Integer id) {
        List<Order> list = selectBySQL(SELECT_BY_ID_SQL, id);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<Order> selectAll() {
        return selectBySQL(SELECT_ALL_SQL);
    }

    @Override
    public List<Order> selectBySQL(String sql, Object... args) {
        List<Order> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                list.add(new Order(rs.getInt("ID"), rs.getTimestamp("CREATED_TIME"), rs.getInt("EMPLOYEE_ID")));
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

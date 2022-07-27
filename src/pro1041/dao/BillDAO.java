package pro1041.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pro1041.entity.Bill;
import pro1041.entity.PaymentMethod;
import pro1041.utils.JdbcHelper;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public class BillDAO implements DAO<Bill, Integer> {

    private final String INSERT_SQL = "INSERT INTO BILL(CREATED_TIME, PAYMENT_METHOD, EMPLOYEE_ID, ORDER_ID) VALUES (?, ?, ?, ?)";
    private final String UPDATE_SQL = "UPDATE BILL SET CREATED_TIME = ?, PAYMENT_METHOD = ?, EMPLOYEE_ID = ?, ORDER_ID = ? WHERE ID = ?";
    private final String DELETE_SQL = "DELETE FROM BILL WHERE ID = ?";
    private final String SELECT_ALL_SQL = "SELECT * FROM BILL";
    private final String SELECT_BY_ID_SQL = "SELECT * FROM BILL WHERE ID = ?";

    @Override
    public void insert(Bill entity) {
        PreparedStatement stm = null;
        try {
            stm = JdbcHelper.getReturnGeneratedKeysStmt(INSERT_SQL, entity.getCreatedTime(), entity.getPaymentMethod().toString(), entity.getEmployeeID(), entity.getOrderID());
            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                entity.setId(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(BillDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                stm.getConnection().close();
            } catch (SQLException ex) {
                Logger.getLogger(BillDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void update(Bill entity) {
        JdbcHelper.update(UPDATE_SQL, entity.getCreatedTime(), entity.getPaymentMethod().toString(), entity.getEmployeeID(), entity.getOrderID(), entity.getId());
    }

    @Override
    public void delete(Integer id) {
        JdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public Bill selectByID(Integer id) {
        List<Bill> list = selectBySQL(SELECT_BY_ID_SQL, id);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<Bill> selectAll() {
        return selectBySQL(SELECT_ALL_SQL);
    }

    @Override
    public List<Bill> selectBySQL(String sql, Object... args) {
        List<Bill> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                list.add(new Bill(rs.getInt("ID"), rs.getTimestamp("CREATED_TIME"), PaymentMethod.valueOf(rs.getString("PAYMENT_METHOD")), rs.getInt("EMPLOYEE_ID"), rs.getInt("ORDER_ID")));
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

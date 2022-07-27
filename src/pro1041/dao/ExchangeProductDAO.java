package pro1041.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pro1041.entity.ExchangeProduct;
import pro1041.utils.JdbcHelper;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public class ExchangeProductDAO implements DAO<ExchangeProduct, Integer> {

    private final String INSERT_SQL = "INSERT INTO EXCHANGE_PRODUCT (CREATED_TIME, NOTE, O_PRODUCT_DETAIL_ID, N_PRODUCT_DETAIL_ID, BILL_ID, EMPLOYEE_ID) VALUES (?, ?, ?, ?, ?, ?)";
    private final String SELECT_ALL_SQL = "SELECT * FROM EXCHANGE_PRODUCT";
    private final String SELECT_BY_ID_SQL = "SELECT * FROM EXCHANGE_PRODUCT WHERE ID = ?";
    private final String SELECT_BY_BILL_ID_SQL = "SELECT * FROM EXCHANGE_PRODUCT WHERE BILL_ID =  ?";

    @Override
    public void insert(ExchangeProduct entity) {
        PreparedStatement stm = null;
        try {
            stm = JdbcHelper.getReturnGeneratedKeysStmt(INSERT_SQL, entity.getCreatedTime(), entity.getNote(), entity.getProductDetailReturnID(), entity.getProductDetailExchangeID(), entity.getBillID(), entity.getEmployeeID());
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
    public void update(ExchangeProduct entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ExchangeProduct selectByID(Integer id) {
        List<ExchangeProduct> list = selectBySQL(SELECT_BY_ID_SQL, id);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<ExchangeProduct> selectAll() {
        return selectBySQL(SELECT_ALL_SQL);
    }

    @Override
    public List<ExchangeProduct> selectBySQL(String sql, Object... args) {
        List<ExchangeProduct> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                ExchangeProduct ep = new ExchangeProduct();
                ep.setId(rs.getInt("ID"));
                ep.setCreatedTime(rs.getTimestamp("CREATED_TIME"));
                ep.setNote(rs.getString("NOTE"));
                ep.setProductDetailReturnID(rs.getInt("O_PRODUCT_DETAIL_ID"));
                ep.setProductDetailExchangeID(rs.getInt("N_PRODUCT_DETAIL_ID"));
                ep.setBillID(rs.getInt("BILL_ID"));
                ep.setEmployeeID(rs.getInt("EMPLOYEE_ID"));
                list.add(ep);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ExchangeProduct selectByBillID(Integer id) {
        List<ExchangeProduct> list = selectBySQL(SELECT_BY_BILL_ID_SQL, id);
        return list.size() > 0 ? list.get(0) : null;
    }
}

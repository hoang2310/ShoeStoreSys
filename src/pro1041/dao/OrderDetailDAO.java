package pro1041.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pro1041.entity.OrderDetail;
import pro1041.utils.JdbcHelper;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public class OrderDetailDAO implements DAO<OrderDetail, Integer> {

    private final String INSERT_SQL = "INSERT INTO ORDER_DETAIL(QUANTITY, PRICE, PRODUCT_DETAIL_ID, ORDER_ID) VALUES (?, ?, ?, ?)";
    private final String UPDATE_SQL = "UPDATE ORDER_DETAIL SET QUANTITY = ?, PRICE = ?, PRODUCT_DETAIL_ID = ?, ORDER_ID = ?  WHERE ID = ?";
    private final String DELETE_SQL = "DELETE FROM ORDER_DETAIL WHERE ID = ?";
    private final String SELECT_ALL_SQL = "SELECT * FROM ORDER_DETAIL";
    private final String SELECT_BY_ID_SQL = "SELECT * FROM ORDER_DETAIL WHERE ID = ?";
    private final String SELECT_ALL_BY_ORDER_ID_SQL = "SELECT * FROM ORDER_DETAIL WHERE ORDER_ID = ?";

    @Override
    public void insert(OrderDetail entity) {
        JdbcHelper.update(INSERT_SQL, entity.getQuantity(), entity.getPrice(), entity.getProductDetailID(), entity.getOrderID());
    }

    @Override
    public void update(OrderDetail entity) {
        JdbcHelper.update(UPDATE_SQL, entity.getQuantity(), entity.getPrice(), entity.getProductDetailID(), entity.getOrderID(), entity.getId());
    }

    @Override
    public void delete(Integer id) {
        JdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public OrderDetail selectByID(Integer id) {
        List<OrderDetail> list = selectBySQL(SELECT_BY_ID_SQL, id);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<OrderDetail> selectAll() {
        return selectBySQL(SELECT_ALL_SQL);
    }

    @Override
    public List<OrderDetail> selectBySQL(String sql, Object... args) {
        List<OrderDetail> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setId(rs.getInt("ID"));
                orderDetail.setQuantity(rs.getInt("QUANTITY"));
                orderDetail.setPrice(rs.getDouble("PRICE"));
                orderDetail.setProductDetailID(rs.getInt("PRODUCT_DETAIL_ID"));
                orderDetail.setOrderID(rs.getInt("ORDER_ID"));
                list.add(orderDetail);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<OrderDetail> selectAllByOrderID(int orderID){
        return selectBySQL(SELECT_ALL_BY_ORDER_ID_SQL, orderID);
    }
}

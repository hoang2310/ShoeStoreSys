package pro1041.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pro1041.entity.Product;
import pro1041.utils.JdbcHelper;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public class ProductDAO implements DAO<Product, String> {

    private final String INSERT_SQL = "INSERT INTO PRODUCT (ID, NAME, DESCRIPTION, ACTIVATE, BRAND_ID) VALUES (?, ?, ?, ?, ?)";
    private final String UPDATE_SQL = "UPDATE PRODUCT SET NAME = ?, DESCRIPTION = ?, ACTIVATE = ?, BRAND_ID = ? WHERE ID = ?";
    private final String DELETE_SQL = "DELETE FROM PRODUCT WHERE ID = ?";
    private final String SELECT_ALL_SQL = "SELECT * FROM PRODUCT";
    private final String SELECT_BY_ID_SQL = "SELECT * FROM PRODUCT WHERE ID = ?";

    @Override
    public void insert(Product entity) {
        JdbcHelper.update(INSERT_SQL, entity.getId(), entity.getName(), entity.getDescription(), entity.isActivate(), entity.getBrandID());
    }

    @Override
    public void update(Product entity) {
        JdbcHelper.update(UPDATE_SQL, entity.getName(), entity.getDescription(), entity.isActivate(), entity.getBrandID(), entity.getId());
    }

    @Override
    public void delete(String id) {
        JdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public Product selectByID(String id) {
        List<Product> list = selectBySQL(SELECT_BY_ID_SQL, id);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<Product> selectAll() {
        return selectBySQL(SELECT_ALL_SQL);
    }

    @Override
    public List<Product> selectBySQL(String sql, Object... args) {
        List<Product> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getString("ID"));
                product.setName(rs.getString("NAME"));
                product.setDescription(rs.getString("DESCRIPTION"));
                product.setActivate(rs.getBoolean("ACTIVATE"));
                product.setBrandID(rs.getInt("BRAND_ID"));
                list.add(product);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

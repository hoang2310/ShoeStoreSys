package pro1041.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pro1041.entity.ProductDetail;
import pro1041.entity.Type;
import pro1041.utils.JdbcHelper;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public class ProductDetailDAO implements DAO<ProductDetail, Integer> {

    private final String INSERT_SQL = "INSERT INTO PRODUCT_DETAIL(PRODUCT_ID, SIZE, PRICE, TYPE, QUANTITY, IMPORT_DATE, IMAGE, BARCODE) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private final String UPDATE_SQL = "UPDATE PRODUCT_DETAIL SET SIZE = ?, PRICE = ?, TYPE = ?, QUANTITY = ?, IMPORT_DATE = ?, IMAGE = ?, BARCODE  = ? WHERE ID = ?";
    private final String DELETE_SQL = "DELETE FROM PRODUCT_DETAIL WHERE ID = ?";
    private final String SELECT_ALL_SQL = "SELECT * FROM PRODUCT_DETAIL";
    private final String SELECT_BY_ID_SQL = "SELECT * FROM PRODUCT_DETAIL WHERE ID = ?";
    private final String SELECT_BY_PRODUCT_ID_SQL = "SELECT * FROM PRODUCT_DETAIL WHERE PRODUCT_ID = ?";
    private final String SELECT_BY_BARCODE_SQL = "SELECT * FROM PRODUCT_DETAIL WHERE BARCODE = ?";
    private final String UPDATE_QUANTITY_BY_ID = "UPDATE PRODUCT_DETAIL SET QUANTITY = QUANTITY + ? WHERE ID = ?";

    @Override
    public void insert(ProductDetail entity) {
        PreparedStatement stm = null;
        try {
            stm = JdbcHelper.getReturnGeneratedKeysStmt(INSERT_SQL, entity.getProductID(), entity.getSize(), entity.getPrice(), entity.getType().toString(), entity.getQuantity(), entity.getImportDate(), entity.getImage(), entity.getBarcode());
            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                entity.setId(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDetailDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                stm.getConnection().close();
            } catch (SQLException ex) {
                Logger.getLogger(ProductDetailDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void update(ProductDetail entity) {
        JdbcHelper.update(UPDATE_SQL, entity.getSize(), entity.getPrice(), entity.getType().toString(), entity.getQuantity(), entity.getImportDate(), entity.getImage(), entity.getBarcode(), entity.getId());
    }

    @Override
    public void delete(Integer id) {
        JdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public ProductDetail selectByID(Integer id) {
        List<ProductDetail> list = selectBySQL(SELECT_BY_ID_SQL, id);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<ProductDetail> selectAll() {
        return selectBySQL(SELECT_ALL_SQL);
    }

    @Override
    public List<ProductDetail> selectBySQL(String sql, Object... args) {
        List<ProductDetail> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                ProductDetail productDetail = new ProductDetail();
                productDetail.setId(rs.getInt("ID"));
                productDetail.setProductID(rs.getString("PRODUCT_ID"));
                productDetail.setSize(rs.getFloat("SIZE"));
                productDetail.setPrice(rs.getDouble("PRICE"));
                productDetail.setType(Type.valueOf(rs.getString("TYPE")));
                productDetail.setQuantity(rs.getInt("QUANTITY"));
                productDetail.setImportDate(rs.getDate("IMPORT_DATE"));
                productDetail.setImage(rs.getString("IMAGE"));
                productDetail.setBarcode(rs.getString("BARCODE"));
                list.add(productDetail);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ProductDetail> selectAllByProductID(String productID) {
        return selectBySQL(SELECT_BY_PRODUCT_ID_SQL, productID);
    }

    public ProductDetail selectByBarcode(String barcode) {
        List<ProductDetail> list = selectBySQL(SELECT_BY_BARCODE_SQL, barcode);
        return list.size() > 0 ? list.get(0) : null;
    }

    public void updateQuantityByID(int value, int id) {
        JdbcHelper.update(UPDATE_QUANTITY_BY_ID, value, id);
    }
}

package pro1041.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pro1041.entity.Brand;
import pro1041.utils.JdbcHelper;

/**
 * Do not copy, copy đấm đấy :v
 *
 * @author hanzvu
 */
public class BrandDAO implements DAO<Brand, Integer> {

    private final String INSERT_SQL = "INSERT INTO BRAND(NAME) VALUES (?)";
    private final String UPDATE_SQL = "UPDATE BRAND SET NAME = ? WHERE ID = ?";
    private final String DELETE_SQL = "DELETE FROM BRAND WHERE ID = ?";
    private final String SELECT_ALL_SQL = "SELECT * FROM BRAND ORDER BY ID";
    private final String SELECT_BY_ID_SQL = "SELECT * FROM BRAND WHERE ID = ?";

    @Override
    public void insert(Brand entity) {
        PreparedStatement stm = null;
        try {
            stm = JdbcHelper.getReturnGeneratedKeysStmt(INSERT_SQL, entity.getName());
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
    public void update(Brand entity) {
        JdbcHelper.update(UPDATE_SQL, entity.getName(), entity.getId());
    }

    @Override
    public void delete(Integer id) {
        JdbcHelper.update(DELETE_SQL, id);
    }

    @Override
    public Brand selectByID(Integer id) {
        List<Brand> list = selectBySQL(SELECT_BY_ID_SQL, id);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<Brand> selectAll() {
        return selectBySQL(SELECT_ALL_SQL);
    }

    @Override
    public List<Brand> selectBySQL(String sql, Object... args) {
        List<Brand> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                list.add(new Brand(rs.getInt("ID"), rs.getString("NAME")));
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException ex) {
            throw new RuntimeException();
        }
    }

}

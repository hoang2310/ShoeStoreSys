package pro1041.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JdbcHelper {

    private static String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static String dburl = "jdbc:sqlserver://localhost:1433;databaseName=ShoeStoreSys";
    private static String username = "shoestoresys";
    private static String password = "matkhau";

    //Nap Driverz
    static {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean testConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(dburl, username, password);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(JdbcHelper.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(JdbcHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return false;
    }

    //JdbcHelper.GETSTMT()
    public static PreparedStatement getReturnGeneratedKeysStmt(String sql, Object... args) throws SQLException {
        Connection conn = DriverManager.getConnection(dburl, username, password);
        PreparedStatement stmt;

        stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); //SQL

        for (int i = 0; i < args.length; i++) {
            stmt.setObject(i + 1, args[i]);
        }

        return stmt;
    }

    //JdbcHelper.GETSTMT()
    public static PreparedStatement getStmt(String sql, Object... args) throws SQLException {
        Connection conn = DriverManager.getConnection(dburl, username, password);
        PreparedStatement stmt;

        if (sql.trim().startsWith("{")) {
            stmt = conn.prepareCall(sql); //PROC
        } else {
            stmt = conn.prepareStatement(sql); //SQL
        }

        for (int i = 0; i < args.length; i++) {
            stmt.setObject(i + 1, args[i]);
        }
        return stmt;
    }

    //JdbcHelper.QUERY()
    public static ResultSet query(String sql, Object... args) throws SQLException {
        PreparedStatement stmt = JdbcHelper.getStmt(sql, args);
        return stmt.executeQuery();
    }

    //JdbcHelper.VALUE()
    public static Object value(String sql, Object... args) {
        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            if (rs.next()) {
                return rs.getObject(0);
            }
            rs.getStatement().getConnection().close();
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //JdbcHelper.UPDATE()
    public static int update(String sql, Object... args) {
        try {
            PreparedStatement stmt = JdbcHelper.getStmt(sql, args);
            try {
                return stmt.executeUpdate();
            } finally {
                stmt.getConnection().close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Object[]> getListOfArray(String sql, int col, Object... args) {
        try {
            List<Object[]> list = new ArrayList<>();
            ResultSet rs = query(sql, args);
            while (rs.next()) {
                Object[] vals = new Object[col];
                for (int i = 0; i < col; i++) {
                    vals[i] = rs.getObject(i + 1);
                }
                list.add(vals);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

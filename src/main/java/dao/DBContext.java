package dao;
import config.DatabaseConfig;
import java.sql.Connection;
import java.sql.SQLException;

public class DBContext {
    private static final DBContext instance = new DBContext();

    private DBContext() {}

    public static DBContext getInstance() {
        return instance;
    }

    public Connection getConnection() {
        try {
            return DatabaseConfig.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

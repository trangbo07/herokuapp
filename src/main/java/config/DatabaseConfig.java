package config;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database configuration for multiple environments
 * Supports SQL Server (local) and PostgreSQL (Heroku production)
 */
public class DatabaseConfig {
    
    private static final String LOCAL_SQL_SERVER_URL = 
        "jdbc:sqlserver://localhost:1433;databaseName=HealthCareSystem;encrypt=false;trustServerCertificate=true";
    private static final String LOCAL_SQL_SERVER_USER = "sa";
    private static final String LOCAL_SQL_SERVER_PASSWORD = "123";
    
    /**
     * Get database connection based on environment
     */
    public static Connection getConnection() throws SQLException {
        String env = System.getenv("ENV");
        
        if ("production".equals(env)) {
            return getHerokuPostgreSQLConnection();
        } else {
            return getLocalSQLServerConnection();
        }
    }
    
    /**
     * Get connection URL based on environment
     */
    public static String getConnectionUrl() {
        String env = System.getenv("ENV");
        
        if ("production".equals(env)) {
            return System.getenv("DATABASE_URL");
        } else {
            return LOCAL_SQL_SERVER_URL;
        }
    }
    
    /**
     * Get PostgreSQL connection for Heroku
     */
    private static Connection getHerokuPostgreSQLConnection() throws SQLException {
        try {
            String databaseUrl = System.getenv("DATABASE_URL");
            if (databaseUrl == null) {
                throw new SQLException("DATABASE_URL environment variable not set");
            }
            
            // Parse Heroku DATABASE_URL
            // Format: postgres://username:password@hostname:port/database
            URI uri = new URI(databaseUrl);
            String[] userInfo = uri.getUserInfo().split(":");
            String username = userInfo[0];
            String password = userInfo[1];
            String host = uri.getHost();
            int port = uri.getPort();
            String database = uri.getPath().substring(1); // Remove leading '/'
            
            String jdbcUrl = String.format(
                "jdbc:postgresql://%s:%d/%s?sslmode=require",
                host, port, database
            );
            
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(jdbcUrl, username, password);
            
        } catch (Exception e) {
            throw new SQLException("Failed to connect to Heroku PostgreSQL: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get SQL Server connection for local development
     */
    private static Connection getLocalSQLServerConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(
                LOCAL_SQL_SERVER_URL, 
                LOCAL_SQL_SERVER_USER, 
                LOCAL_SQL_SERVER_PASSWORD
            );
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQL Server JDBC driver not found", e);
        }
    }
    
    /**
     * Check if running in production environment
     */
    public static boolean isProduction() {
        return "production".equals(System.getenv("ENV"));
    }
    
    /**
     * Get database type (for SQL syntax differences)
     */
    public static String getDatabaseType() {
        return isProduction() ? "postgresql" : "sqlserver";
    }
    
    /**
     * Get appropriate SQL syntax for LIMIT
     */
    public static String getLimitSyntax(int limit) {
        if (isProduction()) {
            return "LIMIT " + limit;
        } else {
            return "TOP " + limit;
        }
    }
    
    /**
     * Get appropriate SQL for auto-increment ID
     */
    public static String getAutoIncrementSyntax() {
        if (isProduction()) {
            return "SERIAL PRIMARY KEY";
        } else {
            return "INT IDENTITY(1,1) PRIMARY KEY";
        }
    }
} 
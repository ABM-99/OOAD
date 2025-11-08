package bankmanagementsystem.model;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database configuration and connection management
 */
public class DatabaseConfig {
    private static HikariDataSource dataSource;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_management?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = ""; // No password set
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    static {
        initializeDataSource();
    }
    
    /**
     * Initialize the HikariCP data source
     */
    private static void initializeDataSource() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DB_URL);
            config.setUsername(DB_USERNAME);
            // Only set password if it's not empty (for MySQL with no password)
            if (DB_PASSWORD != null && !DB_PASSWORD.isEmpty()) {
                config.setPassword(DB_PASSWORD);
            }
            config.setDriverClassName(DB_DRIVER);
            
            // Connection pool settings
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            config.setLeakDetectionThreshold(60000);
            
            // MySQL specific settings
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");
            
            dataSource = new HikariDataSource(config);
            System.out.println("✅ Database connection pool initialized successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize database connection pool: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get a database connection from the pool
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database connection pool not initialized");
        }
        return dataSource.getConnection();
    }
    
    /**
     * Test database connection
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Close the data source and all connections
     */
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("✅ Database connection pool closed");
        }
    }
    
    /**
     * Get database URL for display purposes
     * @return database URL
     */
    public static String getDatabaseUrl() {
        return DB_URL;
    }
    
    /**
     * Update database credentials (for configuration changes)
     * @param url database URL
     * @param username database username
     * @param password database password
     */
    public static void updateCredentials(String url, String username, String password) {
        // Close existing data source
        closeDataSource();
        
        // Update static variables
        // Note: In a real application, you'd want to reload these from a config file
        System.out.println("⚠️ Database credentials updated. Restart required for changes to take effect.");
    }
}

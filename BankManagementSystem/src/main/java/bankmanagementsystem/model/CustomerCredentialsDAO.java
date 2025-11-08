package bankmanagementsystem.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for CustomerCredentials operations
 */
public class CustomerCredentialsDAO {
    
    /**
     * Save customer credentials to the database
     * @param credentials CustomerCredentials object to save
     * @return true if successful, false otherwise
     */
    public static boolean saveCredentials(CustomerCredentials credentials) {
        String sql = "INSERT INTO customer_credentials (customer_id, username, password, email, is_active) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE password = VALUES(password), email = VALUES(email), is_active = VALUES(is_active)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, credentials.getCustomerId());
            stmt.setString(2, credentials.getUsername());
            stmt.setString(3, credentials.getPassword());
            stmt.setString(4, credentials.getEmail());
            stmt.setBoolean(5, credentials.isActive());
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("✅ Credentials saved for customer: " + credentials.getCustomerId());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error saving credentials: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Load all customer credentials from the database
     * @return List of CustomerCredentials objects
     */
    public static List<CustomerCredentials> loadCredentials() {
        List<CustomerCredentials> credentials = new ArrayList<>();
        String sql = "SELECT * FROM customer_credentials ORDER BY customer_id";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                CustomerCredentials cred = createCredentialsFromResultSet(rs);
                if (cred != null) {
                    credentials.add(cred);
                }
            }
            
            System.out.println("✅ Loaded " + credentials.size() + " credentials from database");
            
        } catch (SQLException e) {
            System.err.println("❌ Error loading credentials: " + e.getMessage());
        }
        
        return credentials;
    }
    
    /**
     * Find credentials by customer ID
     * @param customerId Customer ID to search for
     * @return CustomerCredentials object or null if not found
     */
    public static CustomerCredentials findCredentialsByCustomerId(String customerId) {
        String sql = "SELECT * FROM customer_credentials WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createCredentialsFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding credentials by customer ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Find credentials by username
     * @param username Username to search for
     * @return CustomerCredentials object or null if not found
     */
    public static CustomerCredentials findCredentialsByUsername(String username) {
        String sql = "SELECT * FROM customer_credentials WHERE username = ? AND is_active = TRUE";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createCredentialsFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding credentials by username: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Find credentials by email
     * @param email Email to search for
     * @return CustomerCredentials object or null if not found
     */
    public static CustomerCredentials findCredentialsByEmail(String email) {
        String sql = "SELECT * FROM customer_credentials WHERE email = ? AND is_active = TRUE";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createCredentialsFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding credentials by email: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Update customer credentials
     * @param credentials CustomerCredentials object with updated information
     * @return true if successful, false otherwise
     */
    public static boolean updateCredentials(CustomerCredentials credentials) {
        String sql = "UPDATE customer_credentials SET password = ?, email = ?, is_active = ? WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, credentials.getPassword());
            stmt.setString(2, credentials.getEmail());
            stmt.setBoolean(3, credentials.isActive());
            stmt.setString(4, credentials.getCustomerId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Credentials updated for customer: " + credentials.getCustomerId());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating credentials: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Deactivate customer credentials
     * @param customerId Customer ID to deactivate
     * @return true if successful, false otherwise
     */
    public static boolean deactivateCredentials(String customerId) {
        String sql = "UPDATE customer_credentials SET is_active = FALSE WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Credentials deactivated for customer: " + customerId);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error deactivating credentials: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Delete customer credentials from the database
     * @param customerId Customer ID to delete credentials for
     * @return true if successful, false otherwise
     */
    public static boolean deleteCredentials(String customerId) {
        String sql = "DELETE FROM customer_credentials WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Credentials deleted for customer: " + customerId);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting credentials: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Check if username is available
     * @param username Username to check
     * @return true if available, false if taken
     */
    public static boolean isUsernameAvailable(String username) {
        String sql = "SELECT COUNT(*) FROM customer_credentials WHERE username = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error checking username availability: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Check if email is available
     * @param email Email to check
     * @return true if available, false if taken
     */
    public static boolean isEmailAvailable(String email) {
        String sql = "SELECT COUNT(*) FROM customer_credentials WHERE email = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error checking email availability: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Validate login credentials
     * @param username Username to validate
     * @param password Password to validate
     * @return CustomerCredentials object if valid, null if invalid
     */
    public static CustomerCredentials validateLogin(String username, String password) {
        String sql = "SELECT * FROM customer_credentials WHERE username = ? AND password = ? AND is_active = TRUE";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createCredentialsFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error validating login: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Create a CustomerCredentials object from a ResultSet
     * @param rs ResultSet containing credentials data
     * @return CustomerCredentials object or null if error
     */
    private static CustomerCredentials createCredentialsFromResultSet(ResultSet rs) throws SQLException {
        String customerId = rs.getString("customer_id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email = rs.getString("email");
        boolean isActive = rs.getBoolean("is_active");
        
        CustomerCredentials credentials = new CustomerCredentials(customerId, username, password, email);
        credentials.setActive(isActive);
        
        return credentials;
    }
    
    /**
     * Get credentials count
     * @return number of credentials in database
     */
    public static int getCredentialsCount() {
        String sql = "SELECT COUNT(*) FROM customer_credentials";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting credentials count: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Get active credentials count
     * @return number of active credentials in database
     */
    public static int getActiveCredentialsCount() {
        String sql = "SELECT COUNT(*) FROM customer_credentials WHERE is_active = TRUE";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting active credentials count: " + e.getMessage());
        }
        
        return 0;
    }
}

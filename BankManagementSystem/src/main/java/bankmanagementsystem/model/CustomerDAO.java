package bankmanagementsystem.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Customer operations
 */
public class CustomerDAO {
    
    /**
     * Save a customer to the database
     * @param customer Customer object to save
     * @return true if successful, false otherwise
     */
    public static boolean saveCustomer(Customer customer) {
        String sql = "INSERT INTO customers (customer_id, first_name, last_name, address, customer_type, national_id, company_name, company_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE first_name = VALUES(first_name), last_name = VALUES(last_name), address = VALUES(address), " +
                    "national_id = VALUES(national_id), company_name = VALUES(company_name), company_address = VALUES(company_address)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customer.getCustomerId());
            stmt.setString(2, customer.getFirstName());
            stmt.setString(3, customer.getLastName());
            stmt.setString(4, customer.getAddress());
            
            if (customer instanceof PersonalCustomer) {
                PersonalCustomer pc = (PersonalCustomer) customer;
                stmt.setString(5, "PERSONAL");
                stmt.setString(6, pc.getNationalId());
                stmt.setNull(7, Types.VARCHAR);
                stmt.setNull(8, Types.VARCHAR);
            } else if (customer instanceof CompanyCustomer) {
                CompanyCustomer cc = (CompanyCustomer) customer;
                stmt.setString(5, "COMPANY");
                stmt.setNull(6, Types.VARCHAR);
                stmt.setString(7, cc.getCompanyName());
                stmt.setString(8, cc.getCompanyAddress());
            } else {
                stmt.setString(5, "PERSONAL");
                stmt.setNull(6, Types.VARCHAR);
                stmt.setNull(7, Types.VARCHAR);
                stmt.setNull(8, Types.VARCHAR);
            }
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("✅ Customer saved: " + customer.getCustomerId());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error saving customer: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Load all customers from the database
     * @return List of Customer objects
     */
    public static List<Customer> loadCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY customer_id";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Customer customer = createCustomerFromResultSet(rs);
                if (customer != null) {
                    // Load linked accounts
                    loadLinkedAccounts(customer);
                    customers.add(customer);
                }
            }
            
            System.out.println("✅ Loaded " + customers.size() + " customers from database");
            
        } catch (SQLException e) {
            System.err.println("❌ Error loading customers: " + e.getMessage());
        }
        
        return customers;
    }
    
    /**
     * Find a customer by ID
     * @param customerId Customer ID to search for
     * @return Customer object or null if not found
     */
    public static Customer findCustomerById(String customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = createCustomerFromResultSet(rs);
                    if (customer != null) {
                        loadLinkedAccounts(customer);
                    }
                    return customer;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding customer: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Update a customer in the database
     * @param customer Customer object with updated information
     * @return true if successful, false otherwise
     */
    public static boolean updateCustomer(Customer customer) {
        return saveCustomer(customer); // Uses ON DUPLICATE KEY UPDATE
    }
    
    /**
     * Delete a customer from the database
     * @param customerId Customer ID to delete
     * @return true if successful, false otherwise
     */
    public static boolean deleteCustomer(String customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Customer deleted: " + customerId);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting customer: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Load linked accounts for a customer
     * @param customer Customer object to load linked accounts for
     */
    private static void loadLinkedAccounts(Customer customer) {
        String sql = "SELECT linked_account_number FROM linked_accounts WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customer.getCustomerId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customer.addLinkedAccountNumber(rs.getString("linked_account_number"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error loading linked accounts: " + e.getMessage());
        }
    }
    
    /**
     * Save linked accounts for a customer
     * @param customer Customer object with linked accounts
     */
    public static void saveLinkedAccounts(Customer customer) {
        // First delete existing linked accounts
        String deleteSql = "DELETE FROM linked_accounts WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
            
            deleteStmt.setString(1, customer.getCustomerId());
            deleteStmt.executeUpdate();
            
            // Insert new linked accounts
            if (!customer.getLinkedAccountNumbers().isEmpty()) {
                String insertSql = "INSERT INTO linked_accounts (customer_id, linked_account_number) VALUES (?, ?)";
                
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    for (String accountNumber : customer.getLinkedAccountNumbers()) {
                        insertStmt.setString(1, customer.getCustomerId());
                        insertStmt.setString(2, accountNumber);
                        insertStmt.addBatch();
                    }
                    insertStmt.executeBatch();
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error saving linked accounts: " + e.getMessage());
        }
    }
    
    /**
     * Create a Customer object from a ResultSet
     * @param rs ResultSet containing customer data
     * @return Customer object or null if error
     */
    private static Customer createCustomerFromResultSet(ResultSet rs) throws SQLException {
        String customerId = rs.getString("customer_id");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String address = rs.getString("address");
        String customerType = rs.getString("customer_type");
        
        Customer customer = null;
        
        if ("PERSONAL".equals(customerType)) {
            String nationalId = rs.getString("national_id");
            customer = new PersonalCustomer(customerId, firstName, lastName, address, nationalId);
        } else if ("COMPANY".equals(customerType)) {
            String companyName = rs.getString("company_name");
            String companyAddress = rs.getString("company_address");
            customer = new CompanyCustomer(customerId, firstName, lastName, address, companyName, companyAddress);
        }
        
        return customer;
    }
    
    /**
     * Get customer count
     * @return number of customers in database
     */
    public static int getCustomerCount() {
        String sql = "SELECT COUNT(*) FROM customers";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting customer count: " + e.getMessage());
        }
        
        return 0;
    }
}

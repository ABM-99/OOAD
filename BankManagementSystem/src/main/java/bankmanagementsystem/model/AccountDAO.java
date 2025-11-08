package bankmanagementsystem.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Account operations
 */
public class AccountDAO {
    
    /**
     * Save an account to the database
     * @param account Account object to save
     * @return true if successful, false otherwise
     */
    public static boolean saveAccount(Account account) {
        String sql = "INSERT INTO accounts (account_number, customer_id, account_type, balance, branch, is_closed, employer_name, employer_address) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE balance = VALUES(balance), branch = VALUES(branch), is_closed = VALUES(is_closed), " +
                    "employer_name = VALUES(employer_name), employer_address = VALUES(employer_address)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, account.getAccountNumber());
            stmt.setString(2, account.getCustomer().getCustomerId());
            stmt.setString(3, account.getClass().getSimpleName());
            stmt.setDouble(4, account.getBalance());
            stmt.setString(5, account.getBranch());
            stmt.setBoolean(6, account.isClosed());
            
            if (account instanceof ChequeAccount) {
                ChequeAccount ca = (ChequeAccount) account;
                stmt.setString(7, ca.getEmployerName());
                stmt.setString(8, ca.getEmployerAddress());
            } else {
                stmt.setNull(7, Types.VARCHAR);
                stmt.setNull(8, Types.VARCHAR);
            }
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("✅ Account saved: " + account.getAccountNumber());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error saving account: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Load all accounts from the database
     * @param customers List of customers to associate accounts with
     * @return List of Account objects
     */
    public static List<Account> loadAccounts(List<Customer> customers) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY account_number";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Account account = createAccountFromResultSet(rs, customers);
                if (account != null) {
                    accounts.add(account);
                }
            }
            
            System.out.println("✅ Loaded " + accounts.size() + " accounts from database");
            
        } catch (SQLException e) {
            System.err.println("❌ Error loading accounts: " + e.getMessage());
        }
        
        return accounts;
    }
    
    /**
     * Find an account by account number
     * @param accountNumber Account number to search for
     * @param customers List of customers for association
     * @return Account object or null if not found
     */
    public static Account findAccountByNumber(String accountNumber, List<Customer> customers) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, accountNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createAccountFromResultSet(rs, customers);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding account: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Find accounts by customer ID
     * @param customerId Customer ID to search for
     * @param customers List of customers for association
     * @return List of Account objects
     */
    public static List<Account> findAccountsByCustomerId(String customerId, List<Customer> customers) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ? ORDER BY account_number";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Account account = createAccountFromResultSet(rs, customers);
                    if (account != null) {
                        accounts.add(account);
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding accounts by customer ID: " + e.getMessage());
        }
        
        return accounts;
    }
    
    /**
     * Update account balance
     * @param accountNumber Account number to update
     * @param newBalance New balance amount
     * @return true if successful, false otherwise
     */
    public static boolean updateAccountBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, newBalance);
            stmt.setString(2, accountNumber);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating account balance: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Close an account
     * @param accountNumber Account number to close
     * @return true if successful, false otherwise
     */
    public static boolean closeAccount(String accountNumber) {
        String sql = "UPDATE accounts SET is_closed = TRUE WHERE account_number = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, accountNumber);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Account closed: " + accountNumber);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error closing account: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Delete an account from the database
     * @param accountNumber Account number to delete
     * @return true if successful, false otherwise
     */
    public static boolean deleteAccount(String accountNumber) {
        String sql = "DELETE FROM accounts WHERE account_number = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, accountNumber);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Account deleted: " + accountNumber);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting account: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Create an Account object from a ResultSet
     * @param rs ResultSet containing account data
     * @param customers List of customers for association
     * @return Account object or null if error
     */
    private static Account createAccountFromResultSet(ResultSet rs, List<Customer> customers) throws SQLException {
        String accountNumber = rs.getString("account_number");
        String customerId = rs.getString("customer_id");
        String accountType = rs.getString("account_type");
        double balance = rs.getDouble("balance");
        String branch = rs.getString("branch");
        boolean isClosed = rs.getBoolean("is_closed");
        
        // Find the customer
        Customer customer = findCustomerById(customers, customerId);
        if (customer == null) {
            System.err.println("❌ Customer not found for account: " + accountNumber);
            return null;
        }
        
        Account account = null;
        
        switch (accountType) {
            case "SavingsAccount":
                account = new SavingsAccount(accountNumber, customer, branch, balance);
                break;
            case "ChequeAccount":
                String employerName = rs.getString("employer_name");
                String employerAddress = rs.getString("employer_address");
                account = new ChequeAccount(accountNumber, customer, branch, balance, employerName, employerAddress);
                break;
            case "InvestmentAccount":
                account = new InvestmentAccount(accountNumber, customer, branch, balance);
                break;
            default:
                System.err.println("❌ Unknown account type: " + accountType);
                return null;
        }
        
        if (account != null) {
            account.setClosed(isClosed);
        }
        
        return account;
    }
    
    /**
     * Helper method to find customer by ID
     * @param customers List of customers to search
     * @param customerId Customer ID to find
     * @return Customer object or null if not found
     */
    private static Customer findCustomerById(List<Customer> customers, String customerId) {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equals(customerId)) {
                return customer;
            }
        }
        return null;
    }
    
    /**
     * Get account count
     * @return number of accounts in database
     */
    public static int getAccountCount() {
        String sql = "SELECT COUNT(*) FROM accounts";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting account count: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Get total balance across all accounts
     * @return total balance amount
     */
    public static double getTotalBalance() {
        String sql = "SELECT SUM(balance) FROM accounts WHERE is_closed = FALSE";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting total balance: " + e.getMessage());
        }
        
        return 0.0;
    }
}

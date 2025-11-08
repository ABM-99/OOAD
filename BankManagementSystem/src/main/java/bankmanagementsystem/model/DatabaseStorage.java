package bankmanagementsystem.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Database storage implementation that replaces file-based storage
 * Maintains the same interface as FileStorage for backward compatibility
 */
public class DatabaseStorage {
    
    /**
     * Save all customers to database
     * @param customers List of customers to save
     */
    public static void saveCustomers(List<Customer> customers) {
        System.out.println("💾 Saving " + customers.size() + " customers to database...");
        
        for (Customer customer : customers) {
            // Save customer
            CustomerDAO.saveCustomer(customer);
            
            // Save linked accounts
            CustomerDAO.saveLinkedAccounts(customer);
            
            // Save customer's accounts
            for (Account account : customer.getAccounts()) {
                AccountDAO.saveAccount(account);
            }
        }
        
        System.out.println("✅ All customers saved to database successfully");
    }
    
    /**
     * Save all accounts to database
     * @param customers List of customers containing accounts to save
     */
    public static void saveAccounts(List<Customer> customers) {
        System.out.println("💾 Saving accounts to database...");
        
        for (Customer customer : customers) {
            for (Account account : customer.getAccounts()) {
                AccountDAO.saveAccount(account);
            }
        }
        
        System.out.println("✅ All accounts saved to database successfully");
    }
    
    /**
     * Load customers from database
     * @return List of Customer objects
     */
    public static List<Customer> loadCustomers() {
        System.out.println("📂 Loading customers from database...");
        
        List<Customer> customers = CustomerDAO.loadCustomers();
        
        // Note: Accounts are loaded separately in loadAccounts() to avoid duplicates
        // Accounts will be associated with customers in loadAllData() or loadAccounts()
        
        System.out.println("✅ Loaded " + customers.size() + " customers from database");
        return customers;
    }
    
    /**
     * Load accounts from database
     * @param customers List of customers for account association
     * @return List of Account objects
     */
    public static List<Account> loadAccounts(List<Customer> customers) {
        System.out.println("📂 Loading accounts from database...");
        
        List<Account> accounts = AccountDAO.loadAccounts(customers);
        
        // Associate accounts with customers
        for (Account account : accounts) {
            Customer customer = account.getCustomer();
            if (customer != null) {
                customer.addAccount(account);
            }
        }
        
        System.out.println("✅ Loaded " + accounts.size() + " accounts from database");
        return accounts;
    }
    
    /**
     * Save customer credentials to database
     * @param credentials List of credentials to save
     */
    public static void saveCredentials(List<CustomerCredentials> credentials) {
        System.out.println("💾 Saving " + credentials.size() + " credentials to database...");
        
        for (CustomerCredentials cred : credentials) {
            CustomerCredentialsDAO.saveCredentials(cred);
        }
        
        System.out.println("✅ All credentials saved to database successfully");
    }
    
    /**
     * Load customer credentials from database
     * @return List of CustomerCredentials objects
     */
    public static List<CustomerCredentials> loadCredentials() {
        System.out.println("📂 Loading credentials from database...");
        
        List<CustomerCredentials> credentials = CustomerCredentialsDAO.loadCredentials();
        
        System.out.println("✅ Loaded " + credentials.size() + " credentials from database");
        return credentials;
    }
    
    /**
     * Save all data (customers, accounts, and credentials) to database
     * @param customers List of customers to save
     */
    public static void saveAllData(List<Customer> customers) {
        saveCustomers(customers);
        saveAccounts(customers);
    }
    
    /**
     * Save all data including credentials to database
     * @param customers List of customers to save
     * @param credentials List of credentials to save
     */
    public static void saveAllData(List<Customer> customers, List<CustomerCredentials> credentials) {
        saveCustomers(customers);
        saveAccounts(customers);
        saveCredentials(credentials);
    }
    
    /**
     * Load all data from database
     * @return List of Customer objects with associated accounts
     */
    public static List<Customer> loadAllData() {
        List<Customer> customers = loadCustomers();
        loadAccounts(customers);
        return customers;
    }
    
    /**
     * Legacy method for backward compatibility
     * @param customers List of customers to save
     */
    public static void saveData(List<Customer> customers) {
        saveAllData(customers);
    }
    
    /**
     * Legacy method for backward compatibility
     * Loads and displays all data from database
     */
    public static void loadData() {
        List<Customer> customers = loadAllData();
        System.out.println("\n=== Loaded Bank Data from Database ===");
        for (Customer customer : customers) {
            customer.displayCustomerInfo();
            for (Account account : customer.getAccounts()) {
                account.displayAccountInfo();
            }
            System.out.println("-----------------------------");
        }
    }
    
    /**
     * Test database connection
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection() {
        return DatabaseConfig.testConnection();
    }
    
    /**
     * Get database statistics
     * @return DatabaseStats object with counts
     */
    public static DatabaseStats getDatabaseStats() {
        return new DatabaseStats(
            CustomerDAO.getCustomerCount(),
            AccountDAO.getAccountCount(),
            CustomerCredentialsDAO.getCredentialsCount(),
            CustomerCredentialsDAO.getActiveCredentialsCount(),
            AccountDAO.getTotalBalance()
        );
    }
    
    /**
     * Database statistics class
     */
    public static class DatabaseStats {
        private final int customerCount;
        private final int accountCount;
        private final int credentialsCount;
        private final int activeCredentialsCount;
        private final double totalBalance;
        
        public DatabaseStats(int customerCount, int accountCount, int credentialsCount, 
                           int activeCredentialsCount, double totalBalance) {
            this.customerCount = customerCount;
            this.accountCount = accountCount;
            this.credentialsCount = credentialsCount;
            this.activeCredentialsCount = activeCredentialsCount;
            this.totalBalance = totalBalance;
        }
        
        public int getCustomerCount() { return customerCount; }
        public int getAccountCount() { return accountCount; }
        public int getCredentialsCount() { return credentialsCount; }
        public int getActiveCredentialsCount() { return activeCredentialsCount; }
        public double getTotalBalance() { return totalBalance; }
        
        @Override
        public String toString() {
            return String.format("Database Stats: %d customers, %d accounts, %d credentials (%d active), $%.2f total balance",
                customerCount, accountCount, credentialsCount, activeCredentialsCount, totalBalance);
        }
    }
    
    /**
     * Migrate data from file storage to database
     * This method can be used to migrate existing file data to the database
     * @param customers List of customers from file storage
     * @param credentials List of credentials from file storage
     */
    public static void migrateFromFiles(List<Customer> customers, List<CustomerCredentials> credentials) {
        System.out.println("🔄 Migrating data from files to database...");
        
        try {
            // Test database connection first
            if (!testConnection()) {
                System.err.println("❌ Database connection failed. Migration aborted.");
                return;
            }
            
            // Save all data to database
            saveAllData(customers, credentials);
            
            System.out.println("✅ Migration completed successfully!");
            System.out.println("📊 " + getDatabaseStats());
            
        } catch (Exception e) {
            System.err.println("❌ Migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

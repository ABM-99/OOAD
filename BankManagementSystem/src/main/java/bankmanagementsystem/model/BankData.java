package bankmanagementsystem.model;

import java.util.ArrayList;
import java.util.List;

public class BankData {
    private static List<Customer> customers = new ArrayList<>();
    private static List<CustomerCredentials> credentials = new ArrayList<>();
    private static boolean dataLoaded = false;

    // Load data from database when class is first accessed
    static {
        loadDataFromDatabase();
    }

    public static void addCustomer(Customer c) {
        customers.add(c);
        // Auto-save when adding customer
        DatabaseStorage.saveAllData(customers, credentials);
        AuditLogger.log("customer", "system", c.getCustomerId(), "create", c.getFirstName() + " " + c.getLastName(), true);
    }

    public static void addCustomerCredentials(CustomerCredentials cred) {
        credentials.add(cred);
        // Auto-save when adding credentials
        DatabaseStorage.saveAllData(customers, credentials);
        AuditLogger.log("credential", cred.getUsername(), cred.getCustomerId(), "create", "email=" + cred.getEmail(), true);
    }

    public static List<Customer> getCustomers() {
        return customers;
    }

    public static void displayAllCustomers() {
        System.out.println("\n=== Registered Customers ===");
        for (Customer c : customers) {
            c.displayCustomerInfo();
            for (Account a : c.getAccounts()) {
                a.displayAccountInfo();
            }
            System.out.println("-----------------------------");
        }
    }

    public static Customer findCustomerByName(String fullName) {
        for (Customer customer : customers) {
            String customerFullName = customer.getFirstName() + " " + customer.getLastName();
            if (customerFullName.equalsIgnoreCase(fullName)) {
                return customer;
            }
        }
        return null;
    }

    public static Customer findCustomerById(String customerId) {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equals(customerId)) {
                return customer;
            }
        }
        return null;
    }

    public static boolean isEmployee(String user, String pass) {
        // Simple hardcoded employee credentials for now
        return "admin".equals(user) && "admin123".equals(pass);
    }

    public static boolean isCustomer(String user, String pass) {
        for (CustomerCredentials cred : credentials) {
            if (cred.getUsername().equals(user) && cred.validatePassword(pass) && cred.isActive()) {
                return true;
            }
        }
        return false;
    }

    public static CustomerCredentials getCustomerCredentials(String username) {
        for (CustomerCredentials cred : credentials) {
            if (cred.getUsername().equals(username)) {
                return cred;
            }
        }
        return null;
    }

    public static Customer getCustomerByUsername(String username) {
        CustomerCredentials cred = getCustomerCredentials(username);
        if (cred != null) {
            return findCustomerById(cred.getCustomerId());
        }
        return null;
    }

    // Update customer profile fields with audit trail
    public static boolean updateCustomerProfile(String customerId, String newFirstName, String newLastName, String newAddress) {
        Customer customer = findCustomerById(customerId);
        if (customer == null) {
            AuditLogger.log("customer", "system", customerId, "update_profile", "not found", false);
            return false;
        }

        StringBuilder changes = new StringBuilder();
        if (newFirstName != null && !newFirstName.equals(customer.getFirstName())) {
            changes.append("firstName:" + customer.getFirstName() + "->" + newFirstName + ";");
            customer.setFirstName(newFirstName);
        }
        if (newLastName != null && !newLastName.equals(customer.getLastName())) {
            changes.append("lastName:" + customer.getLastName() + "->" + newLastName + ";");
            customer.setLastName(newLastName);
        }
        if (newAddress != null && !newAddress.equals(customer.getAddress())) {
            changes.append("address:" + customer.getAddress() + "->" + newAddress + ";");
            customer.setAddress(newAddress);
        }

        DatabaseStorage.saveAllData(customers, credentials);
        AuditLogger.log("customer", customerId, customerId, "update_profile", changes.toString(), true);
        return true;
    }

    // Link an account number to a customer's profile
    public static boolean linkAccountToCustomer(String customerId, String accountNumber) {
        Customer customer = findCustomerById(customerId);
        if (customer == null) {
            AuditLogger.log("link", customerId, accountNumber, "link_account", "customer not found", false);
            return false;
        }
        customer.addLinkedAccountNumber(accountNumber);
        DatabaseStorage.saveAllData(customers, credentials);
        AuditLogger.log("link", customerId, accountNumber, "link_account", "linked", true);
        return true;
    }

    // Soft close an account
    public static boolean closeAccount(String accountNumber, String actorCustomerId) {
        for (Customer c : customers) {
            for (Account a : c.getAccounts()) {
                if (a.getAccountNumber().equals(accountNumber)) {
                    a.setClosed(true);
                    DatabaseStorage.saveAllData(customers, credentials);
                    AuditLogger.log("account", actorCustomerId != null ? actorCustomerId : c.getCustomerId(), accountNumber, "close", "soft close", true);
                    return true;
                }
            }
        }
        AuditLogger.log("account", actorCustomerId != null ? actorCustomerId : "system", accountNumber, "close", "account not found", false);
        return false;
    }

    // Load data from database
    public static void loadDataFromDatabase() {
        if (!dataLoaded) {
            try {
                customers = DatabaseStorage.loadAllData();
                credentials = DatabaseStorage.loadCredentials();
                dataLoaded = true;
                System.out.println("💾 Bank data loaded from database. Found " + customers.size() + " customers and " + credentials.size() + " credentials.");
            } catch (Exception e) {
                System.err.println("❌ Failed to load data from database!");
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
                System.err.println("\n⚠️ IMPORTANT: Database connection failed!");
                System.err.println("Please check:");
                System.err.println("1. MySQL server is running");
                System.err.println("2. Database 'bank_management' exists");
                System.err.println("3. MySQL credentials in DatabaseConfig.java are correct");
                System.err.println("4. Tables are created (run DatabaseSetupRunner)");
                System.err.println("\n⚠️ Starting with empty data. Please fix database connection and restart.");
                // Initialize with empty lists instead of falling back to files
                customers = new ArrayList<>();
                credentials = new ArrayList<>();
                dataLoaded = true;
                System.out.println("📁 Started with empty data. Database connection required.");
            }
        }
    }

    // Save data to database
    public static void saveDataToFiles() {
        DatabaseStorage.saveAllData(customers, credentials);
    }

    // Add account to customer and save
    public static void addAccountToCustomer(String customerId, Account account) {
        Customer customer = findCustomerById(customerId);
        if (customer != null) {
            customer.addAccount(account);
            DatabaseStorage.saveAllData(customers, credentials);
            AuditLogger.log("account", customerId, account.getAccountNumber(), "create", account.getClass().getSimpleName(), true);
        }
    }

    // Update account balance and save
    public static void updateAccountBalance(String accountNumber, double newBalance) {
        for (Customer customer : customers) {
            for (Account account : customer.getAccounts()) {
                if (account.getAccountNumber().equals(accountNumber)) {
                    // Update balance (this is a simplified approach)
                    // In a real system, you'd have a setter method
                    DatabaseStorage.saveAllData(customers, credentials);
                    return;
                }
            }
        }
    }

    // Customer registration methods
    public static boolean isUsernameAvailable(String username) {
        for (CustomerCredentials cred : credentials) {
            if (cred.getUsername().equals(username)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmailAvailable(String email) {
        for (CustomerCredentials cred : credentials) {
            if (cred.getEmail().equals(email)) {
                return false;
            }
        }
        return true;
    }

    // Employee creates customer account (without credentials)
    public static String createCustomerAccount(String firstName, String lastName, String address, 
                                             String customerType, String additionalInfo) {
        // Generate customer ID
        String customerId = generateCustomerId();

        // Create customer based on type
        Customer customer;
        if ("PERSONAL".equals(customerType)) {
            customer = new PersonalCustomer(customerId, firstName, lastName, address, additionalInfo);
        } else if ("COMPANY".equals(customerType)) {
            String[] companyInfo = additionalInfo.split("\\|");
            String companyName = companyInfo.length > 0 ? companyInfo[0] : "";
            String companyAddress = companyInfo.length > 1 ? companyInfo[1] : "";
            customer = new CompanyCustomer(customerId, firstName, lastName, address, companyName, companyAddress);
        } else {
            return "Invalid customer type.";
        }

        // Add customer to system (without credentials)
        addCustomer(customer);

        return "Customer account created successfully! Customer ID: " + customerId + 
               "\nCustomer can now set up their login credentials.";
    }
    
    // Employee creates customer account and returns the customer ID
    public static String createCustomerAccountAndGetId(String firstName, String lastName, String address, 
                                                       String customerType, String additionalInfo) {
        // Generate customer ID
        String customerId = generateCustomerId();

        // Create customer based on type
        Customer customer;
        if ("PERSONAL".equals(customerType)) {
            customer = new PersonalCustomer(customerId, firstName, lastName, address, additionalInfo);
        } else if ("COMPANY".equals(customerType)) {
            String[] companyInfo = additionalInfo.split("\\|");
            String companyName = companyInfo.length > 0 ? companyInfo[0] : "";
            String companyAddress = companyInfo.length > 1 ? companyInfo[1] : "";
            customer = new CompanyCustomer(customerId, firstName, lastName, address, companyName, companyAddress);
        } else {
            return null; // Invalid customer type
        }

        // Add customer to system (without credentials)
        addCustomer(customer);

        return customerId; // Return just the customer ID
    }

    // Customer sets up their own credentials
    public static String setupCustomerCredentials(String customerId, String username, String password, String email) {
        // Check if customer exists
        Customer customer = findCustomerById(customerId);
        if (customer == null) {
            return "Customer ID not found.";
        }

        // Check if username is available
        if (!isUsernameAvailable(username)) {
            return "Username already exists. Please choose a different username.";
        }

        // Check if email is available
        if (!isEmailAvailable(email)) {
            return "Email already registered. Please use a different email.";
        }

        // Create credentials
        CustomerCredentials cred = new CustomerCredentials(customerId, username, password, email);
        addCustomerCredentials(cred);

        return "Login credentials set up successfully! You can now login with your username and password.";
    }

    // Legacy method for backward compatibility
    public static String registerCustomer(String firstName, String lastName, String address, 
                                        String customerType, String username, String password, 
                                        String email, String additionalInfo) {
        // This is now a two-step process
        String result1 = createCustomerAccount(firstName, lastName, address, customerType, additionalInfo);
        if (result1.startsWith("Customer account created successfully")) {
            String customerId = result1.substring(result1.indexOf("Customer ID: ") + 13);
            String result2 = setupCustomerCredentials(customerId, username, password, email);
            return result2;
        }
        return result1;
    }

    private static String generateCustomerId() {
        // Simple ID generation - in real system, this would be more sophisticated
        return "C" + String.format("%03d", customers.size() + 1);
    }

    public static List<CustomerCredentials> getCredentials() {
        return credentials;
    }

    // Automatic interest application system
    public static void applyAutomaticInterest() {
        int totalAccountsProcessed = 0;
        int interestApplied = 0;
        
        for (Customer customer : customers) {
            for (Account account : customer.getAccounts()) {
                totalAccountsProcessed++;
                if (account instanceof Interest) {
                    ((Interest) account).calculateInterest();
                    interestApplied++;
                }
            }
        }
        
        // Save data after applying interest
        DatabaseStorage.saveAllData(customers, credentials);
        AuditLogger.log("system", "interest", "*", "apply", "processed=" + totalAccountsProcessed + ", applied=" + interestApplied, true);
        
        System.out.println("🔄 Automatic Interest Applied:");
        System.out.println("   - Total accounts processed: " + totalAccountsProcessed);
        System.out.println("   - Interest applied to: " + interestApplied + " accounts");
    }

    // Method to simulate daily interest application (can be called by system)
    public static void dailyInterestApplication() {
        applyAutomaticInterest();
    }
}

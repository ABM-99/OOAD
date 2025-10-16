package bankmanagementsystem.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {
    private static final String DATA_DIR = "data";
    private static final String CUSTOMERS_FILE = DATA_DIR + "/customers.txt";
    private static final String ACCOUNTS_FILE = DATA_DIR + "/accounts.txt";
    private static final String CREDENTIALS_FILE = DATA_DIR + "/credentials.txt";

    // Create data directory if it doesn't exist
    static {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    // Save all customers to file
    public static void saveCustomers(List<Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer customer : customers) {
                writer.write("CUSTOMER_START\n");
                writer.write("ID:" + customer.getCustomerId() + "\n");
                writer.write("FIRST_NAME:" + customer.getFirstName() + "\n");
                writer.write("LAST_NAME:" + customer.getLastName() + "\n");
                writer.write("ADDRESS:" + customer.getAddress() + "\n");
                if (!customer.getLinkedAccountNumbers().isEmpty()) {
                    writer.write("LINKED:" + String.join(",", customer.getLinkedAccountNumbers()) + "\n");
                }
                
                // Write customer type specific data
                if (customer instanceof PersonalCustomer) {
                    PersonalCustomer pc = (PersonalCustomer) customer;
                    writer.write("TYPE:PERSONAL\n");
                    writer.write("NATIONAL_ID:" + pc.getNationalId() + "\n");
                } else if (customer instanceof CompanyCustomer) {
                    CompanyCustomer cc = (CompanyCustomer) customer;
                    writer.write("TYPE:COMPANY\n");
                    writer.write("COMPANY_NAME:" + cc.getCompanyName() + "\n");
                    writer.write("COMPANY_ADDRESS:" + cc.getCompanyAddress() + "\n");
                }
                writer.write("CUSTOMER_END\n");
            }
            System.out.println("✅ Customers saved to " + CUSTOMERS_FILE);
        } catch (IOException e) {
            System.err.println("❌ Error saving customers: " + e.getMessage());
        }
    }

    // Save all accounts to file
    public static void saveAccounts(List<Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNTS_FILE))) {
            for (Customer customer : customers) {
                for (Account account : customer.getAccounts()) {
                    writer.write("ACCOUNT_START\n");
                    writer.write("ACCOUNT_NUMBER:" + account.getAccountNumber() + "\n");
                    writer.write("CUSTOMER_ID:" + customer.getCustomerId() + "\n");
                    writer.write("BALANCE:" + account.getBalance() + "\n");
                    writer.write("BRANCH:" + account.getBranch() + "\n");
                    writer.write("TYPE:" + account.getClass().getSimpleName() + "\n");
                    writer.write("CLOSED:" + account.isClosed() + "\n");
                    
                    // Write account type specific data
                    if (account instanceof ChequeAccount) {
                        ChequeAccount ca = (ChequeAccount) account;
                        writer.write("EMPLOYER_NAME:" + ca.getEmployerName() + "\n");
                        writer.write("EMPLOYER_ADDRESS:" + ca.getEmployerAddress() + "\n");
                    }
                    writer.write("ACCOUNT_END\n");
                }
            }
            System.out.println("✅ Accounts saved to " + ACCOUNTS_FILE);
        } catch (IOException e) {
            System.err.println("❌ Error saving accounts: " + e.getMessage());
        }
    }

    // Load customers from file
    public static List<Customer> loadCustomers() {
        List<Customer> customers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            String line;
            String id = "";
            String firstName = "";
            String lastName = "";
            String address = "";
            String customerType = "";
            String nationalId = "";
            String companyName = "";
            String companyAddress = "";
            String linked = "";
            
            while ((line = reader.readLine()) != null) {
                if (line.equals("CUSTOMER_START")) {
                    // Reset all fields
                    id = "";
                    firstName = "";
                    lastName = "";
                    address = "";
                    customerType = "";
                    nationalId = "";
                    companyName = "";
                    companyAddress = "";
                } else if (line.equals("CUSTOMER_END")) {
                    // Create customer based on type
                    Customer customer = null;
                    if (customerType.equals("PERSONAL")) {
                        customer = new PersonalCustomer(id, firstName, lastName, address, nationalId);
                    } else if (customerType.equals("COMPANY")) {
                        customer = new CompanyCustomer(id, firstName, lastName, address, companyName, companyAddress);
                    }
                    if (customer != null) {
                        if (!linked.isEmpty()) {
                            for (String accNo : linked.split(",")) {
                                if (!accNo.isBlank()) customer.addLinkedAccountNumber(accNo.trim());
                            }
                        }
                        customers.add(customer);
                    }
                } else if (line.startsWith("ID:")) {
                    id = line.substring(3);
                } else if (line.startsWith("FIRST_NAME:")) {
                    firstName = line.substring(11);
                } else if (line.startsWith("LAST_NAME:")) {
                    lastName = line.substring(10);
                } else if (line.startsWith("ADDRESS:")) {
                    address = line.substring(8);
                } else if (line.startsWith("LINKED:")) {
                    linked = line.substring(7);
                } else if (line.startsWith("TYPE:")) {
                    customerType = line.substring(5);
                } else if (line.startsWith("NATIONAL_ID:")) {
                    nationalId = line.substring(12);
                } else if (line.startsWith("COMPANY_NAME:")) {
                    companyName = line.substring(13);
                } else if (line.startsWith("COMPANY_ADDRESS:")) {
                    companyAddress = line.substring(16);
                }
            }
        } catch (IOException e) {
            System.out.println("⚠️ No customer data found or error reading file.");
        }
        return customers;
    }

    // Load accounts from file
    public static List<Account> loadAccounts(List<Customer> customers) {
        List<Account> accounts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNTS_FILE))) {
            String line;
            String accountNumber = "";
            String customerId = "";
            double balance = 0.0;
            String branch = "";
            String accountType = "";
            String employerName = "";
            String employerAddress = "";
            boolean closed = false;
            
            while ((line = reader.readLine()) != null) {
                if (line.equals("ACCOUNT_START")) {
                    accountNumber = "";
                    customerId = "";
                    balance = 0.0;
                    branch = "";
                    accountType = "";
                    employerName = "";
                    employerAddress = "";
                } else if (line.equals("ACCOUNT_END")) {
                    // Create account based on type
                    Customer customer = findCustomerById(customers, customerId);
                    if (customer != null) {
                        Account account = createAccount(accountType, accountNumber, customer, branch, balance, employerName, employerAddress);
                        if (account != null) {
                            account.setClosed(closed);
                            accounts.add(account);
                            customer.addAccount(account);
                        }
                    }
                } else if (line.startsWith("ACCOUNT_NUMBER:")) {
                    accountNumber = line.substring(15);
                } else if (line.startsWith("CUSTOMER_ID:")) {
                    customerId = line.substring(12);
                } else if (line.startsWith("BALANCE:")) {
                    balance = Double.parseDouble(line.substring(8));
                } else if (line.startsWith("BRANCH:")) {
                    branch = line.substring(7);
                } else if (line.startsWith("TYPE:")) {
                    accountType = line.substring(5);
                } else if (line.startsWith("CLOSED:")) {
                    closed = Boolean.parseBoolean(line.substring(7));
                } else if (line.startsWith("EMPLOYER_NAME:")) {
                    employerName = line.substring(14);
                } else if (line.startsWith("EMPLOYER_ADDRESS:")) {
                    employerAddress = line.substring(17);
                }
            }
        } catch (IOException e) {
            System.out.println("⚠️ No account data found or error reading file.");
        }
        return accounts;
    }

    // Helper method to find customer by ID
    private static Customer findCustomerById(List<Customer> customers, String customerId) {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equals(customerId)) {
                return customer;
            }
        }
        return null;
    }

    // Helper method to create account based on type
    private static Account createAccount(String accountType, String accountNumber, Customer customer, String branch, double balance, String employerName, String employerAddress) {
        switch (accountType) {
            case "SavingsAccount":
                return new SavingsAccount(accountNumber, customer, branch, balance);
            case "ChequeAccount":
                return new ChequeAccount(accountNumber, customer, branch, balance, employerName, employerAddress);
            case "InvestmentAccount":
                return new InvestmentAccount(accountNumber, customer, branch, balance);
            default:
                return null;
        }
    }

    // Save customer credentials to file
    public static void saveCredentials(List<CustomerCredentials> credentials) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CREDENTIALS_FILE))) {
            for (CustomerCredentials cred : credentials) {
                writer.write("CREDENTIAL_START\n");
                writer.write("CUSTOMER_ID:" + cred.getCustomerId() + "\n");
                writer.write("USERNAME:" + cred.getUsername() + "\n");
                writer.write("PASSWORD:" + cred.getPassword() + "\n");
                writer.write("EMAIL:" + cred.getEmail() + "\n");
                writer.write("IS_ACTIVE:" + cred.isActive() + "\n");
                writer.write("CREDENTIAL_END\n");
            }
            System.out.println("✅ Credentials saved to " + CREDENTIALS_FILE);
        } catch (IOException e) {
            System.err.println("❌ Error saving credentials: " + e.getMessage());
        }
    }

    // Load customer credentials from file
    public static List<CustomerCredentials> loadCredentials() {
        List<CustomerCredentials> credentials = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CREDENTIALS_FILE))) {
            String line;
            String customerId = "";
            String username = "";
            String password = "";
            String email = "";
            boolean isActive = true;
            
            while ((line = reader.readLine()) != null) {
                if (line.equals("CREDENTIAL_START")) {
                    // Reset all fields
                    customerId = "";
                    username = "";
                    password = "";
                    email = "";
                    isActive = true;
                } else if (line.equals("CREDENTIAL_END")) {
                    // Create credentials object
                    CustomerCredentials cred = new CustomerCredentials(customerId, username, password, email);
                    cred.setActive(isActive);
                    credentials.add(cred);
                } else if (line.startsWith("CUSTOMER_ID:")) {
                    customerId = line.substring(12);
                } else if (line.startsWith("USERNAME:")) {
                    username = line.substring(9);
                } else if (line.startsWith("PASSWORD:")) {
                    password = line.substring(9);
                } else if (line.startsWith("EMAIL:")) {
                    email = line.substring(6);
                } else if (line.startsWith("IS_ACTIVE:")) {
                    isActive = Boolean.parseBoolean(line.substring(10));
                }
            }
        } catch (IOException e) {
            System.out.println("⚠️ No credentials data found or error reading file.");
        }
        return credentials;
    }

    // Save all data (customers, accounts, and credentials)
    public static void saveAllData(List<Customer> customers) {
        saveCustomers(customers);
        saveAccounts(customers);
    }

    // Save all data including credentials
    public static void saveAllData(List<Customer> customers, List<CustomerCredentials> credentials) {
        saveCustomers(customers);
        saveAccounts(customers);
        saveCredentials(credentials);
    }

    // Load all data
    public static List<Customer> loadAllData() {
        List<Customer> customers = loadCustomers();
        loadAccounts(customers);
        return customers;
    }

    // Legacy method for backward compatibility
    public static void saveData(List<Customer> customers) {
        saveAllData(customers);
    }

    public static void loadData() {
        List<Customer> customers = loadAllData();
        System.out.println("\n=== Loaded Bank Data ===");
        for (Customer customer : customers) {
            customer.displayCustomerInfo();
            for (Account account : customer.getAccounts()) {
                account.displayAccountInfo();
            }
            System.out.println("-----------------------------");
        }
    }
}

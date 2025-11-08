package bankmanagementsystem.model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    protected String customerId;
    public String firstName;
    protected String lastName;
    protected String address;
    protected List<Account> accounts = new ArrayList<>();
    protected List<String> linkedAccountNumbers = new ArrayList<>();

    public Customer(String customerId, String firstName, String lastName, String address) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    public void addAccount(Account account) {
        // Check if account already exists to prevent duplicates
        if (account != null && !accounts.contains(account)) {
            // Also check by account number to prevent duplicates with same account number
            boolean exists = false;
            for (Account existingAccount : accounts) {
                if (existingAccount.getAccountNumber().equals(account.getAccountNumber())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                accounts.add(account);
            }
        }
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void displayCustomerInfo() {
        System.out.println("Customer: " + firstName + " " + lastName + " (" + customerId + ")");
        System.out.println("Address: " + address);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAddress() {
        return address;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getLinkedAccountNumbers() {
        return linkedAccountNumbers;
    }

    public void addLinkedAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) return;
        if (!linkedAccountNumbers.contains(accountNumber)) linkedAccountNumbers.add(accountNumber);
    }

    public void removeLinkedAccountNumber(String accountNumber) {
        linkedAccountNumbers.remove(accountNumber);
    }
}

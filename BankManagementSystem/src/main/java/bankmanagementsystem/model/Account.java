package bankmanagementsystem.model;

public abstract class Account {
    protected String accountNumber;
    protected double balance;
    protected String branch;
    protected Customer customer;
    protected boolean closed = false;

    public Account(String accountNumber, Customer customer, String branch, double balance) {
        this.accountNumber = accountNumber;
        this.customer = customer;
        this.branch = branch;
        this.balance = balance;
    }

    public void deposit(double amount) {
        if (closed) return; // guard: no operations on closed accounts
        if (amount > 0) balance += amount;
    }

    public abstract void displayAccountInfo();

    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public String getBranch() { return branch; }
    public Customer getCustomer() { return customer; }
    public boolean isClosed() { return closed; }
    public void setClosed(boolean closed) { this.closed = closed; }
}

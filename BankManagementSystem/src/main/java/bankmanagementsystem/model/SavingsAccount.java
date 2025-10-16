package bankmanagementsystem.model;

public class SavingsAccount extends Account implements Interest {

    public SavingsAccount(String accountNumber, Customer customer, String branch, double balance) {
        super(accountNumber, customer, branch, balance);
    }

    @Override
    public void calculateInterest() {
        double interest = balance * 0.0005;
        balance += interest;
    }

    @Override
    public void displayAccountInfo() {
        System.out.println("Savings Account [" + accountNumber + "] - Balance: BWP " + balance);
    }
}

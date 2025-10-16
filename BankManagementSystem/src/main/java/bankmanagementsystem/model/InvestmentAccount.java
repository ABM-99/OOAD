package bankmanagementsystem.model;

public class InvestmentAccount extends Account implements Interest, Withdraw {

    public InvestmentAccount(String accountNumber, Customer customer, String branch, double balance) {
        super(accountNumber, customer, branch, balance >= 500 ? balance : 500);
    }

    @Override
    public void calculateInterest() {
        double interest = balance * 0.05;
        balance += interest;
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= balance) balance -= amount;
        else System.out.println("Insufficient funds.");
    }

    @Override
    public void displayAccountInfo() {
        System.out.println("Investment Account [" + accountNumber + "] - Balance: BWP " + balance);
    }
}

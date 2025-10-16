package bankmanagementsystem.model;

public class ChequeAccount extends Account implements Withdraw {
    private String employerName;
    private String employerAddress;

    public ChequeAccount(String accountNumber, Customer customer, String branch, double balance, String employerName, String employerAddress) {
        super(accountNumber, customer, branch, balance);
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= balance) balance -= amount;
        else System.out.println("Insufficient funds.");
    }

    @Override
    public void displayAccountInfo() {
        System.out.println("Cheque Account [" + accountNumber + "] - Balance: BWP " + balance);
        System.out.println("Employer: " + employerName + " (" + employerAddress + ")");
    }

    public String getEmployerName() {
        return employerName;
    }

    public String getEmployerAddress() {
        return employerAddress;
    }
}

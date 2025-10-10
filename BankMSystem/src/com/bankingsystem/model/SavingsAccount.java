package com.bankingsystem.model;

/**
 * Savings account:
 * - earns 0.05% monthly interest (0.0005)
 * - does not permit withdrawals
 */
public class SavingsAccount extends Account implements InterestBearing {
    private static final double INTEREST_RATE = 0.0005; // monthly (0.05%)

    public SavingsAccount(String accountNumber, double initialBalance, String branch) {
        super(accountNumber, initialBalance, branch);
    }

    // Withdrawals are not allowed for SavingsAccount
    @Override
    public boolean withdraw(double amount, String note) {
        // Could log attempt, but returns false to indicate disallowed
        Transaction tx = new Transaction(IdUtil.nextId("TX"), getAccountNumber(), 0.0, "WITHDRAW_ATTEMPT", "Attempted withdrawal: " + note);
        addTransaction(tx);
        return false;
    }

    @Override
    public double calculateInterest() {
        double interest = getBalance() * INTEREST_RATE;
        if (interest > 0) {
            increaseBalance(interest);
            Transaction tx = new Transaction(IdUtil.nextId("TX"), getAccountNumber(), interest, "INTEREST", "Savings interest");
            addTransaction(tx);
        }
        return interest;
    }
}

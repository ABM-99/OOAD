package com.bankingsystem.model;

/**
 * Investment account:
 * - requires minimum opening balance of BWP 500.00
 * - earns 5% (0.05) interest (per period as per requirements)
 * - allows deposits and withdrawals while maintaining balance checks
 */
public class InvestmentAccount extends Account implements InterestBearing {
    private static final double INTEREST_RATE = 0.05; // 5%
    private static final double MIN_OPENING_BALANCE = 500.0;

    public InvestmentAccount(String accountNumber, double initialBalance, String branch) {
        super(accountNumber, initialBalance, branch);
        if (initialBalance < MIN_OPENING_BALANCE) {
            throw new IllegalArgumentException("Investment account requires minimum opening balance of BWP500.00");
        }
    }

    @Override
    public boolean withdraw(double amount, String note) {
        if (amount <= 0) return false;
        if (amount > getBalance()) return false;
        reduceBalance(amount);
        Transaction tx = new Transaction(IdUtil.nextId("TX"), getAccountNumber(), amount, "WITHDRAWAL", note);
        addTransaction(tx);
        return true;
    }

    @Override
    public double calculateInterest() {
        double interest = getBalance() * INTEREST_RATE;
        if (interest > 0) {
            increaseBalance(interest);
            Transaction tx = new Transaction(IdUtil.nextId("TX"), getAccountNumber(), interest, "INTEREST", "Investment interest");
            addTransaction(tx);
        }
        return interest;
    }
}

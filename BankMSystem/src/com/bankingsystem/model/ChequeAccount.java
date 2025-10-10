package com.bankingsystem.model;

/**
 * Cheque account:
 * - used for salaries
 * - requires employer detail on creation
 * - allows deposits and withdrawals
 * - no interest is applied
 */
public class ChequeAccount extends Account {
    private final String employer;

    public ChequeAccount(String accountNumber, double initialBalance, String branch, String employer) {
        super(accountNumber, initialBalance, branch);
        if (employer == null || employer.isBlank()) {
            throw new IllegalArgumentException("Cheque account requires employer details.");
        }
        this.employer = employer;
    }

    public String getEmployer() { return employer; }

    @Override
    public boolean withdraw(double amount, String note) {
        if (amount <= 0) return false;
        if (amount > getBalance()) return false;
        reduceBalance(amount);
        Transaction tx = new Transaction(IdUtil.nextId("TX"), getAccountNumber(), amount, "WITHDRAWAL", note);
        addTransaction(tx);
        return true;
    }

    // no calculateInterest method because not interest-bearing
}

package com.bankingsystem.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Account {
    private final String accountNumber;
    private double balance;
    private final String branch;
    private boolean closed = false;
    private final List<Transaction> transactions = new ArrayList<>();

    public Account(String accountNumber, double initialBalance, String branch) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.branch = branch;
    }

    // Encapsulated fields with getters
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public String getBranch() { return branch; }
    public boolean isClosed() { return closed; }
    public void setClosed(boolean closed) { this.closed = closed; }

    // Return an immutable copy for safety (encapsulation)
    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(new ArrayList<>(transactions));
    }

    protected void addTransaction(Transaction tx) {
        transactions.add(tx);
    }

    protected void increaseBalance(double amount) {
        this.balance += amount;
    }

    protected void reduceBalance(double amount) {
        this.balance -= amount;
    }

    // Overloaded deposit methods (demonstrates overloading)
    public void deposit(double amount) {
        deposit(amount, "Deposit");
    }

    public void deposit(double amount, String note) {
        if (closed) throw new IllegalStateException("Cannot deposit to a closed account.");
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        increaseBalance(amount);
        Transaction tx = new Transaction(IdUtil.nextId("TX"), accountNumber, amount, "DEPOSIT", note);
        addTransaction(tx);
    }

    // Withdraw is abstract and must be overridden by subclasses to enforce rules
    public abstract boolean withdraw(double amount, String note);

    @Override
    public String toString() {
        return String.format("%s [Acc:%s] Balance=BWP %.2f Branch=%s Closed=%s",
                this.getClass().getSimpleName(), accountNumber, balance, branch, closed);
    }
}

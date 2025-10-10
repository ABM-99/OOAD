package com.bankingsystem.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final String transactionId;
    private final String accountNumber;
    private final double amount;
    private final String type; // DEPOSIT, WITHDRAWAL, INTEREST, FEE
    private final LocalDateTime timestamp;
    private final String note;

    public Transaction(String transactionId, String accountNumber, double amount, String type, String note) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.note = note;
    }

    public String getTransactionId() { return transactionId; }
    public String getAccountNumber() { return accountNumber; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getNote() { return note; }

    @Override
    public String toString() {
        // Format: 2025-09-19T15:20 | TX-xxxx | DEPOSIT BWP 100.00 | note
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = timestamp.format(f);
        String notePart = (note == null || note.isBlank()) ? "" : ("| " + note);
        return String.format("%s | %s | %s BWP %.2f %s",
                time, transactionId, type, amount, notePart);
    }
}

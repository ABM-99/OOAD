package com.bankingsystem.model;

import java.util.*;

public class Main {
    private static final Map<String, Customer> customers = new LinkedHashMap<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        setupSampleData();
        System.out.println("=== Banking System Core Model Demo (Console) ===");
        mainMenu();
    }

    private static void setupSampleData() {
        Customer c1 = new Customer(IdUtil.nextId("CUST"), "Arabang", "Mothofela", "Kopong");
        // open accounts
        Account sa = new SavingsAccount(IdUtil.nextId("SA"), 1000.00, "Kopong Branch");
        Account ia = new InvestmentAccount(IdUtil.nextId("IA"), 1000.00, "Kopong Branch");
        Account ca = new ChequeAccount(IdUtil.nextId("CA"), 500.00, "Kopong Branch", "First Minds Ltd");

        c1.addAccount(sa);
        c1.addAccount(ia);
        c1.addAccount(ca);

        customers.put(c1.getCustomerId(), c1);

        // Add a second customer for variety
        Customer c2 = new Customer(IdUtil.nextId("CUST"), "Lerato", "Kgosi", "Gaborone");
        Account sa2 = new SavingsAccount(IdUtil.nextId("SA"), 200.00, "Gaborone Branch");
        c2.addAccount(sa2);
        customers.put(c2.getCustomerId(), c2);
    }

    private static void mainMenu() {
        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("1) List customers");
            System.out.println("2) Select customer by ID");
            System.out.println("3) Apply interest to all interest-bearing accounts");
            System.out.println("4) Exit");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": listCustomers(); break;
                case "2": selectCustomer(); break;
                case "3": applyInterestAll(); break;
                case "4": System.out.println("Goodbye."); return;
                default: System.out.println("Invalid choice."); break;
            }
        }
    }

    private static void listCustomers() {
        System.out.println("\nCustomers:");
        for (Customer c : customers.values()) {
            System.out.println(c.getCustomerId() + " -> " + c);
        }
    }

    private static void selectCustomer() {
        System.out.print("Enter customer ID: ");
        String id = scanner.nextLine().trim();
        Customer c = customers.get(id);
        if (c == null) {
            System.out.println("Customer not found.");
            return;
        }
        customerMenu(c);
    }

    private static void customerMenu(Customer customer) {
        while (true) {
            System.out.println("\nCustomer: " + customer);
            System.out.println("1) View accounts and balances");
            System.out.println("2) Deposit");
            System.out.println("3) Withdraw");
            System.out.println("4) View audit trail (transactions)");
            System.out.println("5) Open new account");
            System.out.println("6) Back to main menu");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": viewAccounts(customer); break;
                case "2": depositFlow(customer); break;
                case "3": withdrawFlow(customer); break;
                case "4": viewAuditTrail(customer); break;
                case "5": openAccountFlow(customer); break;
                case "6": return;
                default: System.out.println("Invalid choice."); break;
            }
        }
    }

    private static void viewAccounts(Customer c) {
        System.out.println("\nAccounts for " + c + ":");
        for (Account a : c.getAccounts()) {
            System.out.println(a);
        }
    }

    private static void depositFlow(Customer c) {
        System.out.print("Enter account number: ");
        String accNo = scanner.nextLine().trim();
        Account acc = findAccount(c, accNo);
        if (acc == null) { System.out.println("Account not found."); return; }
        System.out.print("Amount to deposit (BWP): ");
        double amt = readPositiveDouble();
        System.out.print("Note (optional): ");
        String note = scanner.nextLine().trim();
        try {
            acc.deposit(amt, note.isBlank() ? "Deposit" : note);
            System.out.println("Deposit successful. New balance: BWP " + String.format("%.2f", acc.getBalance()));
        } catch (Exception e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
    }

    private static void withdrawFlow(Customer c) {
        System.out.print("Enter account number: ");
        String accNo = scanner.nextLine().trim();
        Account acc = findAccount(c, accNo);
        if (acc == null) { System.out.println("Account not found."); return; }
        System.out.print("Amount to withdraw (BWP): ");
        double amt = readPositiveDouble();
        System.out.print("Note (optional): ");
        String note = scanner.nextLine().trim();

        boolean success = acc.withdraw(amt, note.isBlank() ? "Withdrawal" : note);
        System.out.println(success ? "Withdrawal successful. New balance: BWP " + String.format("%.2f", acc.getBalance())
                : "Withdrawal failed (insufficient funds or withdrawals not allowed).");
    }

    private static void viewAuditTrail(Customer c) {
        System.out.println("\nTransaction Audit Trail for " + c + ":");
        for (Account a : c.getAccounts()) {
            System.out.println("\n-- Account: " + a.getAccountNumber() + " (" + a.getClass().getSimpleName() + ")");
            List<Transaction> txs = a.getTransactions();
            if (txs.isEmpty()) {
                System.out.println("   (no transactions)");
            } else {
                for (Transaction t : txs) {
                    System.out.println("   " + t);
                }
            }
        }
    }

    private static void openAccountFlow(Customer c) {
        System.out.println("\nAccount Types:");
        System.out.println("1) Savings");
        System.out.println("2) Investment (min BWP 500)");
        System.out.println("3) Cheque");
        System.out.print("Choice: ");
        String ch = scanner.nextLine().trim();
        System.out.print("Initial deposit (BWP): ");
        double init = readPositiveDouble();
        System.out.print("Branch: ");
        String branch = scanner.nextLine().trim();
        try {
            switch (ch) {
                case "1":
                    Account sa = new SavingsAccount(IdUtil.nextId("SA"), init, branch);
                    c.addAccount(sa);
                    System.out.println("Savings account opened: " + sa.getAccountNumber());
                    break;
                case "2":
                    Account ia = new InvestmentAccount(IdUtil.nextId("IA"), init, branch);
                    c.addAccount(ia);
                    System.out.println("Investment account opened: " + ia.getAccountNumber());
                    break;
                case "3":
                    System.out.print("Employer: ");
                    String emp = scanner.nextLine().trim();
                    Account ca = new ChequeAccount(IdUtil.nextId("CA"), init, branch, emp);
                    c.addAccount(ca);
                    System.out.println("Cheque account opened: " + ca.getAccountNumber());
                    break;
                default:
                    System.out.println("Invalid type.");
            }
        } catch (Exception e) {
            System.out.println("Failed to open account: " + e.getMessage());
        }
    }

    private static Account findAccount(Customer c, String accNo) {
        for (Account a : c.getAccounts()) {
            if (a.getAccountNumber().equalsIgnoreCase(accNo)) return a;
        }
        return null;
    }

    private static void applyInterestAll() {
        System.out.println("\nApplying interest to all interest-bearing accounts...");
        int count = 0;
        for (Customer c : customers.values()) {
            for (Account a : c.getAccounts()) {
                if (a instanceof InterestBearing) {
                    double applied = ((InterestBearing) a).calculateInterest();
                    System.out.println(String.format("Applied BWP %.2f interest to %s (%s)", applied, a.getAccountNumber(), a.getClass().getSimpleName()));
                    count++;
                }
            }
        }
        if (count == 0) System.out.println("No interest-bearing accounts found.");
    }

    private static double readPositiveDouble() {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                double v = Double.parseDouble(line);
                if (v <= 0) {
                    System.out.print("Please enter a positive number: ");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Try again: ");
            }
        }
    }
}

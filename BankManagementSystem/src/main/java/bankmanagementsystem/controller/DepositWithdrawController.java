package bankmanagementsystem.controller;

import bankmanagementsystem.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class DepositWithdrawController {
    @FXML private TextField accountNoField;
    @FXML private TextField amountField;
    @FXML private Label messageLabel;

    private Customer customer; // set by caller

    public void setCustomer(Customer c) {
        this.customer = c;
    }

    @FXML
    private void handleDeposit() {
        if (customer == null) { messageLabel.setText("No customer provided."); messageLabel.setStyle("-fx-text-fill: red;"); return; }
        String accNo = accountNoField.getText().trim();
        double amt;
        try {
            amt = Double.parseDouble(amountField.getText().trim());
            if (amt <= 0) throw new NumberFormatException();
        } catch (Exception e) {
            messageLabel.setText("Invalid amount.");
            messageLabel.setStyle("-fx-text-fill: red;");
            AuditLogger.log("transaction", customer.getCustomerId(), accNo, "deposit", "invalid amount", false);
            return;
        }

        Account found = findAccount(accNo);
        if (found == null) { messageLabel.setText("Account not found."); messageLabel.setStyle("-fx-text-fill: red;"); AuditLogger.log("transaction", customer.getCustomerId(), accNo, "deposit", "account not found", false); return; }
        if (found.isClosed()) { messageLabel.setText("Account is closed."); messageLabel.setStyle("-fx-text-fill: red;"); AuditLogger.log("transaction", customer.getCustomerId(), accNo, "deposit", "account closed", false); return; }

        found.deposit(amt);
        FileStorage.saveAllData(BankData.getCustomers(), BankData.getCredentials());
        AuditLogger.log("transaction", customer.getCustomerId(), accNo, "deposit", "amount=" + amt, true);
        messageLabel.setText("Deposited BWP " + String.format("%.2f", amt));
        messageLabel.setStyle("-fx-text-fill: green;");
    }

    @FXML
    private void handleWithdraw() {
        if (customer == null) { messageLabel.setText("No customer provided."); messageLabel.setStyle("-fx-text-fill: red;"); return; }
        String accNo = accountNoField.getText().trim();
        double amt;
        try {
            amt = Double.parseDouble(amountField.getText().trim());
            if (amt <= 0) throw new NumberFormatException();
        } catch (Exception e) {
            messageLabel.setText("Invalid amount.");
            messageLabel.setStyle("-fx-text-fill: red;");
            AuditLogger.log("transaction", customer.getCustomerId(), accNo, "withdraw", "invalid amount", false);
            return;
        }

        Account found = findAccount(accNo);
        if (found == null) { messageLabel.setText("Account not found."); messageLabel.setStyle("-fx-text-fill: red;"); AuditLogger.log("transaction", customer.getCustomerId(), accNo, "withdraw", "account not found", false); return; }
        if (found.isClosed()) { messageLabel.setText("Account is closed."); messageLabel.setStyle("-fx-text-fill: red;"); AuditLogger.log("transaction", customer.getCustomerId(), accNo, "withdraw", "account closed", false); return; }

        if (found instanceof Withdraw) {
            if (amt > found.getBalance()) {
                messageLabel.setText("Insufficient funds.");
                messageLabel.setStyle("-fx-text-fill: red;");
                AuditLogger.log("transaction", customer.getCustomerId(), accNo, "withdraw", "insufficient funds: balance=" + found.getBalance() + ", amount=" + amt, false);
                return;
            }
            ((Withdraw) found).withdraw(amt);
            FileStorage.saveAllData(BankData.getCustomers(), BankData.getCredentials());
            AuditLogger.log("transaction", customer.getCustomerId(), accNo, "withdraw", "amount=" + amt, true);
            messageLabel.setText("Withdrew BWP " + String.format("%.2f", amt));
            messageLabel.setStyle("-fx-text-fill: green;");
        } else {
            messageLabel.setText("Withdrawals only allowed on Investment and Cheque accounts.");
            messageLabel.setStyle("-fx-text-fill: red;");
            AuditLogger.log("transaction", customer.getCustomerId(), accNo, "withdraw", "not allowed on account type", false);
        }
    }

    private Account findAccount(String accNo) {
        for (Account a : customer.getAccounts()) {
            if (a.getAccountNumber().equalsIgnoreCase(accNo)) return a;
        }
        return null;
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) accountNoField.getScene().getWindow();
        stage.close();
    }
}

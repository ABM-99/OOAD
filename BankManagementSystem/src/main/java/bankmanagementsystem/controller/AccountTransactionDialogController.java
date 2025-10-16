package bankmanagementsystem.controller;

import bankmanagementsystem.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class AccountTransactionDialogController {
    @FXML private Label accountInfoLabel;
    @FXML private Label accountTypeLabel;
    @FXML private Label currentBalanceLabel;
    @FXML private TextField amountField;
    @FXML private Button depositButton;
    @FXML private Button withdrawButton;
    @FXML private Label messageLabel;
    @FXML private Button backButton;
    
    private Account account;
    private Customer customer;

    @FXML
    private void initialize() {
        // Set up button styles
        depositButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        withdrawButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold;");
    }

    public void setAccount(Account account) {
        this.account = account;
        updateAccountInfo();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    private void updateAccountInfo() {
        if (account != null) {
            accountInfoLabel.setText("Account: " + account.getAccountNumber());
            accountTypeLabel.setText("Type: " + account.getClass().getSimpleName());
            currentBalanceLabel.setText("Current Balance: BWP " + String.format("%.2f", account.getBalance()));
            
            // Enable/disable withdraw button based on account type
            if (account instanceof Withdraw) {
                withdrawButton.setDisable(false);
                withdrawButton.setText("Withdraw");
            } else {
                withdrawButton.setDisable(true);
                withdrawButton.setText("Withdraw (Not Available)");
            }
        }
    }

    @FXML
    private void handleDeposit() {
        if (account == null) {
            showMessage("Account not loaded.", "red");
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                showMessage("Amount must be greater than 0.", "red");
                AuditLogger.log("transaction", customer != null ? customer.getCustomerId() : "?", account != null ? account.getAccountNumber() : "?", "deposit", "invalid amount", false);
                return;
            }

            if (account.isClosed()) {
                showMessage("Account is closed.", "red");
                AuditLogger.log("transaction", customer != null ? customer.getCustomerId() : "?", account.getAccountNumber(), "deposit", "account closed", false);
                return;
            }

            account.deposit(amount);
            FileStorage.saveAllData(BankData.getCustomers(), BankData.getCredentials());
            AuditLogger.log("transaction", customer != null ? customer.getCustomerId() : "?", account.getAccountNumber(), "deposit", "amount=" + amount, true);
            
            showMessage("Deposit successful! Amount: BWP " + String.format("%.2f", amount), "green");
            updateAccountInfo();
            amountField.clear();
            
        } catch (NumberFormatException e) {
            showMessage("Please enter a valid amount.", "red");
            AuditLogger.log("transaction", customer != null ? customer.getCustomerId() : "?", account != null ? account.getAccountNumber() : "?", "deposit", "invalid amount format", false);
        }
    }

    @FXML
    private void handleWithdraw() {
        if (account == null) {
            showMessage("Account not loaded.", "red");
            return;
        }

        if (!(account instanceof Withdraw)) {
            showMessage("Withdrawals not allowed on this account type.", "red");
            AuditLogger.log("transaction", customer != null ? customer.getCustomerId() : "?", account.getAccountNumber(), "withdraw", "not allowed on type", false);
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                showMessage("Amount must be greater than 0.", "red");
                AuditLogger.log("transaction", customer != null ? customer.getCustomerId() : "?", account.getAccountNumber(), "withdraw", "invalid amount", false);
                return;
            }

            if (account.isClosed()) {
                showMessage("Account is closed.", "red");
                AuditLogger.log("transaction", customer != null ? customer.getCustomerId() : "?", account.getAccountNumber(), "withdraw", "account closed", false);
                return;
            }

            if (amount > account.getBalance()) {
                showMessage("Insufficient funds. Available balance: BWP " + String.format("%.2f", account.getBalance()), "red");
                AuditLogger.log("transaction", customer != null ? customer.getCustomerId() : "?", account.getAccountNumber(), "withdraw", "insufficient funds: balance=" + account.getBalance() + ", amount=" + amount, false);
                return;
            }

            ((Withdraw) account).withdraw(amount);
            FileStorage.saveAllData(BankData.getCustomers(), BankData.getCredentials());
            AuditLogger.log("transaction", customer != null ? customer.getCustomerId() : "?", account.getAccountNumber(), "withdraw", "amount=" + amount, true);
            
            showMessage("Withdrawal successful! Amount: BWP " + String.format("%.2f", amount), "green");
            updateAccountInfo();
            amountField.clear();
            
        } catch (NumberFormatException e) {
            showMessage("Please enter a valid amount.", "red");
            AuditLogger.log("transaction", customer != null ? customer.getCustomerId() : "?", account != null ? account.getAccountNumber() : "?", "withdraw", "invalid amount format", false);
        }
    }

    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
    }

    @FXML
    private void handleBack() throws IOException {
        // Close current window
        Stage currentStage = (Stage) backButton.getScene().getWindow();
        
        // Open customer dashboard
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomerDashboard.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Customer Dashboard");
        
        // Pass customer to dashboard controller
        CustomerDashboardController controller = loader.getController();
        controller.setCustomer(customer);
        stage.show();
        
        // Close the current window
        currentStage.close();
    }
}

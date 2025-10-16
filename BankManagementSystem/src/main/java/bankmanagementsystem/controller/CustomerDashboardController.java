package bankmanagementsystem.controller;

import bankmanagementsystem.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class CustomerDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private ListView<String> accountsList;
    @FXML private Label infoLabel;

    private Customer currentCustomer;

    @FXML
    private void initialize() {
        // This will be called when the dashboard loads
        // The customer will be set by the login controller
    }

    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
        if (customer != null) {
            System.out.println("Setting customer: " + customer.getFirstName() + " " + customer.getLastName());
            welcomeLabel.setText("Welcome, " + customer.getFirstName() + " " + customer.getLastName());
            refreshAccountsList();
        } else {
            System.out.println("Customer is null!");
        }
    }

    // Public method to refresh accounts (can be called from other windows)
    public void refreshAccounts() {
        refreshAccountsList();
    }

    @FXML
    private void handleAccountClick() {
        String selectedAccount = accountsList.getSelectionModel().getSelectedItem();
        if (selectedAccount == null || selectedAccount.equals("No accounts found for this customer")) {
            return;
        }
        
        // Extract account number from the selected item
        String accountNumber = selectedAccount.split(" \\| ")[0];
        Account account = findAccountByNumber(accountNumber);
        
        if (account != null) {
            showTransactionDialog(account);
        }
    }

    private Account findAccountByNumber(String accountNumber) {
        for (Account account : currentCustomer.getAccounts()) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    private void showTransactionDialog(Account account) {
        try {
            // Close current window
            Stage currentStage = (Stage) welcomeLabel.getScene().getWindow();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AccountTransactionDialog.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Account Transaction - " + account.getAccountNumber());
            
            // Pass account to the transaction dialog controller
            AccountTransactionDialogController controller = loader.getController();
            controller.setAccount(account);
            controller.setCustomer(currentCustomer);
            stage.show();
            
            // Close the current window
            currentStage.close();
        } catch (IOException e) {
            infoLabel.setText("Error opening transaction dialog: " + e.getMessage());
            infoLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void refreshAccountsList() {
        if (currentCustomer == null) {
            System.out.println("Customer is null, cannot refresh accounts");
            return;
        }
        
        System.out.println("Refreshing accounts for customer: " + currentCustomer.getFirstName());
        System.out.println("Customer has " + currentCustomer.getAccounts().size() + " accounts");
        
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Account a : currentCustomer.getAccounts()) {
            String accountType = a.getClass().getSimpleName();
            String accountInfo = String.format("%s | %s | Balance: BWP %.2f | Branch: %s", 
                a.getAccountNumber(), accountType, a.getBalance(), a.getBranch());
            items.add(accountInfo);
            System.out.println("Added account: " + accountInfo);
        }
        
        if (items.isEmpty()) {
            items.add("No accounts found for this customer");
            System.out.println("No accounts found for customer");
        }
        
        accountsList.setItems(items);
        System.out.println("Accounts list updated with " + items.size() + " items");
    }

    @FXML
    private void handleAddAccount() throws IOException {
        if (currentCustomer == null) {
            infoLabel.setText("Customer not loaded.");
            infoLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        // Close current window
        Stage currentStage = (Stage) welcomeLabel.getScene().getWindow();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomerOpenAccountView.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Open New Account");
        
        // Pass customer to the new account controller
        CustomerOpenAccountController controller = loader.getController();
        controller.setCustomer(currentCustomer);
        stage.show();
        
        // Close the current window
        currentStage.close();
    }



    private void showAccountCreationDialog() {
        try {
            // Close current window
            Stage currentStage = (Stage) welcomeLabel.getScene().getWindow();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomerOpenAccountView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Create New Account");
            
            // Pass customer to the new account controller
            CustomerOpenAccountController controller = loader.getController();
            controller.setCustomer(currentCustomer);
            stage.show();
            
            // Close the current window
            currentStage.close();
        } catch (IOException e) {
            infoLabel.setText("Error opening account creation window: " + e.getMessage());
            infoLabel.setStyle("-fx-text-fill: red;");
        }
    }



    @FXML
    private void handleLogout() throws IOException {
        // Close current window
        Stage currentStage = (Stage) welcomeLabel.getScene().getWindow();
        
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Bank Management System - Login");
        stage.show();
        
        // Close the current window
        currentStage.close();
    }
}

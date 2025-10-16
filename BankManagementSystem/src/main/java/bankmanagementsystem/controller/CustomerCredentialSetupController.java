package bankmanagementsystem.controller;

import bankmanagementsystem.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class CustomerCredentialSetupController {
    @FXML private TextField customerIdField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField emailField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private Label customerInfoLabel;
    @FXML private Button setupButton;
    @FXML private Button backToLoginButton;

    @FXML
    private void initialize() {
        // Initialize the form
        customerInfoLabel.setText("Enter your Customer ID to begin setup");
    }

    @FXML
    private void handleSetupCredentials() {
        // Clear previous messages
        errorLabel.setText("");
        successLabel.setText("");

        // Validate input
        String errorMessage = validateInput();
        if (!errorMessage.isEmpty()) {
            errorLabel.setText(errorMessage);
            return;
        }

        // Get input values
        String customerId = customerIdField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String email = emailField.getText().trim();

        // Check if customer exists
        Customer customer = BankData.findCustomerById(customerId);
        if (customer == null) {
            errorLabel.setText("Customer ID not found. Please check your Customer ID or contact support.");
            return;
        }

        // Check if customer already has credentials
        CustomerCredentials existingCreds = BankData.getCustomerCredentials(username);
        if (existingCreds != null) {
            errorLabel.setText("Username already taken. Please choose a different username.");
            return;
        }

        // Check if email is already used
        if (!BankData.isEmailAvailable(email)) {
            errorLabel.setText("Email already registered. Please use a different email.");
            return;
        }

        // Setup credentials using BankData method
        String result = BankData.setupCustomerCredentials(customerId, username, password, email);
        
        if (result.startsWith("Login credentials set up successfully")) {
            successLabel.setText("Login credentials set up successfully!\n" +
                               "You can now login with:\n" +
                               "Username: " + username + "\n" +
                               "Password: [your password]");
        } else {
            errorLabel.setText(result);
            return;
        }
        
        clearFields();
    }

    @FXML
    private void handleVerifyCustomerId() {
        String customerId = customerIdField.getText().trim();
        if (customerId.isEmpty()) {
            customerInfoLabel.setText("Enter your Customer ID to begin setup");
            return;
        }

        Customer customer = BankData.findCustomerById(customerId);
        if (customer == null) {
            customerInfoLabel.setText("Customer ID not found. Please check your Customer ID.");
            customerInfoLabel.setStyle("-fx-text-fill: red;");
        } else {
            customerInfoLabel.setText("Customer found: " + customer.getFirstName() + " " + customer.getLastName() + 
                                    " (" + customerId + ")");
            customerInfoLabel.setStyle("-fx-text-fill: green;");
        }
    }

    private String validateInput() {
        if (customerIdField.getText().trim().isEmpty()) {
            return "Customer ID is required.";
        }
        if (usernameField.getText().trim().isEmpty()) {
            return "Username is required.";
        }
        if (passwordField.getText().isEmpty()) {
            return "Password is required.";
        }
        if (confirmPasswordField.getText().isEmpty()) {
            return "Please confirm your password.";
        }
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            return "Passwords do not match.";
        }
        if (emailField.getText().trim().isEmpty()) {
            return "Email is required.";
        }

        // Basic email validation
        String email = emailField.getText().trim();
        if (!email.contains("@") || !email.contains(".")) {
            return "Please enter a valid email address.";
        }

        // Password strength validation
        String password = passwordField.getText();
        if (password.length() < 6) {
            return "Password must be at least 6 characters long.";
        }

        return "";
    }

    private void clearFields() {
        customerIdField.clear();
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        emailField.clear();
        customerInfoLabel.setText("Enter your Customer ID to begin setup");
        customerInfoLabel.setStyle("-fx-text-fill: black;");
    }

    @FXML
    private void handleBackToLogin() throws IOException {
        Stage stage = (Stage) backToLoginButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Bank Management System - Login");
    }
}

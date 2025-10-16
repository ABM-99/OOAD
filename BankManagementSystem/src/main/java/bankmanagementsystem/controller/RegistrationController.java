package bankmanagementsystem.controller;

import bankmanagementsystem.model.BankData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class RegistrationController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField addressField;
    @FXML private ComboBox<String> customerTypeCombo;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField emailField;
    @FXML private TextField additionalInfoField;
    @FXML private Label additionalInfoLabel;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private Button registerButton;
    @FXML private Button backToLoginButton;

    @FXML
    private void initialize() {
        // Initialize customer type combo box
        customerTypeCombo.getItems().addAll("PERSONAL", "COMPANY");
        customerTypeCombo.setValue("PERSONAL");
        
        // Set initial additional info label
        updateAdditionalInfoLabel();
        
        // Add listener to customer type combo box
        customerTypeCombo.setOnAction(event -> updateAdditionalInfoLabel());
    }

    private void updateAdditionalInfoLabel() {
        String selectedType = customerTypeCombo.getValue();
        if ("PERSONAL".equals(selectedType)) {
            additionalInfoLabel.setText("National ID:");
            additionalInfoField.setPromptText("Enter your National ID");
        } else if ("COMPANY".equals(selectedType)) {
            additionalInfoLabel.setText("Company Name:");
            additionalInfoField.setPromptText("Enter company name");
        }
    }

    @FXML
    private void handleRegister() {
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
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String address = addressField.getText().trim();
        String customerType = customerTypeCombo.getValue();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String email = emailField.getText().trim();
        String additionalInfo = additionalInfoField.getText().trim();

        // For company customers, we need both company name and address
        if ("COMPANY".equals(customerType)) {
            // In a real system, you'd have separate fields for company name and address
            // For now, we'll use the additionalInfo field for company name only
            additionalInfo = additionalInfo + "|"; // Empty company address for now
        }

        // Register customer
        String result = BankData.registerCustomer(firstName, lastName, address, customerType, 
                                                username, password, email, additionalInfo);

        if (result.startsWith("Registration successful")) {
            successLabel.setText(result);
            clearFields();
        } else {
            errorLabel.setText(result);
        }
    }

    private String validateInput() {
        if (firstNameField.getText().trim().isEmpty()) {
            return "First name is required.";
        }
        if (lastNameField.getText().trim().isEmpty()) {
            return "Last name is required.";
        }
        if (addressField.getText().trim().isEmpty()) {
            return "Address is required.";
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
        if (additionalInfoField.getText().trim().isEmpty()) {
            return additionalInfoLabel.getText().replace(":", "") + " is required.";
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
        firstNameField.clear();
        lastNameField.clear();
        addressField.clear();
        customerTypeCombo.setValue("PERSONAL");
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        emailField.clear();
        additionalInfoField.clear();
    }

    @FXML
    private void handleBackToLogin() throws IOException {
        Stage stage = (Stage) backToLoginButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Bank Management System - Login");
    }
}

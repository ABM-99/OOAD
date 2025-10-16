package bankmanagementsystem.controller;

import bankmanagementsystem.model.BankData;
import bankmanagementsystem.model.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button setupCredentialsButton;

    @FXML
    private void handleLogin() throws IOException {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        Stage stage = (Stage) usernameField.getScene().getWindow();

        if (BankData.isEmployee(user, pass)) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EmployeeDashboard.fxml"));
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Employee Dashboard");
        } else if (BankData.isCustomer(user, pass)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomerDashboard.fxml"));
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Customer Dashboard");
                
                // Pass customer to dashboard controller
                CustomerDashboardController dashboardController = loader.getController();
                Customer customer = BankData.getCustomerByUsername(user);
                if (customer != null) {
                    dashboardController.setCustomer(customer);
                } else {
                    errorLabel.setText("Customer data not found!");
                }
            } catch (Exception e) {
                errorLabel.setText("Error loading customer dashboard: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Invalid credentials!");
        }
    }

    @FXML
    private void handleSetupCredentials() throws IOException {
        Stage stage = (Stage) setupCredentialsButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomerCredentialSetupView.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Bank Management System - Setup Credentials");
    }
}

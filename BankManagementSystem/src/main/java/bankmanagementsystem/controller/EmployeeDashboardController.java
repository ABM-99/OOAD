package bankmanagementsystem.controller;

import bankmanagementsystem.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;

public class EmployeeDashboardController {
    @FXML private Button registerCustomerButton;
    @FXML private Button viewCustomersButton;
    @FXML private Button viewAuditButton;

    @FXML
    private void handleRegisterCustomer() throws IOException {
        // Close current window
        Stage currentStage = (Stage) registerCustomerButton.getScene().getWindow();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EmployeeCustomerCreationView.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Create Customer Account");
        stage.show();
        
        // Close the current window
        currentStage.close();
    }


    @FXML
    private void handleViewCustomers() throws IOException {
        // Close current window
        Stage currentStage = (Stage) viewCustomersButton.getScene().getWindow();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ViewCustomersView.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("View All Customers");
        stage.show();
        
        // Close the current window
        currentStage.close();
    }

    @FXML
    private void handleViewAuditLog() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AuditLogView.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Audit Log");
        stage.show();
    }

    @FXML
    private void handleApplyInterest() {
        BankData.applyAutomaticInterest();
    }

    @FXML
    private void handleSaveData() {
        DatabaseStorage.saveAllData(BankData.getCustomers(), BankData.getCredentials());
    }

    @FXML
    private void handleLogout() throws IOException {
        // Close current window
        Stage currentStage = (Stage) registerCustomerButton.getScene().getWindow();
        
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Bank Management System - Login");
        stage.show();
        
        // Close the current window
        currentStage.close();
    }
}

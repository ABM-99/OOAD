package bankmanagementsystem.controller;

import bankmanagementsystem.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewCustomersController {
    @FXML private ListView<String> customersList;
    @FXML private ListView<String> accountsList;
    @FXML private Label customerInfoLabel;
    @FXML private Button refreshButton;
    @FXML private Button backToEmployeeButton;

    private Customer selectedCustomer;

    @FXML
    private void initialize() {
        refreshCustomersList();
    }

    @FXML
    private void handleRefresh() {
        refreshCustomersList();
        customerInfoLabel.setText("Select a customer to view their accounts");
        accountsList.getItems().clear();
    }

    @FXML
    private void handleCustomerSelection() {
        String selectedItem = customersList.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            System.out.println("Selected item: " + selectedItem);
            try {
                // Extract customer ID from the selected item - more robust parsing
                int idStart = selectedItem.indexOf("ID: ");
                int idEnd = selectedItem.indexOf(" |", idStart);
                
                if (idStart == -1 || idEnd == -1) {
                    System.out.println("Could not parse customer ID from: " + selectedItem);
                    customerInfoLabel.setText("Error: Could not parse customer ID");
                    customerInfoLabel.setStyle("-fx-text-fill: red;");
                    return;
                }
                
                String customerId = selectedItem.substring(idStart + 4, idEnd);
                System.out.println("Selected Customer ID: " + customerId);
                selectedCustomer = BankData.findCustomerById(customerId);
                
                if (selectedCustomer != null) {
                    System.out.println("Customer found: " + selectedCustomer.getFirstName() + " " + selectedCustomer.getLastName());
                    displayCustomerInfo();
                    refreshAccountsList();
                } else {
                    System.out.println("Customer not found for ID: " + customerId);
                    customerInfoLabel.setText("Customer not found for ID: " + customerId);
                    customerInfoLabel.setStyle("-fx-text-fill: red;");
                }
            } catch (Exception e) {
                System.out.println("Error parsing customer selection: " + e.getMessage());
                customerInfoLabel.setText("Error selecting customer: " + e.getMessage());
                customerInfoLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }

    private void refreshCustomersList() {
        ObservableList<String> customerItems = FXCollections.observableArrayList();
        
        for (Customer customer : BankData.getCustomers()) {
            String customerType = customer instanceof PersonalCustomer ? "Personal" : "Company";
            String item = customer.getFirstName() + " " + customer.getLastName() + 
                         " | ID: " + customer.getCustomerId() + 
                         " | Type: " + customerType + 
                         " | Accounts: " + customer.getAccounts().size();
            customerItems.add(item);
        }
        
        customersList.setItems(customerItems);
    }

    private void displayCustomerInfo() {
        if (selectedCustomer != null) {
            String customerType = selectedCustomer instanceof PersonalCustomer ? "Personal" : "Company";
            String info = "Customer: " + selectedCustomer.getFirstName() + " " + selectedCustomer.getLastName() + 
                         "\nID: " + selectedCustomer.getCustomerId() + 
                         "\nType: " + customerType + 
                         "\nAddress: " + selectedCustomer.getAddress();
            
            if (selectedCustomer instanceof PersonalCustomer) {
                PersonalCustomer pc = (PersonalCustomer) selectedCustomer;
                info += "\nNational ID: " + pc.getNationalId();
            } else if (selectedCustomer instanceof CompanyCustomer) {
                CompanyCustomer cc = (CompanyCustomer) selectedCustomer;
                info += "\nCompany: " + cc.getCompanyName() + ", " + cc.getCompanyAddress();
            }
            
            customerInfoLabel.setText(info);
        }
    }

    private void refreshAccountsList() {
        if (selectedCustomer == null) return;
        
        ObservableList<String> accountItems = FXCollections.observableArrayList();
        
        for (Account account : selectedCustomer.getAccounts()) {
            String accountType = account.getClass().getSimpleName();
            String item = account.getAccountNumber() + " | " + accountType + 
                         " | Balance: BWP " + String.format("%.2f", account.getBalance()) + 
                         " | Branch: " + account.getBranch();
            accountItems.add(item);
        }
        
        accountsList.setItems(accountItems);
    }

    @FXML
    private void handleBackToEmployee() throws IOException {
        Stage stage = (Stage) backToEmployeeButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EmployeeDashboard.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Employee Dashboard");
    }
}

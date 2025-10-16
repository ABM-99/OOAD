package bankmanagementsystem.controller;

import bankmanagementsystem.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class CustomerOpenAccountController {
    @FXML private ComboBox<String> accountTypeCombo;
    @FXML private ComboBox<String> branchCombo;
    @FXML private TextField initialBalanceField;
    @FXML private TextField employerNameField;
    @FXML private TextField employerAddressField;
    @FXML private VBox employmentDetailsBox;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private Button createButton;
    @FXML private Button backButton;
    
    private Customer customer;

    @FXML
    private void initialize() {
        // Initialize account type combo box
        accountTypeCombo.getItems().addAll("SavingsAccount", "InvestmentAccount", "ChequeAccount");
        accountTypeCombo.setValue("SavingsAccount");
        
        // Initialize branches
        branchCombo.getItems().addAll("main", "kopong", "Maun", "F-town", "Mogoditshane", "Molepolole");
        branchCombo.setValue("main");
        initialBalanceField.setText("0.0");
        
        // Add listener to account type combo box
        accountTypeCombo.setOnAction(event -> { updateEmploymentDetailsVisibility(); event.consume(); });
        
        // Set initial visibility
        updateEmploymentDetailsVisibility();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null) {
            System.out.println("Customer set for new account: " + customer.getFirstName() + " " + customer.getLastName());
        }
    }

    private void updateEmploymentDetailsVisibility() {
        String accountType = accountTypeCombo.getValue();
        if ("ChequeAccount".equals(accountType)) {
            employmentDetailsBox.setVisible(true);
            employmentDetailsBox.setManaged(true);
        } else {
            employmentDetailsBox.setVisible(false);
            employmentDetailsBox.setManaged(false);
        }
    }

    @FXML
    private void handleCreateAccount() {
        errorLabel.setText("");
        successLabel.setText("");

        if (customer == null) {
            errorLabel.setText("Customer not loaded.");
            return;
        }

        String errorMessage = validateInput();
        if (!errorMessage.isEmpty()) {
            errorLabel.setText(errorMessage);
            return;
        }

        // Get input values
        String accountType = accountTypeCombo.getValue();
        String branch = branchCombo.getValue();
        double initialBalance;
        
        try {
            initialBalance = Double.parseDouble(initialBalanceField.getText().trim());
            if (initialBalance < 0) {
                errorLabel.setText("Initial balance cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Please enter a valid initial balance.");
            return;
        }

        // Validate business rules
        String businessRuleError = validateBusinessRules(accountType, initialBalance);
        if (!businessRuleError.isEmpty()) {
            errorLabel.setText(businessRuleError);
            return;
        }

        // Get employment details for Cheque accounts
        String employerName = "";
        String employerAddress = "";
        if ("ChequeAccount".equals(accountType)) {
            employerName = employerNameField.getText().trim();
            employerAddress = employerAddressField.getText().trim();
        }

        // Create account for the customer
        String result = createAccountForCustomer(accountType, branch, initialBalance, employerName, employerAddress);

        if (result.startsWith("Account created successfully")) {
            successLabel.setText(result);
            clearFields();
            
            // Show success message and close window after a delay
            successLabel.setText(result + "\n\nWindow will close in 3 seconds...");
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    javafx.application.Platform.runLater(() -> {
                        Stage stage = (Stage) createButton.getScene().getWindow();
                        stage.close();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            errorLabel.setText(result);
        }
    }

    private String validateBusinessRules(String accountType, double initialBalance) {
        // Investment account minimum balance rule
        if ("InvestmentAccount".equals(accountType) && initialBalance < 500) {
            return "Investment accounts require a minimum deposit of BWP 500.";
        }
        
        // Cheque account employment details rule
        if ("ChequeAccount".equals(accountType)) {
            if (employerNameField.getText().trim().isEmpty()) {
                return "Employer name is required for Cheque accounts.";
            }
            if (employerAddressField.getText().trim().isEmpty()) {
                return "Employer address is required for Cheque accounts.";
            }
        }
        
        return "";
    }

    private String createAccountForCustomer(String accountType, String branch, double initialBalance, String employerName, String employerAddress) {
        if (customer == null) {
            return "Customer not found.";
        }

        String accountNumber = generateAccountNumber();
        Account account;
        
        switch (accountType) {
            case "SavingsAccount":
                account = new SavingsAccount(accountNumber, customer, branch, initialBalance);
                break;
            case "ChequeAccount":
                account = new ChequeAccount(accountNumber, customer, branch, initialBalance, employerName, employerAddress);
                break;
            case "InvestmentAccount":
                account = new InvestmentAccount(accountNumber, customer, branch, initialBalance);
                break;
            default:
                return "Invalid account type.";
        }

        // Add account to customer
        customer.addAccount(account);
        
        // Save data
        FileStorage.saveAllData(BankData.getCustomers(), BankData.getCredentials());

        return "Account created successfully! Account Number: " + accountNumber + 
               "\nAccount Type: " + accountType + 
               "\nInitial Balance: BWP " + String.format("%.2f", initialBalance);
    }

    private String generateAccountNumber() {
        return "ACC" + String.format("%06d", System.currentTimeMillis() % 1000000);
    }

    private String validateInput() {
        if (accountTypeCombo.getValue() == null) {
            return "Please select an account type.";
        }
        if (branchCombo.getValue() == null || branchCombo.getValue().trim().isEmpty()) {
            return "Branch is required.";
        }
        if (initialBalanceField.getText().trim().isEmpty()) {
            return "Initial balance is required.";
        }

        // Additional validation for Cheque accounts
        if ("ChequeAccount".equals(accountTypeCombo.getValue())) {
            if (employerNameField.getText().trim().isEmpty()) {
                return "Employer name is required for Cheque accounts.";
            }
            if (employerAddressField.getText().trim().isEmpty()) {
                return "Employer address is required for Cheque accounts.";
            }
        }

        return "";
    }

    private void clearFields() {
        accountTypeCombo.setValue("SavingsAccount");
        initialBalanceField.setText("0.0");
        branchCombo.setValue("main");
        employerNameField.clear();
        employerAddressField.clear();
        updateEmploymentDetailsVisibility();
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

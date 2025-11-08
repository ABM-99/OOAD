package bankmanagementsystem.controller;

import bankmanagementsystem.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class EmployeeCustomerCreationController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField addressField;
    @FXML private ComboBox<String> customerTypeCombo;
    @FXML private TextField additionalInfoField;
    @FXML private Label additionalInfoLabel;
    @FXML private ComboBox<String> accountTypeCombo;
    @FXML private TextField initialBalanceField;
    @FXML private ComboBox<String> branchCombo;
    @FXML private VBox employmentDetailsBox;
    @FXML private TextField employerNameField;
    @FXML private TextField employerAddressField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private Button createButton;
    @FXML private Button backToEmployeeButton;

    @FXML
    private void initialize() {
        // Initialize customer type combo box
        customerTypeCombo.getItems().addAll("PERSONAL", "COMPANY");
        customerTypeCombo.setValue("PERSONAL");
        
        // Initialize account type combo box
        accountTypeCombo.getItems().addAll("SavingsAccount", "ChequeAccount", "InvestmentAccount");
        accountTypeCombo.setValue("SavingsAccount");
        
        // Set initial values
        initialBalanceField.setText("0.0");
        branchCombo.getItems().addAll("main", "kopong", "Maun", "F-town", "Mogoditshane", "Molepolole");
        branchCombo.setValue("main");
        
        // Set initial additional info label
        updateAdditionalInfoLabel();
        
        // Add listener to customer type combo box
        customerTypeCombo.setOnAction(event -> { updateAdditionalInfoLabel(); event.consume(); });
        
        // Add listener to account type combo box
        accountTypeCombo.setOnAction(event -> { updateEmploymentDetailsVisibility(); event.consume(); });
        
        // Set initial visibility
        updateEmploymentDetailsVisibility();
    }

    private void updateAdditionalInfoLabel() {
        String selectedType = customerTypeCombo.getValue();
        if ("PERSONAL".equals(selectedType)) {
            additionalInfoLabel.setText("National ID:");
            additionalInfoField.setPromptText("Enter customer's National ID");
        } else if ("COMPANY".equals(selectedType)) {
            additionalInfoLabel.setText("Company Name:");
            additionalInfoField.setPromptText("Enter company name");
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
    private void handleCreateCustomer() {
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
        String additionalInfo = additionalInfoField.getText().trim();
        String accountType = accountTypeCombo.getValue();
        String branch = branchCombo.getValue();
        
        // Parse initial balance
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
        
        // Create customer account and get customer ID directly
        String customerId = BankData.createCustomerAccountAndGetId(firstName, lastName, address, customerType, additionalInfo);
        
        if (customerId == null) {
            errorLabel.setText("Invalid customer type.");
            return;
        }
        
        // Create account for the customer
        String accountResult = createAccountForCustomer(customerId, accountType, branch, initialBalance, employerName, employerAddress);
        
        if (accountResult.startsWith("Account created successfully")) {
            String result = "Customer account created successfully! Customer ID: " + customerId;
            successLabel.setText(result + "\n" + accountResult);
        } else {
            String result = "Customer account created successfully! Customer ID: " + customerId;
            errorLabel.setText(result + "\n" + accountResult);
        }
        clearFields();
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

    private String createAccountForCustomer(String customerId, String accountType, String branch, double initialBalance, String employerName, String employerAddress) {
        Customer customer = BankData.findCustomerById(customerId);
        if (customer == null) {
            return "Customer not found.";
        }

        // Generate account number
        String accountNumber = generateAccountNumber(accountType);
        
        // Create account based on type
        Account account;
        switch (accountType) {
            case "SavingsAccount":
                account = new SavingsAccount(accountNumber, customer, branch, initialBalance);
                break;
            case "ChequeAccount":
                // For cheque accounts, we need employer info
                account = new ChequeAccount(accountNumber, customer, branch, initialBalance, employerName, employerAddress);
                break;
            case "InvestmentAccount":
                account = new InvestmentAccount(accountNumber, customer, branch, initialBalance);
                break;
            default:
                return "Invalid account type.";
        }

        // Add account to customer and save to database
        BankData.addAccountToCustomer(customerId, account);
        
        // Ensure account is saved directly to database
        AccountDAO.saveAccount(account);

        return "Account created successfully! Account Number: " + accountNumber;
    }

    private String generateAccountNumber(String accountType) {
        // Simple account number generation
        String prefix = accountType.substring(0, 2).toUpperCase(); // SA, CA, IA
        int count = 0;
        for (Customer customer : BankData.getCustomers()) {
            count += customer.getAccounts().size();
        }
        return prefix + String.format("%04d", count + 1);
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
        if (additionalInfoField.getText().trim().isEmpty()) {
            return additionalInfoLabel.getText().replace(":", "") + " is required.";
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
        firstNameField.clear();
        lastNameField.clear();
        addressField.clear();
        customerTypeCombo.setValue("PERSONAL");
        additionalInfoField.clear();
        accountTypeCombo.setValue("SavingsAccount");
        initialBalanceField.setText("0.0");
        branchCombo.setValue("main");
        employerNameField.clear();
        employerAddressField.clear();
        updateEmploymentDetailsVisibility();
    }

    @FXML
    private void handleBackToEmployee() throws IOException {
        Stage stage = (Stage) backToEmployeeButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EmployeeDashboard.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Employee Dashboard");
    }
}

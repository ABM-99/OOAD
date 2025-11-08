package bankmanagementsystem.controller;

import bankmanagementsystem.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
// FXMLLoader and Scene imports removed as they are not used in this controller

// IOException import removed as it is not used in this controller

public class OpenAccountController {
    @FXML private ChoiceBox<String> custTypeChoice;
    @FXML private ChoiceBox<String> accTypeChoice;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField addressField;
    @FXML private TextField nationalIdField;
    @FXML private TextField companyNameField;
    @FXML private TextField companyAddressField;
    @FXML private TextField employerNameField;
    @FXML private TextField employerAddressField;
    @FXML private ComboBox<String> branchChoice;
    @FXML private TextField openingBalanceField;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        custTypeChoice.getItems().addAll("Personal", "Company");
        accTypeChoice.getItems().addAll("Savings", "Investment", "Cheque");
        // set defaults
        custTypeChoice.setValue("Personal");
        accTypeChoice.setValue("Savings");
        branchChoice.getItems().addAll("main", "kopong", "Maun", "F-town", "Mogoditshane", "Molepolole");
        branchChoice.setValue("main");
        messageLabel.setText("");
    }

    @FXML
    private void handleCreateAccount() {
        String custType = custTypeChoice.getValue();
        String accType = accTypeChoice.getValue();

        // validate opening balance
        double openingBalance;
        try {
            openingBalance = Double.parseDouble(openingBalanceField.getText().trim());
            if (openingBalance < 0) throw new NumberFormatException();
        } catch (Exception e) {
            messageLabel.setText("Invalid opening balance.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Build customer
        Customer customer;
        String custId = IdUtil.nextId("CUST");
        if ("Personal".equals(custType)) {
            // minimal validation
            if (firstNameField.getText().isBlank() || lastNameField.getText().isBlank()) {
                messageLabel.setText("First and last names are required.");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }
            customer = new PersonalCustomer(custId,
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    addressField.getText().trim(),
                    nationalIdField.getText().trim());
        } else {
            if (companyNameField.getText().isBlank()) {
                messageLabel.setText("Company name is required.");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }
            customer = new CompanyCustomer(custId,
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    addressField.getText().trim(),
                    companyNameField.getText().trim(),
                    companyAddressField.getText().trim());
        }

        // Create account based on type and rules
        String accNo = IdUtil.nextId("ACC");
        Account account = null;

        try {
            switch (accType) {
                case "Savings" -> {
                    account = new SavingsAccount(accNo, customer, branchChoice.getValue(), openingBalance);
                }
                case "Investment" -> {
                    if (openingBalance < 500.0) {
                        messageLabel.setText("Investment account requires minimum opening balance of BWP 500.");
                        messageLabel.setStyle("-fx-text-fill: red;");
                        return;
                    }
                    account = new InvestmentAccount(accNo, customer, branchChoice.getValue(), openingBalance);
                }
                case "Cheque" -> {
                    if (employerNameField.getText().isBlank() || employerAddressField.getText().isBlank()) {
                        messageLabel.setText("Employer details required for Cheque account.");
                        messageLabel.setStyle("-fx-text-fill: red;");
                        return;
                    }
                    account = new ChequeAccount(accNo, customer, branchChoice.getValue(), openingBalance,
                            employerNameField.getText().trim(), employerAddressField.getText().trim());
                }
            }
        } catch (IllegalArgumentException ex) {
            messageLabel.setText("Failed: " + ex.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Add account to customer and save
        customer.addAccount(account);
        BankData.addCustomer(customer);
        DatabaseStorage.saveData(BankData.getCustomers()); // persist to text file
        messageLabel.setText("Account created: " + accNo + " for " + customer.getFirstName() + " " + customer.getLastName());
        messageLabel.setStyle("-fx-text-fill: green;");
        clearForm();
    }

    private void clearForm() {
        firstNameField.clear(); lastNameField.clear(); addressField.clear();
        nationalIdField.clear(); companyNameField.clear(); companyAddressField.clear();
        employerNameField.clear(); employerAddressField.clear(); branchChoice.setValue("main");
        openingBalanceField.clear();
    }

    @FXML
    private void handleCancel() {
        // close current window
        Stage stage = (Stage) custTypeChoice.getScene().getWindow();
        stage.close();
    }
}

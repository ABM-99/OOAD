package bankmanagementsystem.model;

public class CompanyCustomer extends Customer {
    private String companyName;
    private String companyAddress;

    public CompanyCustomer(String customerId, String firstName, String lastName, String address, String companyName, String companyAddress) {
        super(customerId, firstName, lastName, address);
        this.companyName = companyName;
        this.companyAddress = companyAddress;
    }

    @Override
    public void displayCustomerInfo() {
        super.displayCustomerInfo();
        System.out.println("Company: " + companyName + ", " + companyAddress);
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }
}

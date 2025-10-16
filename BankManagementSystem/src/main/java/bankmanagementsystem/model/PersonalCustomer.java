package bankmanagementsystem.model;

public class PersonalCustomer extends Customer {
    private String nationalId;

    public PersonalCustomer(String customerId, String firstName, String lastName, String address, String nationalId) {
        super(customerId, firstName, lastName, address);
        this.nationalId = nationalId;
    }

    @Override
    public void displayCustomerInfo() {
        super.displayCustomerInfo();
        System.out.println("National ID: " + nationalId);
    }

    public String getNationalId() {
        return nationalId;
    }
}

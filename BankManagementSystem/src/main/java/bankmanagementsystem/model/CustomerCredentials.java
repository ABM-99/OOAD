package bankmanagementsystem.model;

/**
 * Represents customer login credentials
 */
public class CustomerCredentials {
    private String customerId;
    private String username;
    private String password;
    private String email;
    private boolean isActive;

    public CustomerCredentials(String customerId, String username, String password, String email) {
        this.customerId = customerId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.isActive = true;
    }

    // Getters
    public String getCustomerId() {
        return customerId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return isActive;
    }

    // Setters
    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    // Validation methods
    public boolean validatePassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public boolean isUsernameAvailable(String username) {
        return !this.username.equals(username);
    }

    @Override
    public String toString() {
        return "CustomerCredentials{" +
                "customerId='" + customerId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}

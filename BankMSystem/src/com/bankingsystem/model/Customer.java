package com.bankingsystem.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Customer entity: holds personal info and accounts.
 */
public class Customer {
    private final String customerId;
    private String firstName;
    private String surname;
    private String address;
    private final List<Account> accounts = new ArrayList<>();

    public Customer(String customerId, String firstName, String surname, String address) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.surname = surname;
        this.address = address;
    }

    public String getCustomerId() { return customerId; }
    public String getFirstName() { return firstName; }
    public String getSurname() { return surname; }
    public String getAddress() { return address; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setAddress(String address) { this.address = address; }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(new ArrayList<>(accounts));
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s)", firstName, surname, customerId);
    }
}

package com.bankingsystem.model;

/**
 * Interface for accounts that earn interest.
 */
public interface InterestBearing {
    /**
     * Calculates interest and applies it to the account balance.
     * @return interest amount applied
     */
    double calculateInterest();
}

package com.training.validation.demo.transports;

import com.training.validation.demo.common.AccountNumber;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

/**
 * Data transport object that contains the current balance of a given bank account.
 */
public class AccountBalance {

    //strive to design immutable DTOs
    private final AccountNumber accountNumber;
    private final double balance;

    public AccountBalance(AccountNumber accountNumber, double balance) {
        Objects.requireNonNull(accountNumber, "The account number must not be null");
        if(balance < 0) {
            throw new IllegalArgumentException("The balance must be > 0: " + balance);
        }
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                       .append("accountNumber", accountNumber)
                       .append("balance", balance)
                       .toString();
    }
}

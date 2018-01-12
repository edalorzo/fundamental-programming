package com.training.validation.demo.api;

import com.training.validation.demo.common.AccountNumber;

/**
 * Thrown when any unexpected error occurs during a bank account transaction.
 */
public class SavingsAccountException extends RuntimeException {

    //all SavingsAccountException are characterized by the account number.
    private final AccountNumber accountNumber;

    public SavingsAccountException(AccountNumber accountNumber) {
        this.accountNumber = accountNumber;
    }

    public SavingsAccountException(String message, AccountNumber accountNumber, Throwable cause) {
        super(message, cause);
        this.accountNumber = accountNumber;
    }

    public SavingsAccountException(AccountNumber accountNumber, Throwable cause) {
        super(cause);
        this.accountNumber = accountNumber;
    }

    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    //the importance of overriding getMessage
    @Override
    public String getMessage() {
        return String.format("Failure to execute operation on account '%s'", accountNumber);
    }
}

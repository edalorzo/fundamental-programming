package com.training.validation.demo.api;

import com.training.validation.demo.common.AccountNumber;

/**
 * Thrown when a bank account cannot be found for a given account number.
 */
public class BankAccountNotFoundException extends SavingsAccountException {


    public BankAccountNotFoundException(AccountNumber accountNumber) {
        super(accountNumber);
    }

    //the importance of overriding getMessage
    @Override
    public String getMessage() {
        return String.format("The bank account number '%s' does not exist!", getAccountNumber());
    }
}

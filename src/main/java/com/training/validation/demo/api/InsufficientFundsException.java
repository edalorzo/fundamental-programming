package com.training.validation.demo.api;

import com.training.validation.demo.common.AccountNumber;

/**
 * Thrown when the bank account does not have sufficient funds to satisfy
 * an operation, e.g. a withdrawal.
 */
public class InsufficientFundsException extends SavingsAccountException {

    private final double balance;
    private final double withdrawal;

    //stores contextual details
    public InsufficientFundsException(AccountNumber accountNumber, double balance, double withdrawal) {
        super(accountNumber);
        this.balance = balance;
        this.withdrawal = withdrawal;
    }

    public double getBalance() {
        return balance;
    }

    public double getWithdrawal() {
        return withdrawal;
    }

    //the importance of overriding getMessage to provide a personalized message
    @Override
    public String getMessage() {
        return String.format("Insufficient funds in bank account %s: (balance $%.2f, withdrawal: $%.2f)." +
                                     " The account is short $%.2f",
                this.getAccountNumber(), this.balance, this.withdrawal, this.withdrawal - this.balance);
    }
}

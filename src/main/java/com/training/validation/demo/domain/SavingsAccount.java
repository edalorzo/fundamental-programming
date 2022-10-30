package com.training.validation.demo.domain;

import com.training.validation.demo.api.BankAccount;
import com.training.validation.demo.api.InsufficientFundsException;
import com.training.validation.demo.common.AccountNumber;
import com.training.validation.demo.transports.AccountBalance;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class SavingsAccount implements BankAccount {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //favor immutability when possible
    private final AccountNumber accountNumber;
    private double balance;

    /**
     * Creates a new savings account with the given account number.
     * The account number must have the following format X-XXX-XXX where X is an digit between 0-9.
     * The initial balance of the account will initially be 0.0.
     *
     * @param accountNumber - the account number.
     * @throws NullPointerException if the {@code accountNumber} is null.
     */
    public SavingsAccount(AccountNumber accountNumber, double balance) {
        Objects.requireNonNull(accountNumber, "The account number must not be null");
        if(balance < 0) {
            throw new IllegalArgumentException("The balance must be > 0: " + balance);
        }
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    @Override
    public AccountBalance withdrawMoney(double amount) {
        if(amount <= 0)
            throw new IllegalArgumentException("The amount must be >= 0: " + amount);

        if(balance < amount) {
            throw new InsufficientFundsException(accountNumber, balance, amount);
        }
        balance -= amount;

        logger.info("Withdrew ${} from account {} for a final balance of ${}", amount, accountNumber, balance);

        return new AccountBalance(accountNumber, balance);
    }

    @Override
    public AccountBalance saveMoney(double amount) {
        if(amount <= 0)
            throw new IllegalArgumentException("The amount must be >= 0: " + amount);

        balance += amount;
        logger.info("Saved ${} from account {} for a final balance of ${}", amount, accountNumber, balance);

        return new AccountBalance(accountNumber,balance);
    }

    @Override
    public AccountBalance getCurrentBalance() {
        return new AccountBalance(accountNumber, balance);
    }


    @Override
    public AccountNumber getAccountNumber() {
        return this.accountNumber;
    }


    //importance of toString for debugging purposes
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                       .append("accountNumber", accountNumber)
                       .append("balance", balance)
                       .toString();
    }
}

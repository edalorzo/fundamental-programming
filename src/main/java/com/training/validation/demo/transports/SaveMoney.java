package com.training.validation.demo.transports;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.training.validation.demo.common.AccountNumber;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Command/Request object used to represent the saving of certain amount of money
 * in a given bank account.
 */
public class SaveMoney {

    //strive to design immutable DTOs
    private final AccountNumber accountNumber;
    private final double amount;

    @JsonCreator
    public SaveMoney(@JsonProperty("accountNumber") AccountNumber accountNumber,
                     @JsonProperty("amount") double amount) {

        Objects.requireNonNull(accountNumber, "The account number must not be null");
        if(amount <= 0) {
            throw new IllegalArgumentException("The amount must be > 0: " + amount);
        }
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    @NotNull
    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    @Min(1)
    public double getAmount() {
        return amount;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                       .append("accountNumber", accountNumber)
                       .append("amount", amount)
                       .toString();
    }
}

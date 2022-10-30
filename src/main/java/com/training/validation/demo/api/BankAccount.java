package com.training.validation.demo.api;

import com.training.validation.demo.common.AccountNumber;
import com.training.validation.demo.transports.AccountBalance;

public interface BankAccount {

    /**
     * Withdrawing money from a savings account reduces its balance by the
     * provided withdrawal amount.
     * For the withdrawal operation to succeed, the savings account is expected to have enough balance
     * to satisfy the withdrawal.
     * At any point in time the final balance of the saving accounts may
     * never be smaller than 0.
     *
     * @param amount - the amount you want to withdraw from your account.
     * @return the balance in the account after the withdrawal.
     * @throws IllegalArgumentException if {@code amount} <= 0.
     * @throws InsufficientFundsException if the current {@code balance} is smaller than {@code amount}
     */

    AccountBalance withdrawMoney(double amount) throws InsufficientFundsException;

    /**
     * Saving money into the savings account increases its balance by the saved amount.
     * In order that the saving succeed, the final account balance must represent a positive amount of money
     * At any point in time the final balance of the saving accounts may never be smaller than 0.
     *
     * @param amount - the amount to save into the account.
     * @return the balance of the account after savings.
     * @throws IllegalArgumentException if {@code amount} <= 0.
     */

    AccountBalance saveMoney(double amount);

    /**
     * Gets this bank account number.
     */
    AccountNumber getAccountNumber();

    /**
     * Provides the account current balance.
     */
    AccountBalance getCurrentBalance();
}

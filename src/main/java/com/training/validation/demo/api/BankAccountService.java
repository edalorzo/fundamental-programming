package com.training.validation.demo.api;

import com.training.validation.demo.common.AccountNumber;
import com.training.validation.demo.transports.AccountBalance;
import com.training.validation.demo.transports.SaveMoney;
import com.training.validation.demo.transports.WithdrawMoney;

public interface BankAccountService {

    /**
     * Withdraws the provided amount of money from the given bank account.
     *
     * @param withdrawal - the withdrawal request details.
     * @return the balance of the account after withdrawal.
     * @throws InsufficientFundsException if the account does not have enough balance for withdrawal.
     * @throws BankAccountNotFoundException if the provided account does not exist.
     */
    AccountBalance withdrawMoney(WithdrawMoney withdrawal);

    /**
     * Saves the provided amount of money into the given bank account.
     *
     * @param savings - the savings request details.
     * @return the balance of the account after deposit.
     * @throws BankAccountNotFoundException if the provided account does not exist.
     */
    AccountBalance saveMoney(SaveMoney savings);

    /**
     * Retrieves the account current balance.
     *
     * @param account the account number.
     * @return the account's current balance.
     * @throws BankAccountNotFoundException if the provided account does not exist.
     */
    AccountBalance getCurrentBalance(AccountNumber account);

}

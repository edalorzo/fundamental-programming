package com.training.validation.demo.api;

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
    double withdrawMoney(WithdrawMoney withdrawal);

    /**
     * Saves the provided amount of money into the given bank account.
     *
     * @param savings - the savings request details.
     * @return the balance of the account after withdrawal.
     * @throws BankAccountNotFoundException if the provided account does not exist.
     */
    double saveMoney(SaveMoney savings);

}

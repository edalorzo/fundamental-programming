package com.training.validation.demo.api;

import com.training.validation.demo.domain.SavingsAccount;
import com.training.validation.demo.common.AccountNumber;

import java.util.Optional;

public interface BankAccountRepository {

    /**
     * Using the provided account number it finds the corresponding bank account
     * domain object.
     *
     * @param accountNumber - the account number to seek by.
     * @return the found {@code SavingsAccount} or an empty optional if not found.
     * @throws NullPointerException if the {@code accountNumber} is null
     * @throws IllegalArgumentException if the {@code accountNumber} is not a valid account number.
     */
    Optional<SavingsAccount> findAccountByNumber(AccountNumber accountNumber);

}

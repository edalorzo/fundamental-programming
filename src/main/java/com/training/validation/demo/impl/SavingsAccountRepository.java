package com.training.validation.demo.impl;

import com.training.validation.demo.api.BankAccountRepository;
import com.training.validation.demo.common.AccountNumber;
import com.training.validation.demo.domain.SavingsAccount;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SavingsAccountRepository implements BankAccountRepository {

    private final List<SavingsAccount> savingsAccounts = Arrays.asList(
            new SavingsAccount(new AccountNumber("1-234-567-890")),
            new SavingsAccount(new AccountNumber("9-876-543-210")),
            new SavingsAccount(new AccountNumber("1-236-547-890")),
            new SavingsAccount(new AccountNumber("9-874-563-210"))
    );

    private final Random random = new Random();

    @Override
    public Optional<SavingsAccount> findAccountByNumber(AccountNumber accountNumber) {
        Objects.requireNonNull(accountNumber, "The account number must not be null");

        //just added some random failure here to demonstrate a retry mechanism
        //for transient exceptions in the service layer.
        if(random.nextBoolean()) {
            //the possibility of retrying transient exceptions
            throw new QueryTimeoutException("Database query timed out!");
            //a persistent exception should not be retried
            //throw new DataIntegrityViolationException("Database constraint failed!");
        }
        return findSavingsAccount(accountNumber);
    }

    private Optional<SavingsAccount> findSavingsAccount(AccountNumber accountNumber) {
        for(SavingsAccount savingsAccount : savingsAccounts) {
            if(accountNumber.equals(savingsAccount.getAccountNumber())) {
                return Optional.of(savingsAccount);
            }
        }
        return Optional.empty();
    }

}

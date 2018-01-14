package com.training.validation.demo.impl;

import com.training.validation.demo.api.*;
import com.training.validation.demo.transports.SaveMoney;
import com.training.validation.demo.transports.WithdrawMoney;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static java.util.Collections.singletonMap;

@Service
public class SavingsAccountService implements BankAccountService {

    private final BankAccountRepository accountRepository;

    @Autowired
    public SavingsAccountService(BankAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public double withdrawMoney(WithdrawMoney withdrawal) throws InsufficientFundsException {
        //notice I still check nullability of the parameter, but not its contents
        //a programmer may still have made the error of passing a null value

        Objects.requireNonNull(withdrawal, "The withdrawal request must not be null");

        //we may also configure this as a bean
        RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy policy = new SimpleRetryPolicy(3, singletonMap(TransientDataAccessException.class, true), true);
        retryTemplate.setRetryPolicy(policy);

        //dealing with transient exceptions locally
        return retryTemplate.execute(context -> {
            try {
                //remember that checked exceptions don't play well with lambdas
                //e.g. if InsufficientFundsException was a checked exception
                return accountRepository.findAccountByNumber(withdrawal.getAccountNumber())
                                        .map(account -> account.withdrawMoney(withdrawal.getAmount()))
                                        .orElseThrow(() -> new BankAccountNotFoundException(withdrawal.getAccountNumber()));
            }
            catch (DataAccessException cause) {
                //avoid leaky abstractions and wrap lower level abstraction exceptions into your own exception
                //make sure you keep the exception chain intact such that you don't lose sight of the root cause
                throw new SavingsAccountException(withdrawal.getAccountNumber(), cause);
                //Would it make to have a more specific exception e.g. WithdrawMoneyException(withdrawal, ex)?
            }
        });

    }

    @Override
    public double saveMoney(SaveMoney savings) {

        Objects.requireNonNull(savings, "The savings request must not be null");

        try {
            return accountRepository.findAccountByNumber(savings.getAccountNumber())
                                    .map(account -> account.saveMoney(savings.getAmount()))
                                    .orElseThrow(() -> new BankAccountNotFoundException(savings.getAccountNumber()));
        }
        catch (DataAccessException cause) {
            //avoid leaky abstractions and wrap lower level abstraction exceptions into your own exception
            //make sure you keep the exception chain intact such that you don't lose sight of the root cause
            throw new SavingsAccountException(savings.getAccountNumber(), cause);
        }
    }
}

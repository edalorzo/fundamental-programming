package com.training.validation.demo.impl;

import com.training.validation.demo.api.BankAccountNotFoundException;
import com.training.validation.demo.api.BankAccountRepository;
import com.training.validation.demo.api.InsufficientFundsException;
import com.training.validation.demo.api.SavingsAccountException;
import com.training.validation.demo.common.AccountNumber;
import com.training.validation.demo.domain.SavingsAccount;
import com.training.validation.demo.transports.SaveMoney;
import com.training.validation.demo.transports.WithdrawMoney;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.QueryTimeoutException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SavingsAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private SavingsAccountService savingsAccountService;

    private final AccountNumber accountNumber = new AccountNumber("1-234-567-890");

    @Test
    public void testSuccessfulMoneySaving() {
        when(bankAccountRepository.findAccountByNumber(accountNumber))
                .thenReturn(Optional.of(new SavingsAccount(accountNumber)));

        SaveMoney savings = new SaveMoney(new AccountNumber("1-234-567-890"), 100);
        double balance = savingsAccountService.saveMoney(savings);

        verify(bankAccountRepository, times(1)).findAccountByNumber(eq(accountNumber));
        verifyNoMoreInteractions(bankAccountRepository);

        assertThat(balance).isEqualTo(100.0);
    }

    @Test(expected = BankAccountNotFoundException.class)
    public void testSavingsFailureDueToUnknownBankAccount() {

        when(bankAccountRepository.findAccountByNumber(accountNumber))
                .thenReturn(Optional.empty());

        SaveMoney savings = new SaveMoney(new AccountNumber("1-234-567-890"), 100);
        try {
            savingsAccountService.saveMoney(savings);
        }
        catch (Exception e) {
            verify(bankAccountRepository, times(1)).findAccountByNumber(eq(accountNumber));
            verifyNoMoreInteractions(bankAccountRepository);
            throw e;
        }
        fail("The saveMoney method should have failed!");
    }

    @Test
    public void testSuccessfulMoneyWithdrawal() {

        when(bankAccountRepository.findAccountByNumber(accountNumber))
                .thenReturn(Optional.of(new SavingsAccount(accountNumber)));

        SaveMoney savings = new SaveMoney(new AccountNumber("1-234-567-890"), 100);
        savingsAccountService.saveMoney(savings);

        WithdrawMoney withdraw = new WithdrawMoney(new AccountNumber("1-234-567-890"), 1);
        double balance = savingsAccountService.withdrawMoney(withdraw);

        verify(bankAccountRepository, times(2)).findAccountByNumber(eq(accountNumber));
        verifyNoMoreInteractions(bankAccountRepository);

        assertThat(balance).isEqualTo(99.0);
    }

    @Test(expected = InsufficientFundsException.class)
    public void testWithdrawalFailureDueToInsufficientFunds() {

        when(bankAccountRepository.findAccountByNumber(accountNumber))
                .thenReturn(Optional.of(new SavingsAccount(accountNumber)));

        WithdrawMoney withdraw = new WithdrawMoney(new AccountNumber("1-234-567-890"), 100);
        try {
            savingsAccountService.withdrawMoney(withdraw);
        }
        catch (Exception e) {
            verify(bankAccountRepository, times(1)).findAccountByNumber(eq(accountNumber));
            verifyNoMoreInteractions(bankAccountRepository);
            throw e;
        }
        fail("The withDrawMoney method should have failed due to insufficient funds!");
    }

    @Test(expected = BankAccountNotFoundException.class)
    public void testWithdrawalFailureDueToUnknownBankAccount() {

        when(bankAccountRepository.findAccountByNumber(accountNumber))
                .thenReturn(Optional.empty());

        WithdrawMoney withdraw = new WithdrawMoney(new AccountNumber("1-234-567-890"), 100);
        try {
            savingsAccountService.withdrawMoney(withdraw);
        }
        catch (Exception e) {
            verify(bankAccountRepository, times(1)).findAccountByNumber(eq(accountNumber));
            verifyNoMoreInteractions(bankAccountRepository);
            throw e;
        }
        fail("The withDrawMoney method should have failed due to missing account!");

    }

    @Test(expected = SavingsAccountException.class)
    public void testWithdrawalFailureDueToOtherExceptions() {
        when(bankAccountRepository.findAccountByNumber(accountNumber))
                .thenThrow(new QueryTimeoutException("Query timed out!"));

        WithdrawMoney withdraw = new WithdrawMoney(new AccountNumber("1-234-567-890"), 100);
        try {
            savingsAccountService.withdrawMoney(withdraw);
        }
        catch (Exception e) {
            verify(bankAccountRepository, times(1)).findAccountByNumber(eq(accountNumber));
            verifyNoMoreInteractions(bankAccountRepository);
            throw e;
        }
        fail("The withDrawMoney method should have failed due to query time out!");
    }

    @Test(expected = NullPointerException.class)
    public void testWithdrawalWithNullParameter() {
        WithdrawMoney withdraw = new WithdrawMoney(new AccountNumber("1-234-567-890"), 100);
        savingsAccountService.withdrawMoney(withdraw);
        fail("The withDrawMoney method should have failed due to null parameter!");
    }


    @Test(expected = NullPointerException.class)
    public void testSavingsWithNullParameter() {
        SaveMoney savings = new SaveMoney(new AccountNumber("1-234-567-890"), 100);
        savingsAccountService.saveMoney(savings);
        fail("The saveMoney method should have failed due to null parameter!");
    }

}

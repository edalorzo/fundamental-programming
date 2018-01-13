package com.training.validation.demo.domain;

import com.training.validation.demo.api.BankAccount;
import com.training.validation.demo.api.InsufficientFundsException;
import com.training.validation.demo.common.AccountNumber;
import org.junit.Assert;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class SavingsAccountTest {

    private final AccountNumber accountNumber = new AccountNumber("1-234-567-890");
    private final BankAccount bankAccount = new SavingsAccount(accountNumber);

    @Test
    public void saveMoney() {

        double balance = bankAccount.saveMoney(100);
        assertThat(balance).isEqualTo(100);
        balance = bankAccount.saveMoney(75);
        assertThat(balance).isEqualTo(175);
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveMoneyWithNegativeAmount() {
        bankAccount.saveMoney(-100);
        Assert.fail("Savings of negative numbers should fail!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveMoneyWithZeroAmount() {
        bankAccount.saveMoney(0.0);
        Assert.fail("Savings of $0 should fail!");
    }

    @Test
    public void withdrawMoney() {
        double balance = bankAccount.saveMoney(100);
        assertThat(balance).isEqualTo(100);
        balance = bankAccount.withdrawMoney(50);
        assertThat(balance).isEqualTo(50);
        balance = bankAccount.withdrawMoney(25);
        assertThat(balance).isEqualTo(25);
        balance = bankAccount.withdrawMoney(25);
        assertThat(balance).isEqualTo(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void withdrawMoneyWithNegativeAmount() {
        bankAccount.withdrawMoney(-100);
        Assert.fail("Withdrawal of negative numbers should fail!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void withdrawMoneyWithZeroAmount() {
        bankAccount.withdrawMoney(0.0);
        Assert.fail("Withdrawal of negative numbers should fail!");
    }

    @Test(expected = InsufficientFundsException.class)
    public void withdrawMoneyWithInsufficientFunds() {
        bankAccount.withdrawMoney(50);
        Assert.fail("Withdrawal should fail when there aren't sufficient funds!");
    }
}
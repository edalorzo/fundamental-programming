package com.training.validation.demo.transports;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.validation.demo.common.AccountNumber;
import org.junit.Assert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class WithdrawMoneyTest {

    private final AccountNumber accountNumber = new AccountNumber("1-234-567-890");

    @Test
    public void testValidConstruction() {
        WithdrawMoney withdrawal = new WithdrawMoney(accountNumber, 10.0);
        assertThat(withdrawal.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(withdrawal.getAmount()).isEqualTo(10.0);
    }


    @Test(expected = NullPointerException.class)
    public void testInvalidAccountConstruction() {
        new WithdrawMoney(null, 10.0);
        fail("The WithdrawMoney object must not be created with an invalid account number!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidAmountConstruction() {
        new WithdrawMoney(accountNumber, -10.0);
        fail("The WithdrawMoney object must not be created with an invalid amount!");
    }

    @Test
    public void testEqualityContract() {

        WithdrawMoney alpha = new WithdrawMoney(accountNumber, 10.0);
        WithdrawMoney beta = new WithdrawMoney(accountNumber, 10.0);
        WithdrawMoney gamma = new WithdrawMoney(accountNumber, 10.0);
        WithdrawMoney delta = new WithdrawMoney(accountNumber, 20.0);

        //reflexive quality
        assertTrue(alpha.equals(alpha));

        //reflexive quality
        assertTrue(alpha.equals(beta));
        assertTrue(beta.equals(alpha));

        //transitive quality
        assertTrue(beta.equals(gamma));
        assertTrue(alpha.equals(gamma));

        //inequality
        assertFalse(alpha.equals(delta));

        //hashcode consistency
        assertTrue(alpha.hashCode() == beta.hashCode());
    }

    @Test
    public void testSerialization() {

        ObjectMapper mapper = new ObjectMapper();
        try {
            WithdrawMoney source = new WithdrawMoney(accountNumber, 10.0);
            String json = mapper.writeValueAsString(source);
            WithdrawMoney copy = mapper.readValue(json, WithdrawMoney.class);
            assertThat(source).isEqualTo(copy);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
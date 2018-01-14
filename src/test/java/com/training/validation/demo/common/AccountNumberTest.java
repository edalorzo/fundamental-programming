package com.training.validation.demo.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.validation.demo.transports.WithdrawMoney;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;


public class AccountNumberTest {

    @Test
    public void testValidConstruction() {
        AccountNumber accountNumber = new AccountNumber("1-234-567-890");
        assertThat(accountNumber.getNumber()).isEqualTo("1-234-567-890");
        assertThat(accountNumber.toString()).isEqualTo("1-234-567-890");
    }


    @Test(expected = NullPointerException.class)
    public void testInvalidAccountConstruction() {
        new AccountNumber(null);
        fail("The AccountNumber object must not be created with an invalid account number!");
    }

    @Test
    public void testEqualityContract() {

        AccountNumber alpha = new AccountNumber("1-234-567-890");
        AccountNumber beta = new AccountNumber("1-234-567-890");
        AccountNumber gamma = new AccountNumber("1-234-567-890");
        AccountNumber delta = new AccountNumber("9-876-543-210");

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
            AccountNumber source = new AccountNumber("1-234-567-890");
            String json = mapper.writeValueAsString(source);
            AccountNumber copy = mapper.readValue(json, AccountNumber.class);
            assertThat(source).isEqualTo(copy);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
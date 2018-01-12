package com.training.validation.demo.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

/**
 * Account Number Value Object
 */
public class AccountNumber {

    //favor immutability when possible
    private final String number;

    @JsonCreator
    public AccountNumber(String number) {
        Objects.requireNonNull(number, "The account number must not be null");
        if(!number.matches("\\d-\\d{3}-\\d{3}-\\d{3}")) {
            throw new IllegalArgumentException("Invalid savings account number format: " + number);
        }
        this.number = number;
    }

    @JsonValue
    public String getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AccountNumber that = (AccountNumber) o;

        return number.equals(that.number);
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }

    @Override
    public String toString() {
        return this.number;
    }
}

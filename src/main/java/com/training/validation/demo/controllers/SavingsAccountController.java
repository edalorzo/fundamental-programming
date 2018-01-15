package com.training.validation.demo.controllers;

import com.training.validation.demo.api.BankAccountService;
import com.training.validation.demo.impl.SavingsAccountService;
import com.training.validation.demo.transports.AccountBalance;
import com.training.validation.demo.transports.SaveMoney;
import com.training.validation.demo.transports.WithdrawMoney;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class SavingsAccountController {

    private final BankAccountService accountService;

    @Autowired
    public SavingsAccountController(BankAccountService accountService) {
        this.accountService = accountService;
    }

    @PutMapping("withdraw")
    public ResponseEntity<AccountBalance> onMoneyWithdrawal(@RequestBody @Validated WithdrawMoney withdrawal, BindingResult errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }

        //any exception thrown here will be handled in the ExceptionHandlers class
        double balance = accountService.withdrawMoney(withdrawal);
        return ResponseEntity.ok(new AccountBalance(
                withdrawal.getAccountNumber(), balance));
    }

    @PutMapping("save")
    public ResponseEntity<AccountBalance> onMoneySaving(@RequestBody @Validated SaveMoney savings, BindingResult errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }

        //any exception thrown here will be handled in the ExceptionHandlers class
        double balance = accountService.saveMoney(savings);
        return ResponseEntity.ok(new AccountBalance(
                savings.getAccountNumber(), balance));
    }
}

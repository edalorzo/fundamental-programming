package com.training.validation.demo.controllers;

import com.training.validation.demo.api.BankAccountService;
import com.training.validation.demo.common.AccountNumber;
import com.training.validation.demo.transports.AccountBalance;
import com.training.validation.demo.transports.SaveMoney;
import com.training.validation.demo.transports.WithdrawMoney;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class SavingsAccountController {

    private final BankAccountService accountService;

    @Autowired
    public SavingsAccountController(BankAccountService accountService) {
        this.accountService = accountService;
    }

    @PutMapping("withdraw")
    public ResponseEntity<AccountBalance> onMoneyWithdrawal(@RequestBody WithdrawMoney withdrawal) {

        //any exception thrown here will be handled in the ExceptionHandlers class
        AccountBalance balance = accountService.withdrawMoney(withdrawal);
        return ResponseEntity.ok(balance);
    }

    @PutMapping("save")
    public ResponseEntity<AccountBalance> onMoneySaving(@RequestBody SaveMoney savings) {

        //any exception thrown here will be handled in the ExceptionHandlers class
        AccountBalance balance = accountService.saveMoney(savings);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountBalance> getBalance(@PathVariable("accountNumber") AccountNumber accountNumber) {
        AccountBalance balance = accountService.getCurrentBalance(accountNumber);
        return ResponseEntity.ok(balance);
    }
}

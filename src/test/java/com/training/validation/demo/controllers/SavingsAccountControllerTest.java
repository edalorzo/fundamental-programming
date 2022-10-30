package com.training.validation.demo.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.validation.demo.api.BankAccountService;
import com.training.validation.demo.common.AccountNumber;
import com.training.validation.demo.transports.AccountBalance;
import com.training.validation.demo.transports.SaveMoney;
import com.training.validation.demo.transports.WithdrawMoney;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SavingsAccountController.class)
public class SavingsAccountControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final AccountNumber accountNumber = new AccountNumber("1-234-567-890");

    @MockBean
    private BankAccountService bankAccountService;

    @Autowired
    private MockMvc mvc;


    @Test
    public void testSavingMoney() throws Exception {

        SaveMoney savings = new SaveMoney(accountNumber, 100.0);

        given(bankAccountService.saveMoney(savings))
                .willReturn(new AccountBalance(accountNumber, savings.getAmount()));

        RequestBuilder request = put("/accounts/save")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(getJsonString(savings));

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.accountNumber", equalTo("1-234-567-890")))
                .andExpect(jsonPath("$.balance", equalTo(100.0)))
                .andDo(print());

    }

    @Test
    public void testSavingsWithInvalidAmount() throws Exception {

        RequestBuilder request = put("/accounts/save")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{\"accountNumber\":\"1-234-567-890\", \"amount\": -100}");

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.messages[0]", equalTo("The amount must be > 0: -100.0")))
                .andDo(print());

    }

    @Test
    public void testSavingsWithInvalidAccountNumber() throws Exception {

        RequestBuilder request = put("/accounts/save")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{\"accountNumber\": null, \"amount\": 100}");

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.messages[0]", equalTo("The account number must not be null")))
                .andDo(print());

    }


    @Test
    public void testWithdrawingMoney() throws Exception {

        WithdrawMoney withdrawal = new WithdrawMoney(accountNumber, 100.0);

        given(bankAccountService.withdrawMoney(withdrawal))
                .willReturn(new AccountBalance(accountNumber, 10.0));

        RequestBuilder request = put("/accounts/withdraw")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(getJsonString(withdrawal));

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.accountNumber", equalTo("1-234-567-890")))
                .andExpect(jsonPath("$.balance", equalTo(10.0)))
                .andDo(print());

    }


    @Test
    public void testWithdrawalWithInvalidAmount() throws Exception {

        RequestBuilder request = put("/accounts/withdraw")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{\"accountNumber\":\"1-234-567-890\", \"amount\": -100}");

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.messages[0]", equalTo("The amount must be > 0: -100.0")))
                .andDo(print());

    }

    @Test
    public void testWithdrawalWithInvalidAccountNumber() throws Exception {

        RequestBuilder request = put("/accounts/withdraw")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{\"accountNumber\": null, \"amount\": 100}");

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.messages[0]", equalTo("The account number must not be null")))
                .andDo(print());

    }


    private String getJsonString(Object source) throws Exception {
        return mapper.writeValueAsString(source);
    }

}
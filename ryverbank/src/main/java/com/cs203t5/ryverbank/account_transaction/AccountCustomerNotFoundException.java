package com.cs203t5.ryverbank.account_transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountCustomerNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public AccountCustomerNotFoundException(Long id){
        super("Bad request for creating account: Could not find user " + id);
    }

}

package com.shukyurov.BankMicroservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ClientsNotFoundException extends RuntimeException {

    private String bankAccountNumber1;
    private String bankAccountNumber2;

    public ClientsNotFoundException(String bankAccountNumber1, String bankAccountNumber2) {
        super(String.format("Clients or client with account number : %s and(or) %s not found", bankAccountNumber1, bankAccountNumber2));
        this.bankAccountNumber1 = bankAccountNumber1;
        this.bankAccountNumber2 = bankAccountNumber2;
    }

}

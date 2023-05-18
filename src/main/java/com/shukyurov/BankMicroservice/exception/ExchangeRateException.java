package com.shukyurov.BankMicroservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ExchangeRateException extends RuntimeException {

    private static final String CLIENT_URL = "https://api.twelvedata.com/";

    public ExchangeRateException() {
        super(String.format("Could not get exchange rate from resource : '%s'", CLIENT_URL));
    }

}

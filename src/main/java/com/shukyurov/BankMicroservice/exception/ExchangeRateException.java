package com.shukyurov.BankMicroservice.exception;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ExchangeRateException extends RuntimeException {

    @Value("${spring.client.url}")
    private static String clientUrl;

    public ExchangeRateException() {
        super(String.format("Could not get exchange rate from resource : '%s'", clientUrl));
    }

}

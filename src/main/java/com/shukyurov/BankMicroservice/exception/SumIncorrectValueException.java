package com.shukyurov.BankMicroservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SumIncorrectValueException extends RuntimeException {

    private Double sum;

    public SumIncorrectValueException(Double sum) {
        super(String.format("Sum to increase or decrease limit is incorrect : '%s'", sum));
        this.sum = sum;
    }

}

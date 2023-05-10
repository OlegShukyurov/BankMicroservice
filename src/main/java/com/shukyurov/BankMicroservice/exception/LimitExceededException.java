package com.shukyurov.BankMicroservice.exception;

import lombok.Getter;

@Getter
public class LimitExceededException extends RuntimeException {

    private String remainingMonthLimit;

    public LimitExceededException(String remainingMonthLimit) {
        super(String.format("Your current remaining month limit has been exceeded  : '%s'", remainingMonthLimit));
        this.remainingMonthLimit = remainingMonthLimit;
    }

}

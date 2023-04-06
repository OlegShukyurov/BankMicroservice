package com.shukyurov.BankMicroservice.model;

import lombok.Getter;

@Getter
public enum ExchangeType {

    USD_RUB("USD/RUB"), USD_KZT("USD/KZT");

    private final String symbol;

    ExchangeType(String symbol) {
        this.symbol = symbol;
    }
}

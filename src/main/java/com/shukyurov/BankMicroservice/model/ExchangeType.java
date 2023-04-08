package com.shukyurov.BankMicroservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExchangeType {

    USD_RUB("USD/RUB"), USD_KZT("USD/KZT");

    private final String symbol;

}

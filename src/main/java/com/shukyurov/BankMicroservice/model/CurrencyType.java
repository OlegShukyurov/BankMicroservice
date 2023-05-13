package com.shukyurov.BankMicroservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CurrencyType {

    USD("USD"), KZT("KZT"), RUB("RUB");

    private final String currencyType;

}

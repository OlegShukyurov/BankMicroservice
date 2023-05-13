package com.shukyurov.BankMicroservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExpenseCategoryType {

    PRODUCT("product"), SERVICE("service");

    private final String expenseCategoryType;

}

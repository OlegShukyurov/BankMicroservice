package com.shukyurov.BankMicroservice.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionDTO {

    private Long account_from;

    private Long account_to;

    private String currency_shortname;

    private BigDecimal sum;

    private String expense_category;

    private LocalDateTime datetime;

}

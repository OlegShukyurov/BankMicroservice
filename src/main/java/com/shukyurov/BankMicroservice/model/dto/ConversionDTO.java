package com.shukyurov.BankMicroservice.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ConversionDTO {

    private String symbol;

    private BigDecimal rate;

    private LocalDateTime timestamp;
}

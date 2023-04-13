package com.shukyurov.BankMicroservice.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConversionDTO {

    private String symbol;

    private Double rate;

    private Long timestamp;

}

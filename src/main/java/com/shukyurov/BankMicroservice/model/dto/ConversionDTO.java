package com.shukyurov.BankMicroservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversionDTO {

    private String symbol;

    private Double rate;

    private Long timestamp;

}

package com.shukyurov.BankMicroservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LimitDTO {

    @NotNull(message = "Limit sum should not be null")
    @Min(value = 0, message = "Sum of limit should be greater than 0")
    private Double limit_sum;

    @NotNull(message = "Currency type should not be null")
    @Pattern(regexp = "(^USD$)|(^KZT$)|(^RUB$)",
            message = "Currency shortname should be in these variants: 1) 'RUB' 2) 'KZT' 3) 'USD'")
    private String limit_currency_shortname;

}

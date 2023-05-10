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
public class LimitRequestDTO {

    @NotNull(message = "Limit sum should not be null")
    @Min(value = 0, message = "Sum of limit should be greater than 0")
    private Double limit_sum;

    @NotNull(message = "Currency type should not be null")
    @Pattern(regexp = "(^USD$)",
            message = "Currency shortname should be : 'USD'")
    private String limit_currency_shortname;

    @NotNull(message = "Expense category should not be null")
    @Pattern(regexp = "(^product$)|(^service$)",
            message = "Expense category should be in these variants : 1) 'product' 2) 'service'")
    private String limit_expense_category;

}

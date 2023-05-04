package com.shukyurov.BankMicroservice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    @NotEmpty(message = "Account number should not be empty")
    @NotNull(message = "Account number should not be null")
    @Pattern(regexp = "^[0-9]{10}$",
            message = "Account number should be in this format: '0000000000' for example: '1234567890'")
    private String account_from;

    @NotEmpty(message = "Account number should not be empty")
    @NotNull(message = "Account number should not be null")
    @Pattern(regexp = "^[0-9]{10}$",
            message = "Account number should be in this format: '0000000000' for example: '1234567890'")
    private String account_to;

    @NotNull(message = "Currency type should not be null")
    @Pattern(regexp = "(^RUB$)|(^KZT$)",
            message = "Currency shortname should be in these variants: 1) 'RUB' 2) 'KZT'")
    private String currency_shortname;

    @NotNull(message = "Sum should not be null")
    @Min(value = 0, message = "Sum of transaction should be greater than 0")
    private Double sum;

    @NotNull(message = "Expense category should not be null")
    @Pattern(regexp = "(^product$)|(^service$)",
            message = "Expense category should be in these variants : 1) 'product' 2) 'service'")
    private String expense_category;

    @NotNull(message = "Datetime should not be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datetime;

}

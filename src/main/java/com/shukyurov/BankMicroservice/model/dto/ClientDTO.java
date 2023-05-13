package com.shukyurov.BankMicroservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {

    @NotEmpty(message = "Account number should not be empty")
    @NotNull(message = "Account number should not be null")
    @Pattern(regexp = "^[0-9]{10}$", message = "Account number should be in this format: '0000000000' for example: '1234567890'")
    private String bankAccountNumber;

}

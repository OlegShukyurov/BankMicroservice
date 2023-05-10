package com.shukyurov.BankMicroservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDTO extends TransactionRequestDTO {

    private LimitResponseDTO limit;

}

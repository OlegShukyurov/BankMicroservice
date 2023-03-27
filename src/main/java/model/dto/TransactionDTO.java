package model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionDTO {

    private int account_from;

    private int account_to;

    private String currency_shortname;

    private BigDecimal sum;

    private String expense_category;

    private LocalDateTime datetime;
}

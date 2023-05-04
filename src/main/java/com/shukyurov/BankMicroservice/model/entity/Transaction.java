package com.shukyurov.BankMicroservice.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import com.shukyurov.BankMicroservice.model.CurrencyType;
import com.shukyurov.BankMicroservice.model.ExpenseCategoryType;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Account number should not be empty")
    @NotNull(message = "Account number should not be null")
    @Pattern(regexp = "^[0-9]{10}$",
            message = "Account number should be in this format: '0000000000' for example: '1234567890'")
    private String accountFrom;

    @NotEmpty(message = "Account number should not be empty")
    @NotNull(message = "Account number should not be null")
    @Pattern(regexp = "^[0-9]{10}$",
            message = "Account number should be in this format: '0000000000' for example: '1234567890'")
    private String accountTo;

    @NotNull(message = "Sum should not be null")
    @Min(value = 0, message = "Sum of transaction should be greater than 0")
    private BigDecimal sum;

    @NotNull(message = "Currency type should not be null")
    @Enumerated(EnumType.STRING)
    private CurrencyType currencyShortname;

    @NotNull(message = "Expense category should not be null")
    @Enumerated(EnumType.STRING)
    private ExpenseCategoryType expenseCategory;

    @NotNull(message = "Datetime should not be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datetime;

    @JoinColumn(name = "client_id", referencedColumnName = "id")
    @ManyToOne
    private Client transactionClient;

    @JoinColumn(name = "limit_id", referencedColumnName = "id")
    @ManyToOne
    private Limit transactionLimit;

    private boolean limitExceeded;

}

package com.shukyurov.BankMicroservice.model.entity;

import lombok.Data;
import com.shukyurov.BankMicroservice.model.CurrencyType;
import com.shukyurov.BankMicroservice.model.ExpenseCategoryType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int accountFrom;

    private int accountTo;

    private LocalDateTime dateTime;

    private BigDecimal sum;

    @Enumerated(EnumType.STRING)
    private CurrencyType currencyShortname;

    @Enumerated(EnumType.STRING)
    private ExpenseCategoryType expenseCategory;

    @JoinColumn(name = "client_id", referencedColumnName = "id")
    @ManyToOne
    private Client transactionClient;

    @JoinColumn(name = "limit_id", referencedColumnName = "id")
    @ManyToOne
    private Limit transactionLimit;

}

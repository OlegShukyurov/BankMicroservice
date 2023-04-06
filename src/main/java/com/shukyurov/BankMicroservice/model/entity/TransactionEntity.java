package com.shukyurov.BankMicroservice.model.entity;

import lombok.Data;
import com.shukyurov.BankMicroservice.model.CurrencyType;
import com.shukyurov.BankMicroservice.model.ExpenseCategoryType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transaction")
public class TransactionEntity {

    @Id
    @Column(name = "transaction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "client_id", referencedColumnName = "client_id")
    @ManyToOne
    private ClientEntity transactionClient;

    @JoinColumn(name = "limit_id", referencedColumnName = "limit_id")
    @ManyToOne
    private LimitEntity transactionLimit;

    @Column(name = "account_from")
    private int accountFrom;

    @Column(name = "account_to")
    private int accountTo;

    @Column(name = "datetime")
    private LocalDateTime transactionDateTime;

    @Column(name = "currency_shortname")
    @Enumerated(EnumType.STRING)
    private CurrencyType transactionCurrencyShortname;

    @Column(name = "expense_category")
    @Enumerated(EnumType.STRING)
    private ExpenseCategoryType transactionExpenseCategory;

    @Column(name = "sum")
    private BigDecimal transactionSum;
}

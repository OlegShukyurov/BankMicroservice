package com.shukyurov.BankMicroservice.model.entity;

import lombok.Data;
import com.shukyurov.BankMicroservice.model.CurrencyType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "limit")
public class LimitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "limit_id")
    private Long id;

    @JoinColumn(name = "client_id", referencedColumnName = "client_id")
    @ManyToOne
    private ClientEntity limitClient;

    @OneToMany(mappedBy = "transactionLimit")
    private List<TransactionEntity> limitTransactions;

    @Column(name = "limit_exceeded")
    private boolean limitExceeded;

    @Column(name = "limit_sum")
    private BigDecimal limitSum;

    @Column(name = "remaining_month_limit")
    private BigDecimal remainingMonthLimit;

    @Column(name = "limit_date_time")
    private LocalDateTime limitDateTime;

    @Column(name = "limit_currency_shortname")
    @Enumerated(EnumType.STRING)
    private CurrencyType limitCurrencyShortname;
}

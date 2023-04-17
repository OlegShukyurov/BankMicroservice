package com.shukyurov.BankMicroservice.model.entity;

import lombok.Data;
import com.shukyurov.BankMicroservice.model.CurrencyType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table
public class Limit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean limitExceeded;

    private BigDecimal limitSum;

    private BigDecimal remainingMonthLimit;

    private LocalDateTime limitDateTime;

    @Enumerated(EnumType.STRING)
    private CurrencyType limitCurrencyShortname;

    @JoinColumn(name = "client_id", referencedColumnName = "id")
    @ManyToOne
    private Client limitClient;

    @OneToMany(mappedBy = "transactionLimit")
    private List<Transaction> limitTransactions;

}

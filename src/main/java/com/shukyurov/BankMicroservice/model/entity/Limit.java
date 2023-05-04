package com.shukyurov.BankMicroservice.model.entity;

import lombok.*;
import com.shukyurov.BankMicroservice.model.CurrencyType;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "limits")
public class Limit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Sum should not be null")
    @Min(value = 0, message = "Sum of limit should be equals or greater than 0")
    private BigDecimal limitSum;

    @NotNull(message = "Remaining month limit should not be null")
    private BigDecimal remainingMonthLimit;

    private LocalDateTime limitDateTime;

    @NotNull(message = "Currency type should not be null")
    @Enumerated(EnumType.STRING)
    private CurrencyType limitCurrencyShortname;

    @JoinColumn(name = "client_id", referencedColumnName = "id")
    @ManyToOne
    private Client limitClient;

    @OneToMany(mappedBy = "transactionLimit")
    private List<Transaction> limitTransactions;

}

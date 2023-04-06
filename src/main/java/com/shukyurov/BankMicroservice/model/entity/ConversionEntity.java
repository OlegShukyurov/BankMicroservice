package com.shukyurov.BankMicroservice.model.entity;

import lombok.Data;
import com.shukyurov.BankMicroservice.model.ExchangeType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED;

@Data
@Table(name = "conversion")
@PrimaryKeyClass
public class ConversionEntity {

    @PrimaryKeyColumn(name = "id", type = PARTITIONED)
    private Long id;

    @Column(name = "symbol")
    @Enumerated(EnumType.STRING)
    private ExchangeType conversionSymbol;

    @Column(name = "rate")
    private BigDecimal conversionRate;

    @Column(name = "rate_on_previously_close")
    private BigDecimal rateOnPreviousClose;

    @Column(name = "made_at")
    private LocalDateTime madeAt;
}

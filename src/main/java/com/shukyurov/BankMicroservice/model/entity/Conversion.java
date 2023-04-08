package com.shukyurov.BankMicroservice.model.entity;

import lombok.Data;
import com.shukyurov.BankMicroservice.model.ExchangeType;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table(name = "conversion")
@PrimaryKeyClass
public class Conversion {

    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    private Long id;

    private BigDecimal rate;

    private BigDecimal rateOnPreviousClose;

    private LocalDateTime madeAt;

    @Enumerated(EnumType.STRING)
    private ExchangeType symbol;

}

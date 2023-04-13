package com.shukyurov.BankMicroservice.model.entity;

import lombok.Data;
import com.shukyurov.BankMicroservice.model.ExchangeType;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.Table;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table(value = "conversion")
public class Conversion {

    private UUID id;

    private BigDecimal rate;

    @Column(value = "rate_on_previous_close")
    private BigDecimal rateOnPreviousClose;


    @PrimaryKeyColumn(name = "made_at", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private LocalDateTime madeAt;

    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    @Enumerated(EnumType.STRING)
    private ExchangeType symbol;

}

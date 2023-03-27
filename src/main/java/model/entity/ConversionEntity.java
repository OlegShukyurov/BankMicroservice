package model.entity;

import lombok.Data;
import model.ExchangeType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "conversion")
public class ConversionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversion_id")
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

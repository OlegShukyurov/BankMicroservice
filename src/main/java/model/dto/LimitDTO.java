package model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class LimitDTO {

    private BigDecimal limit_sum;

    private LocalDateTime limit_datetime;

    private String limit_currency_shortname;

}

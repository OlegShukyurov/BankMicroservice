package com.shukyurov.BankMicroservice.converter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimestampConverter {

    public static LocalDateTime convertTimestampToLocalDateTime(Long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    }

    public static LocalDate convertTimestampToLocalDate(long timestamp) {
        return convertTimestampToLocalDateTime(timestamp).toLocalDate();
    }

    public static Long convertLocalDateTimeToTimestamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

}

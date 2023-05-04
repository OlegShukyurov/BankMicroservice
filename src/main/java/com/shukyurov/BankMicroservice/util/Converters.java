package com.shukyurov.BankMicroservice.util;

import com.shukyurov.BankMicroservice.model.CurrencyType;
import com.shukyurov.BankMicroservice.model.ExchangeType;
import com.shukyurov.BankMicroservice.model.ExpenseCategoryType;
import org.modelmapper.Converter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Converters {

    public static Converter<Double, BigDecimal> doubleToDecimalConverter() {
        return mappingContext -> {
            Double source = mappingContext.getSource();
            return BigDecimal.valueOf(source).setScale(2, RoundingMode.HALF_UP);
        };
    }

    public static Converter<LocalDateTime, Long> localDateTimeLongConverter() {
        return mappingContext -> {
            LocalDateTime source = mappingContext.getSource();
            return source.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
        };
    }

    public static Converter<Long, LocalDateTime> longLocalDateTimeConverter() {
        return mappingContext -> {
            Long source = mappingContext.getSource();
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(source), ZoneId.systemDefault());
        };
    }

    public static Converter<String, ExchangeType> stringExchangeTypeConverter() {
        return mappingContext -> {
            String source = mappingContext.getSource();
            return ExchangeType.valueOf(source.replace('/', '_'));
        };
    }

    public static Converter<String, CurrencyType> stringCurrencyTypeConverter() {
        return mappingContext -> {
            String source = mappingContext.getSource();
            return CurrencyType.valueOf(source);
        };
    }

    public static Converter<String, ExpenseCategoryType> stringExpenseCategoryTypeConverter() {
        return mappingContext -> {
            String source = mappingContext.getSource();
            return ExpenseCategoryType.valueOf(source.toUpperCase());
        };
    }

    public static Converter<ExpenseCategoryType, String> expenseCategoryTypeStringConverter() {
        return mappingContext -> {
            ExpenseCategoryType source = mappingContext.getSource();
            return source.getExpenseCategoryType();
        };
    }

}

package com.shukyurov.BankMicroservice.mapper;

import com.shukyurov.BankMicroservice.converter.TimestampConverter;
import com.shukyurov.BankMicroservice.model.ExchangeType;
import com.shukyurov.BankMicroservice.model.dto.ConversionDTO;
import com.shukyurov.BankMicroservice.model.entity.Conversion;
import org.modelmapper.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ConversionMapper extends AbstractMapper<Conversion, ConversionDTO> {

    public ConversionMapper() {
        super(new ModelMapper(), Conversion.class, ConversionDTO.class);
    }

    @Override
    public ConversionDTO toDto(Conversion entity) {
        return getTypeMap().map(entity);
    }

    @Override
    public Conversion toEntity(ConversionDTO dto) {
        return getReverseTypeMap().map(dto);
    }

    @Override
    protected Class<Conversion> getEntityClass() {
        return Conversion.class;
    }

    @Override
    protected Class<ConversionDTO> getDtoClass() {
        return ConversionDTO.class;
    }

    @Override
    protected void configureTypeMap(TypeMap<Conversion, ConversionDTO> typeMap) {
        Converter<LocalDateTime, Long> localDateTimeToLong = c -> TimestampConverter.convertLocalDateTimeToTimestamp(c.getSource());

        typeMap.addMappings(mapper -> mapper.using(localDateTimeToLong).map(Conversion::getMadeAt, ConversionDTO::setTimestamp));
    }

    @Override
    protected void configureReverseTypeMap(TypeMap<ConversionDTO, Conversion> reverseTypeMap) {
        Converter<String, ExchangeType> stringToExchangeType = c -> ExchangeType.valueOf(c.getSource().replace('/', '_'));
        Converter<Long, LocalDateTime> longToLocalDateTime = c -> TimestampConverter.convertTimestampToLocalDateTime(c.getSource());

        reverseTypeMap.addMappings(mapper -> {
            mapper.using(stringToExchangeType).map(ConversionDTO::getSymbol, Conversion::setSymbol);
            mapper.using(longToLocalDateTime).map(ConversionDTO::getTimestamp, Conversion::setMadeAt);
        });
    }

}

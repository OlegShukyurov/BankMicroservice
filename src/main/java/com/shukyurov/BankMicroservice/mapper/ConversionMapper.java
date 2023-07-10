package com.shukyurov.BankMicroservice.mapper;

import com.shukyurov.BankMicroservice.util.Converters;
import com.shukyurov.BankMicroservice.model.dto.ConversionDTO;
import com.shukyurov.BankMicroservice.model.entity.Conversion;
import org.modelmapper.*;
import org.springframework.stereotype.Component;

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
        typeMap.addMappings(mapper -> mapper.using(Converters.localDateTimeLongConverter()).map(Conversion::getMadeAt, ConversionDTO::setTimestamp));
    }

    @Override
    protected void configureReverseTypeMap(TypeMap<ConversionDTO, Conversion> reverseTypeMap) {
        reverseTypeMap.addMappings(mapper -> {
            mapper.using(Converters.stringExchangeTypeConverter()).map(ConversionDTO::getSymbol, Conversion::setSymbol);
            mapper.using(Converters.longLocalDateTimeConverter()).map(ConversionDTO::getTimestamp, Conversion::setMadeAt);
            mapper.using(Converters.doubleToDecimalConverter()).map(ConversionDTO::getRate, Conversion::setRate);
        });
    }

}

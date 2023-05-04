package com.shukyurov.BankMicroservice.mapper;

import com.shukyurov.BankMicroservice.model.dto.LimitDTO;
import com.shukyurov.BankMicroservice.model.entity.Limit;
import com.shukyurov.BankMicroservice.util.Converters;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

@Component
public class LimitMapper extends AbstractMapper <Limit, LimitDTO> {

    public LimitMapper() {
        super(new ModelMapper(), Limit.class, LimitDTO.class);
    }

    @Override
    public LimitDTO toDto(Limit entity) {
        return getTypeMap().map(entity);
    }

    @Override
    public Limit toEntity(LimitDTO dto) {
        return getReverseTypeMap().map(dto);
    }

    @Override
    protected Class<Limit> getEntityClass() {
        return Limit.class;
    }

    @Override
    protected Class<LimitDTO> getDtoClass() {
        return LimitDTO.class;
    }

    @Override
    protected void configureTypeMap(TypeMap<Limit, LimitDTO> typeMap) {
        typeMap.addMappings(mapper -> {
            mapper.map(Limit::getLimitSum, LimitDTO::setLimit_sum);
            mapper.map(Limit::getLimitCurrencyShortname, LimitDTO::setLimit_currency_shortname);
        });
    }

    @Override
    protected void configureReverseTypeMap(TypeMap<LimitDTO, Limit> reverseTypeMap) {
        reverseTypeMap.addMappings(mapper -> {
            mapper.using(Converters.doubleToDecimalConverter()).map(LimitDTO::getLimit_sum, Limit::setLimitSum);
            mapper.using(Converters.stringCurrencyTypeConverter()).map(LimitDTO::getLimit_currency_shortname, Limit::setLimitCurrencyShortname);
        });
    }

}

package com.shukyurov.BankMicroservice.mapper;

import com.shukyurov.BankMicroservice.model.dto.LimitResponseDTO;
import com.shukyurov.BankMicroservice.model.entity.Limit;
import com.shukyurov.BankMicroservice.util.Converters;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

@Component
public class LimitResponseMapper extends AbstractMapper<Limit, LimitResponseDTO> {

    public LimitResponseMapper() {
        super(new ModelMapper(), Limit.class, LimitResponseDTO.class);
    }

    @Override
    public LimitResponseDTO toDto(Limit entity) {
        return getTypeMap().map(entity);
    }

    @Override
    public Limit toEntity(LimitResponseDTO dto) {
        return getReverseTypeMap().map(dto);
    }

    @Override
    protected Class<Limit> getEntityClass() {
        return Limit.class;
    }

    @Override
    protected Class<LimitResponseDTO> getDtoClass() {
        return LimitResponseDTO.class;
    }

    @Override
    protected void configureTypeMap(TypeMap<Limit, LimitResponseDTO> typeMap) {
        typeMap.addMappings(mapper -> {
            mapper.map(Limit::getLimitSum, LimitResponseDTO::setLimit_sum);
            mapper.map(Limit::getLimitCurrencyShortname, LimitResponseDTO::setLimit_currency_shortname);
            mapper.map(Limit::getLimitDateTime, LimitResponseDTO::setLimit_date_time);
            mapper.using(Converters.expenseCategoryTypeStringConverter()).map(Limit::getLimitExpenseCategory, LimitResponseDTO::setLimit_expense_category);
        });
    }

    @Override
    protected void configureReverseTypeMap(TypeMap<LimitResponseDTO, Limit> reverseTypeMap) {
        reverseTypeMap.addMappings(mapper -> {
            mapper.using(Converters.doubleToDecimalConverter()).map(LimitResponseDTO::getLimit_sum, Limit::setLimitSum);
            mapper.using(Converters.stringCurrencyTypeConverter()).map(LimitResponseDTO::getLimit_currency_shortname, Limit::setLimitCurrencyShortname);
            mapper.using(Converters.stringExpenseCategoryTypeConverter()).map(LimitResponseDTO::getLimit_expense_category, Limit::setLimitExpenseCategory);
            mapper.map(LimitResponseDTO::getLimit_date_time, Limit::setLimitDateTime);
        });
    }

}

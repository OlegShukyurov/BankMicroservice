package com.shukyurov.BankMicroservice.mapper;

import com.shukyurov.BankMicroservice.model.dto.LimitRequestDTO;
import com.shukyurov.BankMicroservice.model.entity.Limit;
import com.shukyurov.BankMicroservice.util.Converters;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

@Component
public class LimitRequestMapper extends AbstractMapper <Limit, LimitRequestDTO> {

    public LimitRequestMapper() {
        super(new ModelMapper(), Limit.class, LimitRequestDTO.class);
    }

    @Override
    public LimitRequestDTO toDto(Limit entity) {
        return getTypeMap().map(entity);
    }

    @Override
    public Limit toEntity(LimitRequestDTO dto) {
        return getReverseTypeMap().map(dto);
    }

    @Override
    protected Class<Limit> getEntityClass() {
        return Limit.class;
    }

    @Override
    protected Class<LimitRequestDTO> getDtoClass() {
        return LimitRequestDTO.class;
    }

    @Override
    protected void configureTypeMap(TypeMap<Limit, LimitRequestDTO> typeMap) {
        typeMap.addMappings(mapper -> {
            mapper.map(Limit::getLimitSum, LimitRequestDTO::setLimit_sum);
            mapper.map(Limit::getLimitCurrencyShortname, LimitRequestDTO::setLimit_currency_shortname);
            mapper.using(Converters.expenseCategoryTypeStringConverter()).map(Limit::getLimitExpenseCategory, LimitRequestDTO::setLimit_expense_category);
        });
    }

    @Override
    protected void configureReverseTypeMap(TypeMap<LimitRequestDTO, Limit> reverseTypeMap) {
        reverseTypeMap.addMappings(mapper -> {
            mapper.using(Converters.doubleToDecimalConverter()).map(LimitRequestDTO::getLimit_sum, Limit::setLimitSum);
            mapper.using(Converters.stringCurrencyTypeConverter()).map(LimitRequestDTO::getLimit_currency_shortname, Limit::setLimitCurrencyShortname);
            mapper.using(Converters.stringExpenseCategoryTypeConverter()).map(LimitRequestDTO::getLimit_expense_category, Limit::setLimitExpenseCategory);
        });
    }

}

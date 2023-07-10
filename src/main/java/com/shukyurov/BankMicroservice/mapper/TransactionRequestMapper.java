package com.shukyurov.BankMicroservice.mapper;

import com.shukyurov.BankMicroservice.model.dto.TransactionRequestDTO;
import com.shukyurov.BankMicroservice.model.entity.Transaction;
import com.shukyurov.BankMicroservice.util.Converters;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

@Component
public class TransactionRequestMapper extends AbstractMapper<Transaction, TransactionRequestDTO> {

    public TransactionRequestMapper() {
        super(new ModelMapper(), Transaction.class, TransactionRequestDTO.class);
    }

    @Override
    public TransactionRequestDTO toDto(Transaction entity) {
        return getTypeMap().map(entity);
    }

    @Override
    public Transaction toEntity(TransactionRequestDTO dto) {
        return getReverseTypeMap().map(dto);
    }

    @Override
    protected Class<Transaction> getEntityClass() {
        return Transaction.class;
    }

    @Override
    protected Class<TransactionRequestDTO> getDtoClass() {
        return TransactionRequestDTO.class;
    }

    @Override
    protected void configureTypeMap(TypeMap<Transaction, TransactionRequestDTO> typeMap) {
        typeMap.addMappings(mapper -> {
            mapper.map(Transaction::getAccountFrom, TransactionRequestDTO::setAccount_from);
            mapper.map(Transaction::getAccountTo, TransactionRequestDTO::setAccount_to);
            mapper.map(Transaction::getCurrencyShortname, TransactionRequestDTO::setCurrency_shortname);
            mapper.using(Converters.expenseCategoryTypeStringConverter()).map(Transaction::getExpenseCategory, TransactionRequestDTO::setExpense_category);
        });
    }

    @Override
    protected void configureReverseTypeMap(TypeMap<TransactionRequestDTO, Transaction> reverseTypeMap) {
        reverseTypeMap.addMappings(mapper -> {
            mapper.map(TransactionRequestDTO::getAccount_from, Transaction::setAccountFrom);
            mapper.map(TransactionRequestDTO::getAccount_to, Transaction::setAccountTo);
            mapper.using(Converters.doubleToDecimalConverter()).map(TransactionRequestDTO::getSum, Transaction::setSum);
            mapper.using(Converters.stringCurrencyTypeConverter()).map(TransactionRequestDTO::getCurrency_shortname, Transaction::setCurrencyShortname);
            mapper.using(Converters.stringExpenseCategoryTypeConverter()).map(TransactionRequestDTO::getExpense_category, Transaction::setExpenseCategory);
        });
    }

}

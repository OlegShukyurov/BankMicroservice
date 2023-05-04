package com.shukyurov.BankMicroservice.mapper;

import com.shukyurov.BankMicroservice.model.dto.TransactionDTO;
import com.shukyurov.BankMicroservice.model.entity.Transaction;
import com.shukyurov.BankMicroservice.util.Converters;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper extends AbstractMapper<Transaction, TransactionDTO> {

    public TransactionMapper() {
        super(new ModelMapper(), Transaction.class, TransactionDTO.class);
    }

    @Override
    public TransactionDTO toDto(Transaction entity) {
        return getTypeMap().map(entity);
    }

    @Override
    public Transaction toEntity(TransactionDTO dto) {
        return getReverseTypeMap().map(dto);
    }

    @Override
    protected Class<Transaction> getEntityClass() {
        return Transaction.class;
    }

    @Override
    protected Class<TransactionDTO> getDtoClass() {
        return TransactionDTO.class;
    }

    @Override
    protected void configureTypeMap(TypeMap<Transaction, TransactionDTO> typeMap) {
        typeMap.addMappings(mapper -> {
            mapper.map(Transaction::getAccountFrom, TransactionDTO::setAccount_from);
            mapper.map(Transaction::getAccountTo, TransactionDTO::setAccount_to);
            mapper.map(Transaction::getCurrencyShortname, TransactionDTO::setCurrency_shortname);
            mapper.using(Converters.expenseCategoryTypeStringConverter()).map(Transaction::getExpenseCategory, TransactionDTO::setExpense_category);
        });
    }

    @Override
    protected void configureReverseTypeMap(TypeMap<TransactionDTO, Transaction> reverseTypeMap) {
        reverseTypeMap.addMappings(mapper -> {
            mapper.map(TransactionDTO::getAccount_from, Transaction::setAccountFrom);
            mapper.map(TransactionDTO::getAccount_to, Transaction::setAccountTo);
            mapper.using(Converters.doubleToDecimalConverter()).map(TransactionDTO::getSum, Transaction::setSum);
            mapper.using(Converters.stringCurrencyTypeConverter()).map(TransactionDTO::getCurrency_shortname, Transaction::setCurrencyShortname);
            mapper.using(Converters.stringExpenseCategoryTypeConverter()).map(TransactionDTO::getExpense_category, Transaction::setExpenseCategory);
        });
    }

}

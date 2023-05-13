package com.shukyurov.BankMicroservice.mapper;

import com.shukyurov.BankMicroservice.model.CurrencyType;
import com.shukyurov.BankMicroservice.model.ExpenseCategoryType;
import com.shukyurov.BankMicroservice.model.dto.TransactionResponseDTO;
import com.shukyurov.BankMicroservice.model.entity.Transaction;
import com.shukyurov.BankMicroservice.util.Converters;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class TransactionResponseMapper extends AbstractMapper<Transaction, TransactionResponseDTO> {

    public TransactionResponseMapper() {
        super(Transaction.class, TransactionResponseDTO.class);
    }

    @Override
    public TransactionResponseDTO toDto(Transaction entity) {
        return getTypeMap().map(entity);
    }

    @Override
    public Transaction toEntity(TransactionResponseDTO dto) {
        return getReverseTypeMap().map(dto);
    }

    @Override
    protected Class<Transaction> getEntityClass() {
        return Transaction.class;
    }

    @Override
    protected Class<TransactionResponseDTO> getDtoClass() {
        return TransactionResponseDTO.class;
    }

    @Override
    protected void configureTypeMap(TypeMap<Transaction, TransactionResponseDTO> typeMap) {
        typeMap.addMappings(mapper -> {
            mapper.map(Transaction::getAccountFrom, TransactionResponseDTO::setAccount_from);
            mapper.map(Transaction::getAccountTo, TransactionResponseDTO::setAccount_to);
            mapper.map(Transaction::getCurrencyShortname, TransactionResponseDTO::setCurrency_shortname);
            mapper.using(Converters.expenseCategoryTypeStringConverter()).map(Transaction::getExpenseCategory, TransactionResponseDTO::setExpense_category);
            mapper.map(Transaction::getTransactionLimit, TransactionResponseDTO::setLimit);
            mapper.map(src -> src.getTransactionLimit().getLimitSum(), (dest, v) -> dest.getLimit().setLimit_sum((Double) v));
            mapper.map(src -> src.getTransactionLimit().getLimitCurrencyShortname(), (dest, v) -> dest.getLimit().setLimit_currency_shortname((String) v));
            mapper.map(src -> src.getTransactionLimit().getLimitDateTime(), (dest, v) -> dest.getLimit().setLimit_date_time((LocalDateTime) v));
            mapper.using(Converters.expenseCategoryTypeStringConverter()).map(src -> src.getTransactionLimit().getLimitExpenseCategory(),
                    (dest, v) -> dest.getLimit().setLimit_expense_category((String) v));
        });
    }

    @Override
    protected void configureReverseTypeMap(TypeMap<TransactionResponseDTO, Transaction> reverseTypeMap) {
        reverseTypeMap.addMappings(mapper -> {
            mapper.map(TransactionResponseDTO::getAccount_from, Transaction::setAccountFrom);
            mapper.map(TransactionResponseDTO::getAccount_to, Transaction::setAccountTo);
            mapper.using(Converters.doubleToDecimalConverter()).map(TransactionResponseDTO::getSum, Transaction::setSum);
            mapper.using(Converters.stringCurrencyTypeConverter()).map(TransactionResponseDTO::getCurrency_shortname, Transaction::setCurrencyShortname);
            mapper.using(Converters.stringExpenseCategoryTypeConverter()).map(TransactionResponseDTO::getExpense_category, Transaction::setExpenseCategory);
            mapper.map(TransactionResponseDTO::getLimit, Transaction::setTransactionLimit);
            mapper.map(src -> src.getLimit().getLimit_date_time(), Transaction::setDatetime);
            mapper.using(Converters.doubleToDecimalConverter()).map(src -> src.getLimit().getLimit_sum(),
                    (dest, v) -> dest.getTransactionLimit().setLimitSum((BigDecimal) v));
            mapper.using(Converters.stringCurrencyTypeConverter()).map(src -> src.getLimit().getLimit_currency_shortname(),
                    (dest, v) -> dest.getTransactionLimit().setLimitCurrencyShortname((CurrencyType) v));
            mapper.using(Converters.expenseCategoryTypeStringConverter()).map(src -> src.getLimit().getLimit_expense_category(),
                    (dest, v) -> dest.getTransactionLimit().setLimitExpenseCategory((ExpenseCategoryType) v));
        });
    }

}

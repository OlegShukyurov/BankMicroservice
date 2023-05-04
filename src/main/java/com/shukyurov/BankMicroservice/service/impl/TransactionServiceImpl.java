package com.shukyurov.BankMicroservice.service.impl;

import com.shukyurov.BankMicroservice.mapper.TransactionMapper;
import com.shukyurov.BankMicroservice.model.dto.TransactionDTO;
import com.shukyurov.BankMicroservice.model.entity.Transaction;
import com.shukyurov.BankMicroservice.service.ClientService;
import com.shukyurov.BankMicroservice.service.LimitService;
import com.shukyurov.BankMicroservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionMapper transactionMapper;
    private final ClientService clientService;
    private final LimitService limitService;

    @Override
    @Transactional
    public TransactionDTO addTransaction(TransactionDTO transactionDTO) {
        Transaction currentTransaction = transactionMapper.toEntity(transactionDTO);
        Transaction savedTransaction = clientService.addTransactionToClient(currentTransaction);
        limitService.updateTransactionLimit(savedTransaction);
        updateLimitExceeded(savedTransaction);

        return transactionMapper.toDto(savedTransaction);
    }

    private void updateLimitExceeded(Transaction transaction) {
        BigDecimal remainingMonthLimit = transaction.getTransactionLimit().getRemainingMonthLimit();
        if (remainingMonthLimit.compareTo(BigDecimal.ZERO) < 0) {
            transaction.setLimitExceeded(true);
        }
    }

}

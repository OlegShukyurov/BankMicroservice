package com.shukyurov.BankMicroservice.service.impl;

import com.shukyurov.BankMicroservice.exception.LimitExceededException;
import com.shukyurov.BankMicroservice.mapper.TransactionRequestMapper;
import com.shukyurov.BankMicroservice.mapper.TransactionResponseMapper;
import com.shukyurov.BankMicroservice.model.CurrencyType;
import com.shukyurov.BankMicroservice.model.ExpenseCategoryType;
import com.shukyurov.BankMicroservice.model.dto.TransactionRequestDTO;
import com.shukyurov.BankMicroservice.model.dto.TransactionResponseDTO;
import com.shukyurov.BankMicroservice.model.entity.Client;
import com.shukyurov.BankMicroservice.model.entity.Transaction;
import com.shukyurov.BankMicroservice.repository.TransactionRepository;
import com.shukyurov.BankMicroservice.service.ClientService;
import com.shukyurov.BankMicroservice.service.LimitService;
import com.shukyurov.BankMicroservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final ClientService clientService;
    private final LimitService limitService;
    private final TransactionRepository transactionRepository;
    private final TransactionRequestMapper transactionRequestMapper;
    private final TransactionResponseMapper transactionResponseMapper;

    @Override
    @Transactional(noRollbackFor = LimitExceededException.class)
    public TransactionRequestDTO addTransaction(TransactionRequestDTO transactionRequestDTO) {
        Transaction currentTransaction = transactionRequestMapper.toEntity(transactionRequestDTO);
        Transaction savedTransaction = clientService.addTransactionToClient(currentTransaction);
        limitService.updateTransactionLimit(savedTransaction);
        updateLimitExceeded(savedTransaction);

        return transactionRequestMapper.toDto(savedTransaction);
    }

    @Override
    public List<TransactionResponseDTO> getAllLimitExceededTransactions(String bankAccountNumber, String currency, String expense) {
        Client client = clientService.getClientByBankAccountNumber(bankAccountNumber);
        List<Transaction> limitExceededTransactionList;
        boolean currencyIsCorrect = Arrays.stream(CurrencyType.values())
                .anyMatch(ct -> Objects.equals(ct.getCurrencyType(), currency));
        boolean expenseIsCorrect = Arrays.stream(ExpenseCategoryType.values())
                .anyMatch(ect -> Objects.equals(ect.getExpenseCategoryType(), expense));

        if (currencyIsCorrect && expenseIsCorrect) {
            limitExceededTransactionList = transactionRepository.findAllLimitExceededTransactions(client, CurrencyType.valueOf(currency),
                    ExpenseCategoryType.valueOf(expense.toUpperCase()));
        } else if (currencyIsCorrect) {
            limitExceededTransactionList = transactionRepository.findAllLimitExceededTransactions(client, CurrencyType.valueOf(currency));
        } else if (expenseIsCorrect) {
            limitExceededTransactionList = transactionRepository.findAllLimitExceededTransactions(client, ExpenseCategoryType.valueOf(expense.toUpperCase()));
        } else {
            limitExceededTransactionList = transactionRepository.findAllLimitExceededTransactions(client);
        }

        return limitExceededTransactionList.stream().map(transactionResponseMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponseDTO> getAllTransactions(String bankAccountNumber) {
        return transactionRepository.findAllByTransactionClient(clientService.getClientByBankAccountNumber(bankAccountNumber))
                .stream()
                .map(transactionResponseMapper::toDto)
                .collect(Collectors.toList());
    }

    private void updateLimitExceeded(Transaction transaction) {
        BigDecimal remainingMonthLimit = transaction.getTransactionLimit().getRemainingMonthLimit();
        if (remainingMonthLimit.compareTo(BigDecimal.ZERO) < 0) {
            transaction.setLimitExceeded(true);
            throw new LimitExceededException(remainingMonthLimit.toString());
        }
    }

}

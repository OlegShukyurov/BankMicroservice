package com.shukyurov.BankMicroservice.service;

import com.shukyurov.BankMicroservice.model.dto.TransactionRequestDTO;
import com.shukyurov.BankMicroservice.model.dto.TransactionResponseDTO;

import java.util.List;

public interface TransactionService {

    TransactionRequestDTO addTransaction(TransactionRequestDTO transactionRequestDTO);

    List<TransactionResponseDTO> getAllTransactions(String bankAccountNumber);

    List<TransactionResponseDTO> getAllLimitExceededTransactions(String bankAccountNumber, String currency, String expense);

}

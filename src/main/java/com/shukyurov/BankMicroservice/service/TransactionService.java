package com.shukyurov.BankMicroservice.service;

import com.shukyurov.BankMicroservice.model.dto.TransactionRequestDTO;
import com.shukyurov.BankMicroservice.model.dto.TransactionResponseDTO;

import java.util.List;

public interface TransactionService {

    TransactionRequestDTO addTransaction(TransactionRequestDTO transactionRequestDTO);

    List<TransactionResponseDTO> getAll(String bankAccountNumber);

    List<TransactionResponseDTO> getAllLimitExceeded(String bankAccountNumber, String currency, String expense);

}

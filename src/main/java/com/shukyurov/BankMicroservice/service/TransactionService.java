package com.shukyurov.BankMicroservice.service;

import com.shukyurov.BankMicroservice.model.dto.TransactionDTO;

public interface TransactionService {

    TransactionDTO addTransaction(TransactionDTO transactionDTO);

}

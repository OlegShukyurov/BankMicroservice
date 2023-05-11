package com.shukyurov.BankMicroservice.service;

import com.shukyurov.BankMicroservice.model.dto.LimitRequestDTO;
import com.shukyurov.BankMicroservice.model.dto.LimitResponseDTO;
import com.shukyurov.BankMicroservice.model.entity.Transaction;

import java.util.List;

public interface LimitService {

    void updateTransactionLimit(Transaction transaction);

    LimitRequestDTO addLimitByBankAccountNumber(String bankAccountNumber, LimitRequestDTO limitRequestDTO);

    LimitResponseDTO increaseLimitByBankAccountNumber(String bankAccountNumber, String expense, Double sum);

    List<LimitResponseDTO> getAllLimitsByBankAccountNumber(String bankAccountNumber, String expense);

}

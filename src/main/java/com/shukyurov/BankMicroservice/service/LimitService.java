package com.shukyurov.BankMicroservice.service;

import com.shukyurov.BankMicroservice.model.dto.LimitRequestDTO;
import com.shukyurov.BankMicroservice.model.entity.Transaction;

public interface LimitService {

    void updateTransactionLimit(Transaction transaction);

    LimitRequestDTO addLimitByBankAccountNumber(String bankAccountNumber, LimitRequestDTO limitRequestDTO);

    void updateAllLastLimits();

}

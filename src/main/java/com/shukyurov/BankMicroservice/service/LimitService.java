package com.shukyurov.BankMicroservice.service;

import com.shukyurov.BankMicroservice.model.dto.LimitDTO;
import com.shukyurov.BankMicroservice.model.entity.Transaction;

public interface LimitService {

    void updateTransactionLimit(Transaction transaction);

    LimitDTO addLimitByBankAccountNumber(String bankAccountNumber, LimitDTO limitDTO);

    void updateAllLastLimits();

}

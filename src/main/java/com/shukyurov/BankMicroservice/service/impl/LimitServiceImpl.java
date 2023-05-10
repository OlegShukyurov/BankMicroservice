package com.shukyurov.BankMicroservice.service.impl;

import com.shukyurov.BankMicroservice.exception.ResourceNotFoundException;
import com.shukyurov.BankMicroservice.mapper.LimitRequestMapper;
import com.shukyurov.BankMicroservice.model.ExpenseCategoryType;
import com.shukyurov.BankMicroservice.model.dto.LimitRequestDTO;
import com.shukyurov.BankMicroservice.model.entity.Client;
import com.shukyurov.BankMicroservice.model.entity.Limit;
import com.shukyurov.BankMicroservice.model.entity.Transaction;
import com.shukyurov.BankMicroservice.repository.ClientRepository;
import com.shukyurov.BankMicroservice.repository.LimitRepository;
import com.shukyurov.BankMicroservice.service.ClientService;
import com.shukyurov.BankMicroservice.service.ConversionService;
import com.shukyurov.BankMicroservice.service.LimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LimitServiceImpl implements LimitService {

    private final LimitRepository limitRepository;
    private final ClientService clientService;
    private final LimitRequestMapper limitRequestMapper;
    private final ConversionService conversionService;
    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public void updateTransactionLimit(Transaction transaction) {
        Limit lastLimit = limitRepository.findLastLimitByClientIdAndExpenseCategory(transaction.getTransactionClient().getId(),
                transaction.getExpenseCategory().toString())
                .orElseThrow(() -> new ResourceNotFoundException("Limit", "clientId and expenseCategory",
                transaction.getTransactionClient().getId() + " and " + transaction.getExpenseCategory().getExpenseCategoryType()));

        BigDecimal lastExchangeRate = conversionService.getLastExchangeRate(lastLimit.getLimitCurrencyShortname().getCurrencyType(),
                transaction.getCurrencyShortname().getCurrencyType());
        updateRemainingMonthLimit(lastLimit, lastExchangeRate, transaction.getSum());
        lastLimit.getLimitTransactions().add(transaction);
        transaction.setTransactionLimit(lastLimit);
    }

    @Override
    @Transactional
    public LimitRequestDTO addLimitByBankAccountNumber(String bankAccountNumber, LimitRequestDTO limitRequestDTO) {
        Limit currentLimit = limitRequestMapper.toEntity(limitRequestDTO);
        enrichLimit(currentLimit);
        Limit savedLimit = clientService.addLimitToClient(bankAccountNumber, currentLimit);

        return limitRequestMapper.toDto(savedLimit);
    }

    @Override
    @Transactional
    @Scheduled(cron = "${spring.client.cron}")
    public void updateAllLastLimits() {
        System.out.println("Update limits start");
        List<Client> clients = clientRepository.findAll().stream().collect(Collectors.toList());
        clients.forEach(client -> {
            Arrays.stream(ExpenseCategoryType.values()).forEach(expenseCategoryType -> {
                Optional<Limit> lastLimit = limitRepository.findLastLimitByClientIdAndExpenseCategory(client.getId(), expenseCategoryType.toString());
                lastLimit.ifPresent(this::updateLimitDateTime);
            });
        });
        System.out.println("Update limits end");
    }

    private void updateLimitDateTime(Limit limit) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime before = limit.getLimitDateTime();
        if (before.plusDays(30L).isBefore(now)) {
            limit.setLimitDateTime(now);
        }
    }

    private void updateRemainingMonthLimit(Limit limit, BigDecimal lastExchangeRate, BigDecimal transactionSum) {
        if (limit.getLimitSum().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal newRemainingMonthLimit = limit.getRemainingMonthLimit().subtract(transactionSum.divide(lastExchangeRate, 2, RoundingMode.HALF_UP));
            limit.setRemainingMonthLimit(newRemainingMonthLimit);
        }
    }

    private void enrichLimit(Limit limit) {
        limit.setLimitDateTime(LocalDateTime.now());
        limit.setRemainingMonthLimit(limit.getLimitSum());
        limit.setLimitTransactions(new ArrayList<>());
    }

}

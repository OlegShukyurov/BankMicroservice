package com.shukyurov.BankMicroservice.service.impl;

import com.shukyurov.BankMicroservice.mapper.LimitMapper;
import com.shukyurov.BankMicroservice.model.CurrencyType;
import com.shukyurov.BankMicroservice.model.dto.LimitDTO;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LimitServiceImpl implements LimitService {

    private final LimitRepository limitRepository;
    private final ClientService clientService;
    private final LimitMapper limitMapper;
    private final ConversionService conversionService;
    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public void updateTransactionLimit(Transaction transaction) {
        Optional<Limit> lastLimit = limitRepository.findTopByLimitClientOrderByLimitDateTimeDesc(transaction.getTransactionClient());
        lastLimit.ifPresentOrElse(limit -> {
            BigDecimal lastExchangeRate = conversionService.getLastExchangeRate(limit.getLimitCurrencyShortname().getCurrencyType(),
                    transaction.getCurrencyShortname().getCurrencyType());
            updateRemainingMonthLimit(limit, lastExchangeRate, transaction.getSum());
            limit.getLimitTransactions().add(transaction);
            transaction.setTransactionLimit(limit);
        }, () -> {
            Limit defaultLimit = createDefaultLimit();
            Client transactionClient = transaction.getTransactionClient();
            transactionClient.getClientLimits().add(defaultLimit);
            defaultLimit.setLimitClient(transactionClient);
            transaction.setTransactionLimit(defaultLimit);
            defaultLimit.getLimitTransactions().add(transaction);
        });
    }

    @Override
    @Transactional
    public LimitDTO addLimitByBankAccountNumber(String bankAccountNumber, LimitDTO limitDTO) {
        Limit currentLimit = limitMapper.toEntity(limitDTO);
        enrichLimit(currentLimit);
        Limit savedLimit = clientService.addLimitToClient(bankAccountNumber, currentLimit);
        return limitMapper.toDto(savedLimit);
    }

    @Override
    @Transactional
    @Scheduled(cron = "${spring.client.cron}")
    public void updateAllLastLimits() {
        System.out.println("Update limits start");
        List<Client> clients = clientRepository.findAll().stream().collect(Collectors.toList());
        clients.stream().forEach(client -> {
            Optional<Limit> lastLimit = limitRepository.findTopByLimitClientOrderByLimitDateTimeDesc(client);
            lastLimit.ifPresent(this::updateLimitDateTime);
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
        if (limit.getLimitSum().compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        limit.setRemainingMonthLimit(limit.getRemainingMonthLimit().subtract(transactionSum.divide(lastExchangeRate, 2, RoundingMode.HALF_UP)));
    }

    private void enrichLimit(Limit limit) {
        limit.setLimitDateTime(LocalDateTime.now());
        limit.setRemainingMonthLimit(limit.getLimitSum());
        limit.setLimitTransactions(new ArrayList<>());
    }

    private Limit createDefaultLimit() {
        Limit defaultLimit = new Limit();
        defaultLimit.setLimitSum(BigDecimal.ZERO);
        defaultLimit.setLimitCurrencyShortname(CurrencyType.USD);
        enrichLimit(defaultLimit);
        limitRepository.save(defaultLimit);

        return defaultLimit;
    }

}

package com.shukyurov.BankMicroservice.service.impl;

import com.shukyurov.BankMicroservice.client.ConversionClient;
import com.shukyurov.BankMicroservice.exception.ExchangeRateException;
import com.shukyurov.BankMicroservice.mapper.ConversionMapper;
import com.shukyurov.BankMicroservice.model.ExchangeType;
import com.shukyurov.BankMicroservice.model.dto.ConversionDTO;
import com.shukyurov.BankMicroservice.model.entity.Conversion;
import com.shukyurov.BankMicroservice.repository.ConversionRepository;
import com.shukyurov.BankMicroservice.service.ConversionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversionServiceImpl implements ConversionService {

    @Value("${spring.client.apikey}")
    private String apiKey;

    private final ConversionMapper conversionMapper;
    private final ConversionClient conversionClient;
    private final ConversionRepository conversionRepository;

    @Override
    public ConversionDTO getConversionDTO(String symbol, String apikey) {
        ConversionDTO conversionDTO = conversionClient.getConversionDTO(symbol, apiKey);
        if (conversionDTO == null) {
            throw new ExchangeRateException();
        }
        return conversionDTO;
    }

    @Override
    public BigDecimal getLastExchangeRate(String limitCurrencyType, String transactionCurrencyType) {
        ExchangeType symbol = ExchangeType.valueOf(limitCurrencyType + "_" + transactionCurrencyType);
        Optional<Conversion> conversion = findTopBySymbolOrderByMadeAtDesc(symbol);
        if (conversion.isPresent()) {
            return conversion.get().getRate();
        }
        return BigDecimal.valueOf(getConversionDTO(symbol.getSymbol(), apiKey).getRate()).setScale(2, RoundingMode.HALF_UP);
    }

    @PostConstruct
    @Scheduled(cron = "${spring.client.cron}")
    public void saveAllConversions() {
        System.out.println("Save all starts");
        Arrays.stream(ExchangeType.values())
                .map(exchangeType -> getConversionDTO(exchangeType.getSymbol(), apiKey))
                .map(conversionMapper::toEntity)
                .peek(this::enrichConversion)
                .forEachOrdered(conversionRepository::save);
        System.out.println("Save all ends");
    }

    private void enrichConversion(Conversion conversion) {
        conversion.setId(UUID.randomUUID());
        conversion.setRateOnPreviousClose(findPreviousRate(conversion));
    }

    private BigDecimal findPreviousRate(Conversion conversion) {
        return findTopBySymbolOrderByMadeAtDesc(conversion.getSymbol()).orElse(conversion).getRate();
    }

    private Optional<Conversion> findTopBySymbolOrderByMadeAtDesc(ExchangeType symbol) {
        return conversionRepository.findTopBySymbolOrderByMadeAtDesc(symbol);
    }

}

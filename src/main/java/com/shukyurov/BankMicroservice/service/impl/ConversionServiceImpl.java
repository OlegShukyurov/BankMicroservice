package com.shukyurov.BankMicroservice.service.impl;

import com.shukyurov.BankMicroservice.client.ConversionClient;
import com.shukyurov.BankMicroservice.mapper.ConversionMapper;
import com.shukyurov.BankMicroservice.model.ExchangeType;
import com.shukyurov.BankMicroservice.model.dto.ConversionDTO;
import com.shukyurov.BankMicroservice.model.entity.Conversion;
import com.shukyurov.BankMicroservice.repository.ConversionRepository;
import com.shukyurov.BankMicroservice.service.ConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConversionServiceImpl implements ConversionService {

    @Value("${spring.client.apikey}")
    private String apiKey;

    private final ConversionMapper conversionMapper;
    private final ConversionClient conversionClient;
    private final ConversionRepository conversionRepository;

    @Autowired
    public ConversionServiceImpl(ConversionMapper conversionMapper, ConversionClient conversionClient,
                                 ConversionRepository conversionRepository) {
        this.conversionMapper = conversionMapper;
        this.conversionClient = conversionClient;
        this.conversionRepository = conversionRepository;
    }

    @Override
    public ConversionDTO getConversionDTO(String symbol, String apikey) {
        return conversionClient.getConversionDTO(symbol, apikey);
    }

    @PostConstruct
    @Scheduled(cron = "${spring.client.cron}")
    private void saveAllConversions() {
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

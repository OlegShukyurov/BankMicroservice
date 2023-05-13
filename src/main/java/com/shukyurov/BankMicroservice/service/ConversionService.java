package com.shukyurov.BankMicroservice.service;

import com.shukyurov.BankMicroservice.model.dto.ConversionDTO;

import java.math.BigDecimal;

public interface ConversionService {

    ConversionDTO getConversionDTO(String symbol, String apikey);

    BigDecimal getLastExchangeRate(String limitCurrencyType, String transactionCurrencyType);

}
